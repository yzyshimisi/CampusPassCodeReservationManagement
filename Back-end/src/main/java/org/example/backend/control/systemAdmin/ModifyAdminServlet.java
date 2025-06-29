package org.example.backend.control.systemAdmin;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.backend.dao.AdminDao;
import org.example.backend.model.Admin;
import org.example.backend.utils.Tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@WebServlet("/api/systemAdmin/modifyAdmin")
public class ModifyAdminServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        JSONObject jsonData = Tools.getRequestJsonData(req);

        AdminDao adminDao = new AdminDao();

//        Integer operatorId = Integer.parseInt(claims.get("id").toString());
//        OperatorContext.set(operatorId);

        try{
            adminDao.lookupConnection();

            List<String> fieldNames = Arrays.asList("loginName", "fullName", "phone", "adminRole");

            if(Tools.areRequestFieldNull(jsonData,fieldNames)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            String loginName = jsonData.getString("loginName");
            String fullName = jsonData.getString("fullName");
            String phone = jsonData.getString("phone");
            int adminRole = jsonData.getIntValue("adminRole");

            if(!Tools.isValidPhone(phone) || !(adminRole>=0 && adminRole<=3)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            Integer departmentId = jsonData.getInteger("departmentId");
            Integer authStatus = jsonData.getInteger("authStatus");

            Admin admin = adminDao.findByLoginName(loginName);
            if (admin == null) {
                throw new Exception("{ \"code\": 404, \"msg\": \"管理员不存在\", \"data\": { } }");
            }

            // 只修改基础信息，不改密码
            admin.setFullName(fullName);
            admin.setDepartmentId(departmentId);
            if(authStatus!=null) admin.setAuthStatus(authStatus);
            admin.setAdminRole(adminRole);
            admin.setPhone(phone);

            if (!adminDao.modifyAdmin(admin)) {
                throw new Exception("{ \"code\": 500, \"msg\": \"更新失败\", \"data\": { } }");
            } else {
                out.println("{ \"code\": 200, \"msg\": \"ok\", \"data\": { } }");
            }

            adminDao.releaseConnection();
        }catch (Exception e){
            try{
                adminDao.releaseConnection();
            }catch (Exception e2){
                e2.printStackTrace();
            }
            out.println(e.getMessage());
        }
    }
}
