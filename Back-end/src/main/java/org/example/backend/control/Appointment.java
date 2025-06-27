package org.example.backend.control;

import cn.hutool.crypto.SmUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.annotation.WebServlet;

import org.example.backend.dao.AppointmentDAO;
import org.example.backend.model.AppointmentBean;

import org.example.backend.model.AppointmentPersonBean;
import org.example.backend.utils.Tools;

@WebServlet("/api/appointment")
public class Appointment extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 读取请求的JSON格式数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        JSONObject jsonData = JSON.parseObject(jsonBuilder.toString());
        List<String> fieldNames = Arrays.asList("isPublic", "campus", "entry_time", "end_time", "organization", "full_name", "id_number", "phone", "transport_mode");

        System.out.println(jsonData);

        AppointmentDAO appointmentDAO = new AppointmentDAO();

        try{
            appointmentDAO.lookupConnection();  // 从数据源中获取连接对象
            Connection dbconn = appointmentDAO.getConnection();     // 获取连接对象，通过事务来同步
            dbconn.setAutoCommit(false);

            // 开始判断字段是否为空
            if(!Tools.areRequestFieldNull(jsonData, fieldNames)){
                System.out.println(1);
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            int isPublic = jsonData.getIntValue("isPublic");
            int campus = jsonData.getIntValue("campus");
            String entry_time = jsonData.getString("entry_time");
            String end_time = jsonData.getString("end_time");
            String organization = jsonData.getString("organization");
            String full_name = jsonData.getString("full_name");
            String id_number = jsonData.getString("id_number");
            String phone = jsonData.getString("phone");
            int transport_mode = jsonData.getIntValue("transport_mode");

            if(transport_mode==1 && !Tools.areRequestFieldNull(jsonData, Arrays.asList("plate_number"))){
                System.out.println(2);
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            String plate_number = jsonData.getString("plate_number");

            if(isPublic==0 && !Tools.areRequestFieldNull(jsonData, Arrays.asList("visiting_department", "contact_person", "visit_purpose"))){
                System.out.println(3);
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            String visiting_department = jsonData.getString("visiting_department");
            String contact_person = jsonData.getString("contact_person");
            String visit_purpose = jsonData.getString("visit_purpose");

            JSONArray entourages = jsonData.getJSONArray("entourages");

            // 判断内容合法
            if (
                    !(isPublic >= 0 && isPublic <= 1) || !(campus >= 0 && campus <= 2) || !(transport_mode >= 0 && transport_mode <= 1) ||
                            !isValidIDCard(id_number) || !isValidPhone(phone) || (transport_mode==1 && !isValidPlate_number(plate_number))
            ) {
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            for(int i=0; i<entourages.size(); i++){
                JSONObject entourage = entourages.getJSONObject(i);
                if(!isValidPhone(entourage.getString("phone")) || !isValidIDCard(entourage.getString("id_number"))){
                    throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
                }
            }

            AppointmentBean appointmentBean = new AppointmentBean();

            appointmentBean.setCampus(campus);
            appointmentBean.setEntry_time(entry_time);
            appointmentBean.setEnd_time(end_time);
            appointmentBean.setOrganization(organization);
            appointmentBean.setTransport_mode(transport_mode);
            appointmentBean.setPlate_number(plate_number);
            appointmentBean.setVisiting_department(visiting_department);
            appointmentBean.setContact_person(contact_person);
            appointmentBean.setVisit_purpose(visit_purpose);
            appointmentBean.setApproval_status(0);

            int generatedKeys = isPublic == 1 ? appointmentDAO.addPublicAppointment(appointmentBean) : appointmentDAO.addOfficialAppointment(appointmentBean);

            if(generatedKeys == -1) {
                dbconn.rollback();
                throw new Exception("{ \"code\": 430, \"msg\": \"数据库操作失败\", \"data\": { } }");
            }

            AppointmentPersonBean appointmentPersonBean = new AppointmentPersonBean();

            appointmentPersonBean.setAppointment_id(generatedKeys);
            appointmentPersonBean.setFull_name(full_name);
            appointmentPersonBean.setId_number(SmUtil.sm3(id_number));
            appointmentPersonBean.setMask_id_number(maskString(id_number));
            appointmentPersonBean.setPhone(phone);
            appointmentPersonBean.setIs_applicant(1);

            Boolean res = isPublic == 1 ? appointmentDAO.addPublicAppointmentPerson(appointmentPersonBean) : appointmentDAO.addOfficialAppointmentPerson(appointmentPersonBean);

            if(!res){
                dbconn.rollback();
                throw new Exception("{ \"code\": 430, \"msg\": \"数据库操作失败\", \"data\": { } }");
            }

            for(int i=0; i<entourages.size(); i++){
                JSONObject entourage = entourages.getJSONObject(i);

                appointmentPersonBean.setFull_name(entourage.getString("full_name"));
                appointmentPersonBean.setId_number(SmUtil.sm3(entourage.getString("id_number")));
                appointmentPersonBean.setMask_id_number(maskString(entourage.getString("id_number")));
                appointmentPersonBean.setPhone(entourage.getString("phone"));
                appointmentPersonBean.setIs_applicant(0);

                res = isPublic == 1 ? appointmentDAO.addPublicAppointmentPerson(appointmentPersonBean) : appointmentDAO.addOfficialAppointmentPerson(appointmentPersonBean);

                if(!res){
                    dbconn.rollback();
                    throw new Exception("{ \"code\": 430, \"msg\": \"数据库操作失败\", \"data\": { } }");
                }
            }

            dbconn.commit();
            appointmentDAO.releaseConnection();     // 释放连接对象
            String jsonResponse = "{ \"code\": 200, \"msg\": \"预约成功\", \"data\": { } }";
            out.print(jsonResponse);
        }catch (Exception e){
            try{
                appointmentDAO.releaseConnection();
            }catch (Exception e2){
                e2.printStackTrace();
            }
            out.print(e.getMessage());
        }
    }

    public String maskString(String str){   // 中间用*代替
        String str1 = str.substring(0,3);
        String str2 = str.substring(14,18);
        return str1+ "***********" +str2;
    }

    public boolean isValidIDCard(String idCard) {
        // 正则表达式匹配15位或18位身份证号码
        String regex = "^(\\d{6})(\\d{4})(\\d{2})(\\d{2})(\\d{3})([0-9]|X)$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(idCard).matches();
    }

    public boolean isValidPhone(String phone) {
        // 正则表达式匹配
        String regex = "^1[3-9]\\d{9}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(phone).matches();
    }

    public boolean isValidPlate_number(String plate_number) {
        // 正则表达式匹配
        String regex = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5})|([A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳]))$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(plate_number).matches();
    }
}
