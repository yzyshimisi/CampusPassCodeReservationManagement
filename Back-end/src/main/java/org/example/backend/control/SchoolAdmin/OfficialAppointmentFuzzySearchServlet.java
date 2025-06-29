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

@WebServlet("/official-appointments/fuzzysearch")
public class OfficialAppointmentFuzzySearchServlet extends HttpServlet {
    private AppointmentManagementDao appointmentDao = new AppointmentManagementDao();
    private Jwt jwt = new Jwt();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String cookie = request.getHeader("Cookie");
        Map<String, Object> jwtPayload = jwt.validateJwt(cookie);

        if (jwtPayload == null || (int) jwtPayload.get("admin_role") != 1) {
            out.print("{\"code\": 403, \"msg\": \"Forbidden: Only school admins can query official appointments\", \"data\": null}");
            return;
        }

        JSONObject jsonData = Tools.getRequestJsonData(request);
        String fullName = jsonData.getString("fullName"); // 容错处理
        String phone = jsonData.getString("phone");      // 容错处理
        if (fullName == null) fullName = "";
        if (phone == null) phone = "";

        try {
            System.out.println("fullName: " + fullName + ", phone: " + phone); // 调试输出
            ArrayList<AppointmentBean> appointments = appointmentDao.queryOfficialAppointmentsByNamePhone(fullName, phone);
            JSONArray results = new JSONArray();
            for (AppointmentBean apt : appointments) {
                ArrayList<AppointmentPersonBean> persons = appointmentDao.findOfficialAppointmentPersons(apt.getId());
                JSONObject record = new JSONObject();
                record.put("appointment", apt);
                record.put("persons", persons);
                results.add(record);
            }

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("code", 200);
            jsonResponse.put("msg", "Query successful");
            jsonResponse.put("data", results);
            out.print(jsonResponse.toJSONString());
        } catch (Exception e) {
            e.printStackTrace(); // 确保异常可见
            out.print("{\"code\": 500, \"msg\": \"Internal server error: " + e.getMessage() + "\", \"data\": null}");
        }
    }
    }