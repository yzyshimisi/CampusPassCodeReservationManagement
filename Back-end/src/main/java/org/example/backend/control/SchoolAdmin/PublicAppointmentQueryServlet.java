package org.example.backend.control.SchoolAdmin;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.AppointmentManagementDao;
import org.example.backend.model.AppointmentBean;
import org.example.backend.model.AppointmentPersonBean;
import org.example.backend.utils.Jwt;
import org.example.backend.utils.Tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

@WebServlet("/ ")
public class PublicAppointmentQueryServlet extends HttpServlet {
    private AppointmentManagementDao appointmentDao = new AppointmentManagementDao();
    private Jwt jwt = new Jwt();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String cookie = request.getHeader("Cookie");
        Map<String, Object> jwtPayload = jwt.validateJwt(cookie);

        if (jwtPayload == null || (int) jwtPayload.get("admin_role") != 1) {
            out.print("{\"code\": 403, \"msg\": \"Forbidden: Only school admins can view all public appointments\", \"data\": null}");
            return;
        }

        try {
            ArrayList<AppointmentBean> appointments = appointmentDao.getAllPublicAppointments();
            JSONArray results = new JSONArray();
            for (AppointmentBean apt : appointments) {
                ArrayList<AppointmentPersonBean> persons = appointmentDao.findPublicAppointmentPersons(apt.getId());
                JSONObject record = new JSONObject();
                record.put("appointment", apt);
                record.put("persons", persons);
                results.add(record);
            }

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("code", 200);
            jsonResponse.put("msg", "View all successful");
            jsonResponse.put("data", results);
            out.print(jsonResponse.toJSONString());
        } catch (Exception e) {
            out.print("{\"code\": 500, \"msg\": \"Internal server error\", \"data\": null}");
            e.printStackTrace();
        }
    }

    // 保留 doPost 方法以兼容现有调用（可选）
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response); // 重定向到 doGet
    }
}