package org.example.backend.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public static void QRCode(){
        String content = "abcdefg"; // 要编码的内容，可以是 URL、文本等
        String filePath = "C:\\Users\\31986\\Desktop\\output_qrcode.png"; // 输出文件路径
        int width = 300; // QR Code 宽度
        int height = 300; // QR Code 高度

        try {
            // 设置编码参数
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 设置字符编码
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 设置纠错级别 (L, M, Q, H)

            // 使用 QRCodeWriter 创建 BitMatrix
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // 将 BitMatrix 写入图像文件
            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

            System.out.println("QR Code 已成功生成并保存到: " + filePath);

        } catch (WriterException e) {
            System.err.println("生成 QR Code 失败: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("写入图像文件失败: " + e.getMessage());
        }
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
