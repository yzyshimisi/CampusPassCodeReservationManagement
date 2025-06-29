package org.example.backend.control.systemAdmin;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.backend.dao.AdminDao;
import org.example.backend.model.Admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/systemAdmin/searchByName")
public class SearchAdminByNameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        AdminDao adminDao = new AdminDao();

        try{
            adminDao.lookupConnection();

            String fuzzyName = req.getParameter("fuzzyName");
            if (fuzzyName == null || fuzzyName.isEmpty()) {
                throw new Exception("{ \"code\": 400, \"msg\": \"缺少 name 参数\", \"data\": { } }");
            }

            List<Admin> result = adminDao.findByFuzzyName(fuzzyName);
            if(result.isEmpty()){
                throw new Exception("{ \"code\": 500, \"msg\": \"server error\", \"data\": { } }");
            }

            JSONObject jsonRes = new JSONObject();
            jsonRes.put("code", 200);
            jsonRes.put("msg", "ok");
            jsonRes.put("data", result);

            out.println(jsonRes.toJSONString());

            adminDao.releaseConnection();
        }catch (Exception e){
            try{
                adminDao.releaseConnection();
            }catch (Exception e2){
                e.printStackTrace();
            }
            out.println(e.getMessage());
        }
    }
}
