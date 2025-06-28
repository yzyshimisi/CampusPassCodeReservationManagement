package org.example.backend.control.systemAdmin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.backend.dao.AdminDao;
import org.example.backend.model.Admin;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/systemAdmin/searchByName")
public class SearchAdminByNameServlet extends HttpServlet {

    private final AdminDao adminDao = new AdminDao();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String name = req.getParameter("name");
        if (name == null || name.isBlank()) {
            resp.setStatus(400);
            JsonObject error = new JsonObject();
            error.addProperty("code", 400);
            error.addProperty("msg", "缺少 name 参数");
            resp.getWriter().print(error.toString());
            return;
        }

        try {
            List<Admin> result = adminDao.findByFuzzyName(name);

            JsonObject res = new JsonObject();
            res.addProperty("code", 200);
            res.addProperty("msg", "success");
            res.add("data", gson.toJsonTree(result));
            resp.getWriter().print(res.toString());
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            JsonObject err = new JsonObject();
            err.addProperty("code", 500);
            err.addProperty("msg", "server error");
            resp.getWriter().print(err.toString());
        }
    }
}
