package org.example.backend.control.SchoolAdmin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.AdminDao;
import org.example.backend.utils.SchoolAdmin.SchoolAdminManagementUtils;
import org.example.backend.utils.Jwt;
import org.example.backend.model.Admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/admin/manage/add")
public class AddAdminServlet extends HttpServlet {
    private AdminDao adminDao = new AdminDao();
    private Jwt jwt = new Jwt();
    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String cookie = request.getHeader("Cookie");

        if (jwt.validateJwt(cookie) == null) {
            out.print("{\"code\": 401, \"msg\": \"Unauthorized\", \"data\": null}");
            return;
        }

        StringBuilder json = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading request: " + e.getMessage());
            out.print("{\"code\": 500, \"msg\": \"Error reading request\", \"data\": null}");
            return;
        }

        JsonObject jsonObj;
        try {
            jsonObj = gson.fromJson(json.toString(), JsonObject.class);
        } catch (Exception e) {
            System.out.println("JSON Parsing Error: " + e.getMessage());
            out.print("{\"code\": 400, \"msg\": \"Invalid JSON format\", \"data\": null}");
            return;
        }

        String fullName = jsonObj.has("fullName") ? jsonObj.get("fullName").getAsString() : null;
        String loginName = jsonObj.has("loginName") ? jsonObj.get("loginName").getAsString() : null;
        String password = jsonObj.has("password") ? jsonObj.get("password").getAsString() : null;
        String phone = jsonObj.has("phone") ? jsonObj.get("phone").getAsString() : null;
        String departmentId = jsonObj.has("departmentId") ? jsonObj.get("departmentId").getAsString() : null;

        // 验证必填字段
        if (fullName == null || fullName.trim().isEmpty() ||
                loginName == null || loginName.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            out.print("{\"code\": 400, \"msg\": \"Missing or empty required fields\", \"data\": null}");
            return;
        }

        // 检查 loginName 唯一性
        try {
            Admin existingAdmin = adminDao.findByLoginName(loginName);
            if (existingAdmin != null) {
                out.print("{\"code\": 400, \"msg\": \"Login name already exists\", \"data\": null}");
                return;
            }
        } catch (Exception e) {
            System.out.println("Error checking login name: " + e.getMessage());
            out.print("{\"code\": 500, \"msg\": \"Internal server error\", \"data\": null}");
            return;
        }

        Admin admin = new Admin();
        admin.setFullName(fullName);
        admin.setLoginName(loginName);
        admin.setAdminRole(3);
        admin.setLoginPassword(password != null ? SchoolAdminManagementUtils.sm3Encrypt(password) : null);
        admin.setPhone(phone);
        admin.setDepartmentId(departmentId != null ? Integer.parseInt(departmentId) : null);
        admin.setAuthStatus(0);
        admin.setLoginFailCount(0);
        admin.setLastPasswordUpdate(new java.sql.Timestamp(System.currentTimeMillis()));
        admin.setLastLoginFailTime(null);

        if (adminDao.addAdmin(admin)) {
            out.print("{\"code\": 200, \"msg\": \"Admin added\", \"data\": {\"id\": " + admin.getId() + "}}");
        } else {
            out.print("{\"code\": 500, \"msg\": \"Failed to add admin\", \"data\": null}");
        }
    }
}