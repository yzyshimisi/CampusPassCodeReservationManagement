package org.example.backend.control.systemAdmin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.AdminDao;
import org.example.backend.model.Admin;

import java.io.IOException;
import java.util.List;


@WebServlet("/api/systemAdmin/listAllAdmin")
public class ListAllAdminServlet extends HttpServlet {
    private final AdminDao adminDao = new AdminDao();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        try {
            List<Admin> admins = adminDao.findAllAdmin();

            JsonObject result = new JsonObject();
            result.addProperty("code", 200);
            result.addProperty("msg", "success");
            result.add("data", gson.toJsonTree(admins));
            resp.getWriter().print(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JsonObject error = new JsonObject();
            error.addProperty("code", 500);
            error.addProperty("msg", "server error");
            resp.setStatus(500);
            resp.getWriter().print(error.toString());
        }
    }
}
