package org.example.backend.control.SchoolAdmin;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.AppointmentManagementDao;
import org.example.backend.utils.Jwt;
import org.example.backend.utils.Tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/official-appointments/approve")
public class OfficialAppointmentApproveServlet extends HttpServlet {
    private AppointmentManagementDao appointmentDao = new AppointmentManagementDao();
    private Jwt jwt = new Jwt();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String cookie = request.getHeader("Cookie");
        Map<String, Object> jwtPayload = jwt.validateJwt(cookie);

        if (jwtPayload == null || (int) jwtPayload.get("admin_role") != 1) {
            out.print("{\"code\": 403, \"msg\": \"Forbidden: Only school admins can approve official appointments\", \"data\": null}");
            return;
        }

        JSONObject jsonData = Tools.getRequestJsonData(request);
        int id = jsonData.getIntValue("id");
        int status = jsonData.getIntValue("status");

        try {
            if (appointmentDao.updateOfficialAppointmentApproval(id, status)) {
                out.print("{\"code\": 200, \"msg\": \"Approval updated\", \"data\": null}");
            } else {
                out.print("{\"code\": 404, \"msg\": \"Appointment not found\", \"data\": null}");
            }
        } catch (Exception e) {
            out.print("{\"code\": 500, \"msg\": \"Internal server error\", \"data\": null}");
            e.printStackTrace();
        }
    }
}