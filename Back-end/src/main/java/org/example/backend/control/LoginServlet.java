package org.example.backend.control;

import cn.hutool.crypto.SmUtil;
import com.alibaba.fastjson2.JSONObject;
import org.example.backend.dao.AdminDao;
import org.example.backend.model.Admin;
import org.example.backend.model.SM3Util;
import org.example.backend.utils.Jwt;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.backend.utils.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    private static final long PASSWORD_EXPIRY_DAYS = 90;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_MINUTES = 30;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8"); request.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        // 读取 JSON 请求体
        JSONObject jsonData = Tools.getRequestJsonData(request);

        AdminDao adminDao = new AdminDao();

        try{
            adminDao.lookupConnection();

            List<String> fieldNames = Arrays.asList("login_name", "login_password", "admin_role");
            if(Tools.areRequestFieldNull(jsonData,fieldNames)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            String loginName = jsonData.getString("login_name");
            String loginPassword = jsonData.getString("login_password");
            int adminRole = jsonData.getIntValue("admin_role");

            if(!(adminRole>=0 && adminRole<=3)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            // 查找用户
            Admin admin = adminDao.findByLoginName(loginName);

            if(admin == null){
                throw new Exception("{ \"code\": 404, \"msg\": \"用户不存在\", \"data\": { } }");
            }

            // 自动清零失败次数数（如果超过锁定时间）
            Timestamp lastFailTime = admin.getLastLoginFailTime();
            if (lastFailTime != null) {
                long minutesSinceLastFail = ChronoUnit.MINUTES.between(
                        lastFailTime.toLocalDateTime(),
                        LocalDateTime.now()
                );
                if (minutesSinceLastFail >= LOCKOUT_MINUTES) {
                    admin.setLoginFailCount(0);
                    admin.setIsLock(0);
                    adminDao.modifyAdmin(admin);
                }
            }

            // 检查是否被锁定
            if (admin.getIsLock() == 1) {
                if(lastFailTime!=null){
                    long minutes = ChronoUnit.MINUTES.between(
                            lastFailTime.toLocalDateTime(),
                            LocalDateTime.now()
                    );
                    String lockoutMinutes = (LOCKOUT_MINUTES - minutes) + "";
                    String jsonString = "{ \"code\": 404, \"msg\": \"账户被锁定，请 " + lockoutMinutes + " 分钟后再试\", \"data\": { } }";
                    throw new Exception(jsonString);
                }
            }

            // 验证密码和角色
            System.out.println(SmUtil.sm3(loginPassword));
            if (admin.getLoginPassword().equals(SmUtil.sm3(loginPassword)) && admin.getAdminRole() == adminRole) {

                String status = "0"; // 默认密码未过期
                Timestamp lastPwdUpdate = admin.getLastPasswordUpdate();
                if (lastPwdUpdate != null) {
                    long days = ChronoUnit.DAYS.between(
                            lastPwdUpdate.toLocalDateTime(),
                            LocalDateTime.now()
                    );
                    if (days >= PASSWORD_EXPIRY_DAYS) {
                        status = "1"; // 密码已过期
                    }
                }

                // 更新状态
                admin.setLoginFailCount(0);
                adminDao.modifyAdmin(admin);

                // 生成Token
                Jwt jwtUtil = new Jwt();
                String token = jwtUtil.generateJwtToken(
                        admin.getId(),
                        admin.getLoginName(),
                        admin.getAdminRole()
                );

                // 返回指定格式 JSON
                JSONObject res = new JSONObject();
                res.put("code", 200);
                res.put("msg", "ok");

                JSONObject data = new JSONObject();
                data.put("token", token);
                data.put("admin_role", admin.getAdminRole());
                data.put("status", status);

                res.put("data", data);

                out.print(res);

            } else {
                admin.setLoginFailCount(admin.getLoginFailCount() + 1);
                admin.setLastLoginFailTime(new Timestamp(System.currentTimeMillis()));
                if(admin.getLoginFailCount() >= MAX_LOGIN_ATTEMPTS){    // 如果超过了次数，禁用用户
                    admin.setIsLock(1);
                }
                adminDao.modifyAdmin(admin);

                throw new Exception("{ \"code\": 401, \"msg\": \"用户名或密码错误\", \"data\": { } }");
            }

            adminDao.releaseConnection();
        } catch (Exception e){
            try{
                adminDao.releaseConnection();
            }catch (Exception e2){
                e2.printStackTrace();
            }
            out.print(e.getMessage());
        }
    }
}