package org.example.backend.control.appointment;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.annotation.WebServlet;
import org.example.backend.dao.AppointmentDAO;
import org.example.backend.model.AppointmentBean;
import org.example.backend.model.AppointmentPersonBean;
import org.example.backend.utils.Tools;

@WebServlet("/api/appointment/check")
public class CheckAppointment extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        JSONObject jsonData = Tools.getRequestJsonData(request);
        System.out.println(jsonData);

        List<String> fieldNames = Arrays.asList("full_name", "id_number", "phone");

        AppointmentDAO appointmentDAO = new AppointmentDAO();

        try{
            appointmentDAO.lookupConnection();

            if(Tools.areRequestFieldNull(jsonData, fieldNames)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            // 获取请求数据
            String full_Name = jsonData.getString("full_name");
            String id_Number = jsonData.getString("id_number");
            String phone = jsonData.getString("phone");

            if(!Tools.isValidIDCard(id_Number) || !Tools.isValidPhone(phone)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            // 查找预约人员记录
            ArrayList<AppointmentPersonBean> public_appointmentPersonBeans = appointmentDAO.findPublicAppointmentPerson(full_Name, id_Number, phone);
            ArrayList<AppointmentPersonBean> official_appointmentPersonBeans = appointmentDAO.findOfficialAppointmentPerson(full_Name,id_Number,phone);

            if(public_appointmentPersonBeans==null || official_appointmentPersonBeans==null){
                throw new Exception("{ \"code\": 421, \"msg\": \"数据库操作失败\", \"data\": { } }");
            }

            // 保存每一个预约记录（一个记录包括预约信息、自己、随行人员）
            JSONArray appointmentRecords = new JSONArray();

            // 遍历每一个预约人员记录，获取预约的相关信息
            for (AppointmentPersonBean publicAppointmentPersonBean : public_appointmentPersonBeans) {
                JSONObject appointmentRecord = new JSONObject();
                appointmentRecord.put("appointment_person", publicAppointmentPersonBean);  // 自己的信息

                int public_appointment_id = publicAppointmentPersonBean.getAppointment_id();

                AppointmentBean appointmentBean = appointmentDAO.findPublicAppointment(public_appointment_id);

                if (appointmentBean == null) {
                    throw new Exception("{ \"code\": 421, \"msg\": \"数据库操作失败\", \"data\": { } }");
                }

                appointmentRecord.put("appointment_info", appointmentBean);  // 预约信息

                // 如果是申请人，需要查找随行人员
                if (publicAppointmentPersonBean.getIs_applicant() == 1) {
                    ArrayList<AppointmentPersonBean> appointmentPersonBeans = appointmentDAO.findPublicAppointmentPerson(public_appointment_id);
                    if (appointmentPersonBeans == null || appointmentPersonBeans.isEmpty()) {
                        throw new Exception("{ \"code\": 421, \"msg\": \"数据库操作失败\", \"data\": { } }");
                    }

                    // 随行人员移除自己
                    appointmentPersonBeans.removeIf(appointmentPersonBean -> appointmentPersonBean.getId() == publicAppointmentPersonBean.getId());
                    appointmentRecord.put("entourages", appointmentPersonBeans);

                } else {
                    // 不是申请人，只能显示自己，随行人员为空
                    appointmentRecord.put("entourages", new JSONArray());
                }

                appointmentRecords.add(appointmentRecord);
            }

            for (AppointmentPersonBean officialAppointmentPersonBean : official_appointmentPersonBeans) {
                JSONObject appointmentRecord = new JSONObject();
                appointmentRecord.put("appointment_person", officialAppointmentPersonBean);

                int official_appointment_id = officialAppointmentPersonBean.getAppointment_id();
                AppointmentBean appointmentBean = appointmentDAO.findOfficialAppointment(official_appointment_id);

                if (appointmentBean == null) {
                    throw new Exception("{ \"code\": 421, \"msg\": \"数据库操作失败\", \"data\": { } }");
                }

                appointmentRecord.put("appointment_info", appointmentBean);

                if (officialAppointmentPersonBean.getIs_applicant() == 1) {
                    ArrayList<AppointmentPersonBean> appointmentPersonBeans = appointmentDAO.findOfficialAppointmentPerson(official_appointment_id);
                    if (appointmentPersonBeans == null || appointmentPersonBeans.isEmpty()) {
                        throw new Exception("{ \"code\": 421, \"msg\": \"数据库操作失败\", \"data\": { } }");
                    }

                    appointmentPersonBeans.removeIf(appointmentPersonBean -> appointmentPersonBean.getId() == officialAppointmentPersonBean.getId());
                    appointmentRecord.put("entourages", appointmentPersonBeans);
                } else {
                    appointmentRecord.put("entourages", new JSONArray());
                }

                appointmentRecords.add(appointmentRecord);
            }

            // 响应
            JSONObject tmp = new JSONObject();
            tmp.put("appointmentRecords", appointmentRecords);
            tmp.put("id_number", id_Number);    // 后续需要请求获取二维码信息，要用到身份证，这里后端直接返回源身份证

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("code", "200");
            jsonResponse.put("msg", "ok");
            jsonResponse.put("data", tmp);

            out.println(jsonResponse.toJSONString());
            appointmentDAO.releaseConnection();

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
