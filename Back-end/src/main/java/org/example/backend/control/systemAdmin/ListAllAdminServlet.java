package org.example.backend.control.systemAdmin;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.AdminDao;
import org.example.backend.model.Admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@WebServlet("/api/systemAdmin/listAllAdmin")
public class ListAllAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        AdminDao adminDao = new AdminDao();
        try {
            adminDao.lookupConnection();
            List<Admin> admins = adminDao.findAllAdmin();

            JSONObject jsonRes = new JSONObject();
            jsonRes.put("code", 200);
            jsonRes.put("msg", "ok");
            jsonRes.put("data", admins);

            out.print(jsonRes);

            adminDao.releaseConnection();
        } catch (Exception e) {
            try{
                adminDao.releaseConnection();
            }catch(Exception ex){
                ex.printStackTrace();
            }
            out.print(e.getMessage());
        }
    }
}
