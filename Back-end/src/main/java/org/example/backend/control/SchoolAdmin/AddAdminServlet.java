package org.example.backend.control.SchoolAdmin;

import cn.hutool.crypto.SmUtil;
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
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@WebServlet("/api/schoolAdmin/addAdmin")
public class AddAdminServlet extends HttpServlet {
    private String DEFAULT_PASSWORD = "12345678";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        JSONObject jsonData = Tools.getRequestJsonData(request);

        AdminDao adminDao = new AdminDao();

        try{
            adminDao.lookupConnection();

            List<String> fieldNames = Arrays.asList("fullName", "loginName", "phone", "authStatus");
            if(Tools.areRequestFieldNull(jsonData,fieldNames)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            String fullName = jsonData.getString("fullName");
            String loginName = jsonData.getString("loginName");
            String phone = jsonData.getString("phone");
            Integer departmentId = jsonData.getInteger("departmentId");
            int authStatus = jsonData.getIntValue("authStatus");

            if(!Tools.isValidPhone(phone)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            // 检查 loginName 唯一性
            Admin existingAdmin = adminDao.findByLoginName(loginName);
            if (existingAdmin != null) {
                throw new Exception("{ \"code\": 400, \"msg\": \"用户名已存在\", \"data\": { } }");
            }

            Admin admin = new Admin();
            admin.setFullName(fullName);
            admin.setLoginName(loginName);
            admin.setAdminRole(3);
            admin.setLoginPassword(SmUtil.sm3(DEFAULT_PASSWORD));
            admin.setPhone(phone);
            admin.setDepartmentId(departmentId);
            admin.setAuthStatus(authStatus);
            admin.setLoginFailCount(0);

            Timestamp now = new Timestamp(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(now.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, -90);
            Timestamp newTimestamp = new Timestamp(calendar.getTimeInMillis());

            admin.setLastPasswordUpdate(newTimestamp);
            admin.setLastLoginFailTime(newTimestamp);

            if (adminDao.addAdmin(admin)) {
                out.print("{ \"code\": 200, \"msg\": \"创建成功\", \"data\": { } }");
            } else {
                throw new Exception("{ \"code\": 500, \"msg\": \"创建失败\", \"data\": { } }");
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