package org.example.backend.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import jakarta.servlet.http.HttpServletRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;



public class Tools {

    public static JSONObject getRequestJsonData(HttpServletRequest request) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        JSONObject json = JSON.parseObject(jsonBuilder.toString());
        return json;
    }

    // 判断指定字段是否为空，如果是字符串是否是空字符串
    public static boolean areRequestFieldNull(JSONObject jsonData, List<String> fieldNames){
        if (jsonData == null || fieldNames == null || fieldNames.isEmpty()) {
            return true;
        }

        for (String fieldName : fieldNames) {
            Object value = jsonData.get(fieldName);
            if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                return true;
            }
        }
        return false;
    }

    public static String maskString(String str){   // 中间用*代替
        String str1 = str.substring(0,3);
        String str2 = str.substring(14,18);
        return str1+ "***********" +str2;
    }

    public static String QRCode(String text, int status){
        int size = 250; // QR Code 宽度

        try {
            // 设置编码参数
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 设置字符编码
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 设置纠错级别 (L, M, Q, H)
            hints.put(EncodeHintType.MARGIN, 0);    // 设置边距（二维码周围的留白）

            // 使用 QRCodeWriter 创建 BitMatrix
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size, hints);

            int purpleColor = status==1 ? 0xFF800080 : 0xFFAAAAAA;  //  紫色/浅灰色 (ARGB 格式)
            int backgroundColor = 0xFFFFFFFF; // 白色 (ARGB 格式)
            MatrixToImageConfig config = new MatrixToImageConfig(purpleColor, backgroundColor);

            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix,config);
            if(status==0)   addProhibitionSymbol(qrImage,size);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", byteArrayOutputStream);

            String base64Image = java.util.Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            return base64Image;

        } catch (WriterException e) {
            System.err.println("生成 QR Code 失败: " + e.getMessage());
            return "";
        } catch (IOException e) {
            System.err.println("写入图像文件失败: " + e.getMessage());
            return "";
        }
    }

    private static void addProhibitionSymbol(BufferedImage qrImage, int size) {
        Graphics2D g = qrImage.createGraphics();    // 使用Graphics2D来绘制禁止符号
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int thickness = 28; // 线条厚度
        g.setStroke(new BasicStroke(thickness));
        g.setColor(new Color(100, 100, 100, 255)); // 半透明灰色

        // 画圆圈
        g.drawOval(thickness / 2, thickness / 2, size - thickness, size - thickness);

        // 画斜杠
        double angle = Math.toRadians(45); // 斜对角
        double radius = (size - thickness) / 2.0;
        int center = size / 2;

        int x1 = (int) (center - radius * Math.cos(angle));
        int y1 = (int) (center - radius * Math.sin(angle));
        int x2 = (int) (center + radius * Math.cos(angle));
        int y2 = (int) (center + radius * Math.sin(angle));

        g.drawLine(x1, y1, x2, y2);
        g.dispose();
    }

    public static boolean isValidIDCard(String idCard) {
        // 正则表达式匹配15位或18位身份证号码
        String regex = "^(\\d{6})(\\d{4})(\\d{2})(\\d{2})(\\d{3})([0-9]|X)$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(idCard).matches();
    }

    public static boolean isValidPhone(String phone) {
        // 正则表达式匹配
        String regex = "^1[3-9]\\d{9}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(phone).matches();
    }

    public static boolean isValidPlate_number(String plate_number) {
        // 正则表达式匹配
        String regex = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5})|([A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳]))$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(plate_number).matches();
    }
}
