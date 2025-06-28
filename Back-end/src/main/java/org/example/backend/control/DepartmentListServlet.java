package org.example.backend.control;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.backend.dao.DepartmentDao;
import org.example.backend.model.Department;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/systemAdmin/departmentlist")
public class DepartmentListServlet extends HttpServlet {

    private final DepartmentDao deptDao = new DepartmentDao();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        try {
            List<Department> list = deptDao.findAllDepartments();   // 复用你的 DAO 方法

            JsonObject res = new JsonObject();
            res.addProperty("code", 200);
            res.addProperty("msg", "success");
            res.add("data", gson.toJsonTree(list));                 // data = 数组
            resp.getWriter().print(res.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JsonObject res = new JsonObject();
            res.addProperty("code", 500);
            res.addProperty("msg", "server error");
            resp.setStatus(500);
            resp.getWriter().print(res.toString());
        }
    }
}
