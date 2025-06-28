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
import java.util.List;
import java.util.Map;

@WebServlet("/admin/manage/fuzzySearch")
public class FuzzySearchAdminServlet extends HttpServlet {
    private AdminDao adminDao = new AdminDao();
    private Jwt jwt = new Jwt();
    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String cookie = request.getHeader("Cookie");

        // 验证 JWT 并获取调用者角色
        Map<String, Object> jwtPayload = jwt.validateJwt(cookie); // 保持类型为 Map<String, Object>
        if (jwtPayload == null) {
            out.print("{\"code\": 401, \"msg\": \"Unauthorized\", \"data\": null}");
            return;
        }

        Object roleObj = jwtPayload.get("admin_role");
        if (roleObj == null || !(roleObj instanceof Integer) || (int) roleObj != 1) {
            out.print("{\"code\": 403, \"msg\": \"Forbidden: Only school admins (role 1) can search admins\", \"data\": null}");
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

        String fuzzyName = null;
        try {
            // 解析 JSON 请求体
            JsonObject jsonObj = gson.fromJson(json.toString(), JsonObject.class);
            fuzzyName = jsonObj.has("fuzzyName") ? jsonObj.get("fuzzyName").getAsString() : null;
            if (fuzzyName == null) {
                out.print("{\"code\": 400, \"msg\": \"Missing fuzzyName\", \"data\": null}");
                return;
            }
        } catch (Exception e) {
            System.out.println("JSON Parsing Error: " + e.getMessage());
            out.print("{\"code\": 400, \"msg\": \"Invalid JSON format\", \"data\": null}");
            return;
        }

        // 执行模糊查询并过滤部门管理员 (role 3)
        List<Admin> admins = adminDao.findByFuzzyName(fuzzyName);
        StringBuilder result = new StringBuilder("{\"code\": 200, \"msg\": \"Success\", \"data\": [");
        boolean first = true;
        for (Admin admin : admins) {
            if (admin.getAdminRole() == 3) { // 仅包含部门管理员
                if (!first) result.append(",");
                result.append("{\"id\": ").append(admin.getId())
                        .append(", \"fullName\": \"").append(admin.getFullName() != null ? admin.getFullName() : "")
                        .append("\", \"loginName\": \"").append(admin.getLoginName() != null ? admin.getLoginName() : "")
                        .append("\", \"phone\": \"").append(admin.getPhone() != null ? admin.getPhone() : "")
                        .append("\", \"authStatus\": ").append(admin.getAuthStatus())
                        .append("}");
                first = false;
            }
        }
        result.append("]}");
        out.print(result.toString());
    }
}