package org.example.backend.control.SchoolAdmin;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.AdminDao;
import org.example.backend.model.Admin;
import org.example.backend.utils.Tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@WebServlet("/api/schoolAdmin/updateAdmin")
public class UpdateAdminServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        JSONObject jsonData = Tools.getRequestJsonData(request);

        AdminDao adminDao = new AdminDao();

        try{
            adminDao.lookupConnection();

            List<String> fieldNames = Arrays.asList("loginName","fullName", "phone", "authStatus");
            if(Tools.areRequestFieldNull(jsonData,fieldNames)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            String loginName = jsonData.getString("loginName");
            String fullName = jsonData.getString("fullName");
            String phone = jsonData.getString("phone");
            Integer departmentId = jsonData.getInteger("departmentId");
            int authStatus = jsonData.getInteger("authStatus");

            if(!Tools.isValidPhone(phone)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            // 获取原管理员信息
            Admin existingAdmin = adminDao.findByLoginName(loginName);
            if(existingAdmin == null || existingAdmin.getAdminRole() != 3){
                throw new Exception("{ \"code\": 404, \"msg\": \"找不到对应的管理员\", \"data\": { } }");
            }

            // 直接修改现有对象
            existingAdmin.setFullName(fullName);
            existingAdmin.setPhone(phone);
            existingAdmin.setDepartmentId(departmentId);
            existingAdmin.setAuthStatus(authStatus);

            if (adminDao.modifyAdmin(existingAdmin)) {
                out.print("{\"code\": 200, \"msg\": \"更新成功\", \"data\": null}");
            } else {
                throw new Exception("{ \"code\": 500, \"msg\": \"更新失败\", \"data\": { } }");
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