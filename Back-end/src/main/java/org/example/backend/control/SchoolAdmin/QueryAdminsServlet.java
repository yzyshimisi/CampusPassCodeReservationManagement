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

@WebServlet("/api/schoolAdmin/queryAdmin")
public class QueryAdminsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        AdminDao adminDao = new AdminDao();

        try{
            adminDao.lookupConnection();

            // 查询所有管理员并过滤部门管理员 (role 3)
            List<Admin> admins = adminDao.findAllAdmin();
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