package org.example.backend.control.appointment;

import cn.hutool.crypto.SmUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import jakarta.servlet.annotation.WebServlet;
import org.example.backend.dao.AppointmentDAO;
import org.example.backend.model.AppointmentBean;
import org.example.backend.model.AppointmentPersonBean;
import org.example.backend.utils.Tools;

@WebServlet("/api/appointment/passCode")
public class GetAppointmentStatus extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 读取请求的JSON格式数据
        JSONObject jsonData = Tools.getRequestJsonData(request);
        System.out.println(jsonData);

        List<String> fieldNames = Arrays.asList("isPublic", "appointment_person_id", "full_name", "id_number", "phone");

        AppointmentDAO appointmentDAO = new AppointmentDAO();

        try{
            appointmentDAO.lookupConnection();

            if(Tools.areRequestFieldNull(jsonData,fieldNames)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            // 获取请求数据
            int isPublic = jsonData.getIntValue("isPublic");
            int appointment_person_id = jsonData.getIntValue("appointment_person_id");
            String full_name = jsonData.getString("full_name");
            String id_number = jsonData.getString("id_number");
            String phone = jsonData.getString("phone");

            if(!Tools.isValidIDCard(id_number) || !Tools.isValidPhone(phone)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            // 获取预约人员信息
            AppointmentPersonBean appointmentPersonBean = isPublic == 1 ? appointmentDAO.findPublicAppointmentPersonOne(appointment_person_id) : appointmentDAO.findOfficialAppointmentPersonOne(appointment_person_id);
            if(appointmentPersonBean==null){
                throw new Exception("{ \"code\": 421, \"msg\": \"数据库操作失败\", \"data\": { } }");
            }

            // 判断信息是否匹配
            if(!(full_name.equals(appointmentPersonBean.getFull_name()) && SmUtil.sm3(id_number).equals(appointmentPersonBean.getId_number()) && phone.equals(appointmentPersonBean.getPhone()))){
                System.out.println(SmUtil.sm3(id_number));
                throw new Exception("{ \"code\": 422, \"msg\": \"信息不匹配\", \"data\": { } }");
            }

            // 获取预约信息
            int appointment_id = appointmentPersonBean.getAppointment_id();
            AppointmentBean appointmentBean = isPublic == 1 ? appointmentDAO.findPublicAppointment(appointment_id) : appointmentDAO.findOfficialAppointment(appointment_id);

            // 获取时间值
            Timestamp entry_Timestamp = Timestamp.valueOf(appointmentBean.getEntry_time());
            Timestamp end_Timestamp = Timestamp.valueOf(appointmentBean.getEnd_time());
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowString = dateFormat.format(now);
            String entryString = dateFormat.format(entry_Timestamp);
            String endString = dateFormat.format(end_Timestamp);

            // 二维码数据
            String info = appointmentPersonBean.getFull_name() + "|" + appointmentPersonBean.getMask_id_number() + "|" + "生成时间：" + nowString + "|" + "进校日期：" + entryString + "|" + "失效日期：" + endString;
            String base64Image;
            System.out.println(info);
            int effective = 0;

            // 判断是否在有效时间内
            if(now.before(end_Timestamp) && now.after(entry_Timestamp)){
                // 如果是公务，还需要审批通过
                if(isPublic==0 && appointmentBean.getApproval_status()!=2){
                    base64Image = Tools.QRCode(info, 0);
                }else{
                    base64Image = Tools.QRCode(info, 1);
                    effective = 1;
                }
            }else{
                base64Image = Tools.QRCode(info, 0);
            }

            // 响应数据
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("code", 200);
            jsonResponse.put("msg", "ok");

            JSONObject tmp = new JSONObject();
            tmp.put("base64_image", base64Image);
            tmp.put("generation_time", nowString);
            tmp.put("effective", effective);

            jsonResponse.put("data", tmp);

            appointmentDAO.releaseConnection();
            out.println(jsonResponse);
        }catch (Exception e){
            try{
                appointmentDAO.releaseConnection();
            }catch (Exception e2){
                e2.printStackTrace();
            }
            out.println(e.getMessage());
        }
    }
}
