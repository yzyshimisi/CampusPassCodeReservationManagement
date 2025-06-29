package org.example.backend.control.SchoolAdmin;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.AdminDao;
import org.example.backend.utils.Tools;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/schoolAdmin/deleteAdmin")
public class DeleteAdminServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        JSONObject jsonData = Tools.getRequestJsonData(request);

        AdminDao adminDao = new AdminDao();

        try{
            adminDao.lookupConnection();

            String loginName = jsonData.getString("loginName");
            if(loginName==null || loginName.isEmpty()){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            if (adminDao.deleteAdmin(loginName)) {
                out.print("{\"code\": 200, \"msg\": \"删除成功\", \"data\": null}");
            } else {
                throw new Exception("{ \"code\": 500, \"msg\": \"删除失败\", \"data\": { } }");
            }

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