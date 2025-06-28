package org.example.backend.control.appointment;

import cn.hutool.crypto.SmUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.annotation.WebServlet;

import org.example.backend.dao.AppointmentDAO;
import org.example.backend.model.AppointmentBean;

import org.example.backend.model.AppointmentPersonBean;
import org.example.backend.utils.Tools;

@WebServlet("/api/appointment/add")
public class AddAppointment extends HttpServlet {
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
        JSONObject jsonData = Tools.getRequestJsonData(request);
        List<String> fieldNames = Arrays.asList("isPublic", "campus", "entry_time", "end_time", "organization", "full_name", "id_number", "phone", "transport_mode");

        System.out.println(jsonData);

        AppointmentDAO appointmentDAO = new AppointmentDAO();

        try{
            appointmentDAO.lookupConnection();  // 从数据源中获取连接对象
            Connection dbconn = appointmentDAO.getConnection();     // 获取连接对象，通过事务来同步
            dbconn.setAutoCommit(false);

            // 开始判断字段是否为空
            if(Tools.areRequestFieldNull(jsonData, fieldNames)){
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

            // 如果是驾车，判断车牌号是否为空
            if(transport_mode==1 && Tools.areRequestFieldNull(jsonData, Arrays.asList("plate_number"))){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            String plate_number = jsonData.getString("plate_number");

            // 如果是公务预约，是否还含有visiting_department、contact_person、visit_purpose这三个字段
            if(isPublic==0 && Tools.areRequestFieldNull(jsonData, Arrays.asList("visiting_department", "contact_person", "visit_purpose"))){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            String visiting_department = jsonData.getString("visiting_department");
            String contact_person = jsonData.getString("contact_person");
            String visit_purpose = jsonData.getString("visit_purpose");

            JSONArray entourages = jsonData.getJSONArray("entourages");

            // 判断内容合法
            if (
                    !(isPublic >= 0 && isPublic <= 1) || !(campus >= 0 && campus <= 2) || !(transport_mode >= 0 && transport_mode <= 1) ||
                            !Tools.isValidIDCard(id_number) || !Tools.isValidPhone(phone) || (transport_mode==1 && !Tools.isValidPlate_number(plate_number))
            ) {
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            // 遍历所有的随行人员，判断数据是否合法
            for(int i=0; i<entourages.size(); i++){
                JSONObject entourage = entourages.getJSONObject(i);
                if(Tools.areRequestFieldNull(entourage,List.of("full_name", "id_number", "phone")) || !Tools.isValidPhone(entourage.getString("phone")) || !Tools.isValidIDCard(entourage.getString("id_number"))){
                    throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
                }
            }

            // 创建预约记录Bean
            AppointmentBean appointmentBean = new AppointmentBean();

            appointmentBean.setCampus(campus);

            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = currentDate.format(formatter);

            appointmentBean.setApplication_time(formattedDate);
            appointmentBean.setEntry_time(entry_time);
            appointmentBean.setEnd_time(end_time);
            appointmentBean.setOrganization(organization);
            appointmentBean.setTransport_mode(transport_mode);
            appointmentBean.setPlate_number(plate_number);
            appointmentBean.setVisiting_department(visiting_department);
            appointmentBean.setContact_person(contact_person);
            appointmentBean.setVisit_purpose(visit_purpose);
            appointmentBean.setApproval_status(0);

            // 插入一个预约记录并获取插入的ID
            int generatedKeys = isPublic == 1 ? appointmentDAO.addPublicAppointment(appointmentBean) : appointmentDAO.addOfficialAppointment(appointmentBean);

            if(generatedKeys == -1) {
                dbconn.rollback();
                throw new Exception("{ \"code\": 421, \"msg\": \"数据库操作失败\", \"data\": { } }");
            }

            // 创建预约人员Bean
            AppointmentPersonBean appointmentPersonBean = new AppointmentPersonBean();

            appointmentPersonBean.setAppointment_id(generatedKeys);
            appointmentPersonBean.setFull_name(full_name);
            appointmentPersonBean.setId_number(SmUtil.sm3(id_number));
            appointmentPersonBean.setMask_id_number(Tools.maskString(id_number));
            appointmentPersonBean.setPhone(phone);
            appointmentPersonBean.setIs_applicant(1);

            // 插入预约人员表
            Boolean res = isPublic == 1 ? appointmentDAO.addPublicAppointmentPerson(appointmentPersonBean) : appointmentDAO.addOfficialAppointmentPerson(appointmentPersonBean);

            if(!res){
                dbconn.rollback();
                throw new Exception("{ \"code\": 421, \"msg\": \"数据库操作失败\", \"data\": { } }");
            }

            // 插入随行人员
            for(int i=0; i<entourages.size(); i++){
                JSONObject entourage = entourages.getJSONObject(i);

                appointmentPersonBean.setFull_name(entourage.getString("full_name"));
                appointmentPersonBean.setId_number(SmUtil.sm3(entourage.getString("id_number")));
                appointmentPersonBean.setMask_id_number(Tools.maskString(entourage.getString("id_number")));
                appointmentPersonBean.setPhone(entourage.getString("phone"));
                appointmentPersonBean.setIs_applicant(0);

                res = isPublic == 1 ? appointmentDAO.addPublicAppointmentPerson(appointmentPersonBean) : appointmentDAO.addOfficialAppointmentPerson(appointmentPersonBean);

                if(!res){
                    dbconn.rollback();
                    throw new Exception("{ \"code\": 421, \"msg\": \"数据库操作失败\", \"data\": { } }");
                }
            }

            // 由于使用了事务，需要手动commit
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
}
