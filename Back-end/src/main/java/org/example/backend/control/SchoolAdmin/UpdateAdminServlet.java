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
import java.sql.Timestamp;
import java.util.Map;

@WebServlet("/admin/manage/update")
public class UpdateAdminServlet extends HttpServlet {
    private AdminDao adminDao = new AdminDao();
    private Jwt jwt = new Jwt();
    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String cookie = request.getHeader("Cookie");

        // 验证 JWT 并获取调用者角色
        Map<String, Object> jwtPayload = jwt.validateJwt(cookie);
        if (jwtPayload == null) {
            out.print("{\"code\": 401, \"msg\": \"Unauthorized\", \"data\": null}");
            return;
        }
         Object roleObj = jwtPayload.get("admin_role");
         if (roleObj == null || !(roleObj instanceof Integer) || (int) roleObj != 1) {
             out.print("{\"code\": 403, \"msg\": \"Forbidden: Only school admins (role 1) can update admins\", \"data\": null}");
             return;
         }

        // 读取请求体
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

        String loginName= jsonObj.has("loginName") ? jsonObj.get("loginName").getAsString() : null;
        // 获取原管理员信息
        Admin existingAdmin = adminDao.findByLoginName(loginName);
        if (existingAdmin == null) {
            out.print("{\"code\": 404, \"msg\": \"Admin not found\", \"data\": null}");
            return;
        }
        if (existingAdmin.getAdminRole() != 3) {
            out.print("{\"code\": 403, \"msg\": \"Forbidden: Only department admins (role 3) can be updated\", \"data\": null}");
            return;
        }

        // 直接修改现有对象
        existingAdmin.setFullName(jsonObj.has("fullName") ? jsonObj.get("fullName").getAsString() : existingAdmin.getFullName());
        existingAdmin.setPhone(jsonObj.has("phone") ? jsonObj.get("phone").getAsString() : existingAdmin.getPhone());
        existingAdmin.setDepartmentId(jsonObj.has("departmentId") ? jsonObj.get("departmentId").getAsInt() : existingAdmin.getDepartmentId());


        // 密码处理：仅重置为 "123456"，且需明确请求
        if (jsonObj.has("resetPassword") && jsonObj.get("resetPassword").getAsBoolean()) {
            existingAdmin.setLoginPassword(SchoolAdminManagementUtils.sm3Encrypt("123456"));
            existingAdmin.setLastPasswordUpdate(new Timestamp(System.currentTimeMillis()));
        }

        // 保留原有的其他字段值，避免重置
        existingAdmin.setAuthStatus(existingAdmin.getAuthStatus());
        existingAdmin.setLoginFailCount(existingAdmin.getLoginFailCount());
        existingAdmin.setLastLoginFailTime(existingAdmin.getLastLoginFailTime());
        existingAdmin.setAdminRole(existingAdmin.getAdminRole()); // 保留原角色

        try {
            if (adminDao.modifyAdmin(existingAdmin)) {
                out.print("{\"code\": 200, \"msg\": \"Admin updated\", \"data\": null}");
            } else {
                out.print("{\"code\": 500, \"msg\": \"Failed to update admin\", \"data\": null}");
            }
        } catch (Exception e) {
            System.out.println("Error updating admin: " + e.getMessage());
            out.print("{\"code\": 500, \"msg\": \"Internal server error\", \"data\": null}");
        }
    }
}