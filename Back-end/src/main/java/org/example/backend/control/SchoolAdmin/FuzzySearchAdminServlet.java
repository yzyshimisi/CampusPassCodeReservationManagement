package org.example.backend.control.SchoolAdmin;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.AdminDao;
import org.example.backend.model.Admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/schoolAdmin/fuzzySearchAdmin")
public class FuzzySearchAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        AdminDao adminDao = new AdminDao();

        try{
            adminDao.lookupConnection();

            String fuzzyName = request.getParameter("fuzzyName");
            if(fuzzyName == null || fuzzyName.isEmpty()){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            // 执行模糊查询并过滤部门管理员 (role 3)
            List<Admin> admins = adminDao.findByFuzzyName(fuzzyName);
            admins.removeIf(admin -> admin.getAdminRole() != 3);

            JSONObject jsonRes = new JSONObject();
            jsonRes.put("code", 200);
            jsonRes.put("msg", "ok");
            jsonRes.put("data", admins);

            out.println(jsonRes.toJSONString());

            adminDao.releaseConnection();
        }catch (Exception e){
            try{
                adminDao.releaseConnection();
            }catch (Exception e2){
                e2.printStackTrace();
            }
            out.print(e.getMessage());
        }
    }
}