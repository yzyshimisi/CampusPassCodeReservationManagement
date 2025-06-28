package org.example.backend.control;

import org.example.backend.dao.AdminDao;
import org.example.backend.model.Admin;
import org.example.backend.model.SM3Util;
import org.example.backend.utils.Jwt;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@WebServlet("/API/login")
public class LoginServlet extends HttpServlet {
    private static final long PASSWORD_EXPIRY_DAYS = 90;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_MINUTES = 30;
    private static final long SESSION_TIMEOUT_MINUTES = 30;

    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        // 1. 读取 JSON 请求体
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        JsonObject reqJson = gson.fromJson(sb.toString(), JsonObject.class);

        String username = reqJson.get("username").getAsString();
        String password = reqJson.get("password").getAsString();
        int roleCode = reqJson.get("role").getAsInt();

        JsonObject resJson = new JsonObject();

        // 2. 参数校验
        if (username.isEmpty() || password.isEmpty()) {
            resJson.addProperty("code", 400);
            resJson.addProperty("msg", "用户名或密码不能为空");
            response.getWriter().print(resJson.toString());
            return;
        }

        // 3. 加密密码
        String hashedPassword;
        try {
            hashedPassword = SM3Util.hash(password);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            resJson.addProperty("code", 500);
            resJson.addProperty("msg", "密码加密失败");
            response.getWriter().print(resJson.toString());
            return;
        }

        // 4. 查找用户
        AdminDao adminDao = new AdminDao();
        Admin admin = adminDao.findByLoginName(username);

        if (admin == null) {
            resJson.addProperty("code", 404);
            resJson.addProperty("msg", "用户不存在");
            response.getWriter().print(resJson.toString());
            return;
        }

        // 5.1 自动清零失败计数（如果超过1小时）
        Timestamp lastFailTime = admin.getLastLoginFailTime();
        if (lastFailTime != null) {
            long minutesSinceLastFail = ChronoUnit.MINUTES.between(
                    lastFailTime.toLocalDateTime(),
                    LocalDateTime.now()
            );
            if (minutesSinceLastFail >= 60 && admin.getLoginFailCount() > 0) {
                admin.setLoginFailCount(0);
                adminDao.modifyAdmin(admin);
            }
        }

        // 5. 检查是否被锁定
        if (admin.getLoginFailCount() >= MAX_LOGIN_ATTEMPTS) {
            if (lastFailTime != null) {
                long minutes = ChronoUnit.MINUTES.between(
                        lastFailTime.toLocalDateTime(),
                        LocalDateTime.now()
                );
                if (minutes < LOCKOUT_MINUTES) {
                    resJson.addProperty("code", 403);
                    resJson.addProperty("msg", "账户被锁定，请 " + (LOCKOUT_MINUTES - minutes) + " 分钟后再试");
                    response.getWriter().print(resJson.toString());
                    return;
                } else {
                    admin.setLoginFailCount(0);
                    adminDao.modifyAdmin(admin);
                }
            }
        }

        // 6. 验证密码和角色
        if (admin.getLoginPassword().equals(hashedPassword) && admin.getAdminRole() == roleCode) {

            String status = "0"; // 默认密码未过期
            Timestamp lastPwdUpdate = admin.getLastPasswordUpdate();
            if (lastPwdUpdate != null) {
                long days = ChronoUnit.DAYS.between(
                        lastPwdUpdate.toLocalDateTime(),
                        LocalDateTime.now()
                );
                if (days > PASSWORD_EXPIRY_DAYS) {
                    status = "1"; // 密码已过期
                }
            }

            // 更新状态
            admin.setLoginFailCount(0);
            adminDao.modifyAdmin(admin);

            // 登录成功：添加 Jwt
            Jwt jwtUtil = new Jwt();
            String token = jwtUtil.generateJwtToken(
                    String.valueOf(admin.getId()),
                    admin.getLoginName(),
                    admin.getAdminRole() // 使用数据库中的实际角色
            );

            // 设置 Cookie
            Cookie jwtCookie = new Cookie("jwtToken", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(2 * 60 * 60);
            response.addCookie(jwtCookie);

            // 返回指定格式 JSON
            JsonObject dataJson = new JsonObject();
            dataJson.addProperty("role", admin.getAdminRole());
            dataJson.addProperty("token", token);
            dataJson.addProperty("status", status);

            JsonObject result = new JsonObject();
            result.addProperty("code", 200);
            result.addProperty("msg", "登录成功");
            result.add("data", dataJson);

            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(result.toString());
        } else {
            admin.setLoginFailCount(admin.getLoginFailCount() + 1);
            admin.setLastLoginFailTime(new Timestamp(System.currentTimeMillis()));
            adminDao.modifyAdmin(admin);

            resJson.addProperty("code", 401);
            resJson.addProperty("msg", "用户名或密码错误");
            response.getWriter().print(resJson.toString());
        }
    }

    private String getRedirectPageByRole(int roleCode) {
        return switch (roleCode) {
            case 0 -> "/systemAdmin/home";
            case 1 -> "/schoolAdmin/home";
            case 2 -> "/auditAdmin/home";
            case 3 -> "/departmentAdmin/home";
            default -> "/login";
        };
    }
}