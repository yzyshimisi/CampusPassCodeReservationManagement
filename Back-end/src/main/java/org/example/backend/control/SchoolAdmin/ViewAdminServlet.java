package org.example.backend.control.SchoolAdmin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.AdminDao;
import org.example.backend.utils.Jwt;
import org.example.backend.model.Admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/admin/manage/view")
public class ViewAdminServlet extends HttpServlet {
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
            System.out.println("Role validation failed: roleObj = " + roleObj); // 调试日志
            out.print("{\"code\": 403, \"msg\": \"Forbidden: Only school admins (role 1) can view admins\", \"data\": null}");
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

        // 获取 loginName
        String loginName = jsonObj.has("loginName") ? jsonObj.get("loginName").getAsString() : null;
        if (loginName == null || loginName.trim().isEmpty()) {
            out.print("{\"code\": 400, \"msg\": \"Missing or empty loginName\", \"data\": null}");
            return;
        }

        // 查询管理员
        Admin admin = adminDao.findByLoginName(loginName);
        if (admin != null) {
            // 验证目标管理员是否为部门管理员 (role 3)
            if (admin.getAdminRole() != 3) {
                out.print("{\"code\": 404, \"msg\": \"Admin not found\", \"data\": null}");
                return;
            }
            out.print("{\"code\": 200, \"msg\": \"Success\", \"data\": {\"id\": " + admin.getId() +
                    ", \"fullName\": \"" + admin.getFullName() +
                    "\", \"loginName\": \"" + admin.getLoginName() +
                    "\", \"phone\": \"" + admin.getPhone() +
                    "\", \"authStatus\": " + admin.getAuthStatus() + "}}");
        } else {
            out.print("{\"code\": 404, \"msg\": \"Admin not found\", \"data\": null}");
        }
    }
}