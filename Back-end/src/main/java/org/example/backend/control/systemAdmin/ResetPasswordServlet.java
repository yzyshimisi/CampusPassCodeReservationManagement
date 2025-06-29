package org.example.backend.control.systemAdmin;

import cn.hutool.crypto.SmUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.backend.dao.AdminDao;
import org.example.backend.model.Admin;
import org.example.backend.utils.Tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

@WebServlet("/api/systemAdmin/resetPassword")
public class ResetPasswordServlet extends HttpServlet {
    private static final String DEFAULT_PASSWORD = "12345678";

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

            String loginName = jsonData.getString("loginName");
            if(loginName == null || loginName.isEmpty()){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            Admin admin = adminDao.findByLoginName(loginName);
            if (admin == null) {
                throw new Exception("{ \"code\": 404, \"msg\": \"管理员不存在\", \"data\": { } }");
            }

            admin.setLoginPassword(SmUtil.sm3(DEFAULT_PASSWORD));
            admin.setLastPasswordUpdate(new Timestamp(System.currentTimeMillis()));

            if (!adminDao.resetPassword(admin)) {
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
