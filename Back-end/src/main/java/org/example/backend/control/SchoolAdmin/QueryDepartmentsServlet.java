package org.example.backend.control.SchoolAdmin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.DepartmentDao;
import org.example.backend.model.Department;
import org.example.backend.utils.Jwt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/departments/query")
public class QueryDepartmentsServlet extends HttpServlet {
    private DepartmentDao departmentDao = new DepartmentDao();
    private Jwt jwt = new Jwt();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            out.print("{\"code\": 403, \"msg\": \"Forbidden: Only school admins (role 1) can view departments\", \"data\": null}");
            return;
        }

        try {
            List<Department> departments = departmentDao.findAllDepartments();
            StringBuilder result = new StringBuilder("{\"code\": 200, \"msg\": \"Success\", \"data\": [");
            boolean first = true;
            for (Department dept : departments) {
                if (!first) result.append(",");
                result.append("{\"id\": ").append(dept.getId())
                        .append(", \"departmentType\": ").append(dept.getDepartmentType() != null ? dept.getDepartmentType() : "null")
                        .append(", \"departmentName\": \"").append(dept.getDepartmentName() != null ? dept.getDepartmentName() : "")
                        .append("\"}");
                first = false;
            }
            result.append("]}");
            out.print(result.toString());
        } catch (Exception e) {
            System.out.println("Error querying departments: " + e.getMessage());
            out.print("{\"code\": 500, \"msg\": \"Internal server error\", \"data\": null}");
        }
    }
}