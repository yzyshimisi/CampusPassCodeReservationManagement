package org.example.backend.control.SchoolAdmin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.AdminDao;
import org.example.backend.utils.Jwt;
import org.example.backend.model.Admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/manage/query")
public class QueryAdminsServlet extends HttpServlet {
    private AdminDao adminDao = new AdminDao();
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
            out.print("{\"code\": 403, \"msg\": \"Forbidden: Only school admins (role 1) can query admins\", \"data\": null}");
            return;
        }

        // 查询所有管理员并过滤部门管理员 (role 3)
        List<Admin> admins = adminDao.findAllAdmin();
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