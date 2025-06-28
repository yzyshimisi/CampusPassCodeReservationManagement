package org.example.backend.control.SchoolAdmin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.DepartmentDao;
import org.example.backend.utils.Jwt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/department/delete")
public class DeleteDepartmentServlet extends HttpServlet {
    private DepartmentDao departmentDao = new DepartmentDao();
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
            out.print("{\"code\": 403, \"msg\": \"Forbidden: Only school admins (role 1) can delete departments\", \"data\": null}");
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

        // 提取参数
        String departmentName;
        if (jsonObj.has("departmentName")) {
            try {
                departmentName = jsonObj.get("departmentName").getAsString();
            } catch (NumberFormatException e) {
                out.print("{\"code\": 400, \"msg\": \"Invalid id parameter\", \"data\": null}");
                return;
            }
        } else {
            out.print("{\"code\": 400, \"msg\": \"Missing id parameter\", \"data\": null}");
            return;
        }

        try {
            if (departmentDao.deleteByDepartmentName(departmentName)) {
                out.print("{\"code\": 200, \"msg\": \"Department deleted\", \"data\": null}");
            } else {
                out.print("{\"code\": 404, \"msg\": \"Department not found\", \"data\": null}");
            }
        } catch (Exception e) {
            System.out.println("Error deleting department: " + e.getMessage());
            out.print("{\"code\": 500, \"msg\": \"Internal server error\", \"data\": null}");
        }
    }
}