package org.example.backend.control.SchoolAdmin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.DepartmentDao;
import org.example.backend.model.Department;
import org.example.backend.utils.Jwt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/department/add")
public class AddDepartmentServlet extends HttpServlet {
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
            out.print("{\"code\": 403, \"msg\": \"Forbidden: Only school admins (role 1) can add departments\", \"data\": null}");
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
        Integer departmentType = jsonObj.has("departmentType") ? jsonObj.get("departmentType").getAsInt() : null;
        String departmentName = jsonObj.has("departmentName") ? jsonObj.get("departmentName").getAsString() : null;
        if (departmentName == null || departmentName.trim().isEmpty()) {
            out.print("{\"code\": 400, \"msg\": \"Missing or empty departmentName\", \"data\": null}");
            return;
        }

        // 检查 departmentName 唯一性
        try {
            Department existingDept = departmentDao.findByDepartmentName(departmentName);
            if (existingDept != null) {
                out.print("{\"code\": 400, \"msg\": \"Department name already exists\", \"data\": null}");
                return;
            }
        } catch (Exception e) {
            System.out.println("Error checking department name: " + e.getMessage());
            out.print("{\"code\": 500, \"msg\": \"Internal server error\", \"data\": null}");
            return;
        }

        // 创建并保存部门
        Department dept = new Department();
        dept.setDepartmentType(departmentType);
        dept.setDepartmentName(departmentName);

        try {
            if (departmentDao.insertDepartment(dept)) {
                out.print("{\"code\": 200, \"msg\": \"Department added\", \"data\": null}");
            } else {
                out.print("{\"code\": 500, \"msg\": \"Failed to add department\", \"data\": null}");
            }
        } catch (Exception e) {
            System.out.println("Error adding department: " + e.getMessage());
            out.print("{\"code\": 500, \"msg\": \"Internal server error\", \"data\": null}");
        }
    }
}