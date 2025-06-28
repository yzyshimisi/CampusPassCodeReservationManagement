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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/admin/manage/delete")
public class DeleteAdminServlet extends HttpServlet {
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

        String loginName = jsonObj.has("loginName") ? jsonObj.get("loginName").getAsString() : null;
        if (loginName == null) {
            out.print("{\"code\": 400, \"msg\": \"Missing loginName\", \"data\": null}");
            return;
        }

        if (adminDao.deleteAdmin(loginName)) {
            out.print("{\"code\": 200, \"msg\": \"Admin deleted\", \"data\": null}");
        } else {
            out.print("{\"code\": 404, \"msg\": \"Admin not found\", \"data\": null}");
        }
    }
}