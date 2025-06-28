package org.example.backend.control.systemAdmin;

import com.google.gson.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.backend.dao.AdminDao;
import org.example.backend.model.Admin;
import org.example.backend.utils.Jwt;
import org.example.backend.utils.OperatorContext;
import org.example.backend.model.SM3Util;

import java.io.IOException;
import java.sql.Timestamp;
@WebServlet("/api/systemAdmin/resetPassword")
public class ResetPasswordServlet extends HttpServlet {
    private final AdminDao adminDao = new AdminDao();
    private static final String DEFAULT_PASSWORD = "Wangpeining@123";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String jwtToken = null;
        for (Cookie cookie : req.getCookies()) {
            if ("jwtToken".equals(cookie.getName())) {
                jwtToken = cookie.getValue();
                break;
            }
        }

        if (jwtToken == null || jwtToken.isBlank()) {
            resp.setStatus(401);
            resp.getWriter().write("{\"msg\":\"未登录\"}");
            return;
        }

        Jwt jwtUtil = new Jwt();
        Claims claims = jwtUtil.validateJwt("jwtToken=" + jwtToken);
        if (claims == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"msg\":\"token 无效\"}");
            return;
        }

        Integer operatorId = Integer.parseInt(claims.get("id").toString());
        OperatorContext.set(operatorId);

        JsonObject body = JsonParser.parseReader(req.getReader()).getAsJsonObject();
        String loginName = body.get("loginName").getAsString();

        Admin admin = adminDao.findByLoginName(loginName);
        if (admin == null) {
            resp.setStatus(404);
            resp.getWriter().write("{\"msg\":\"管理员不存在\"}");
            return;
        }

        try {
            admin.setLoginPassword(SM3Util.hash(DEFAULT_PASSWORD));
            admin.setLastPasswordUpdate(new Timestamp(System.currentTimeMillis()));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"msg\":\"密码加密失败\"}");
            return;
        }

        if (!adminDao.resetPassword(admin)) {
            resp.setStatus(500);
            resp.getWriter().write("{\"msg\":\"更新失败\"}");
        } else {
            resp.getWriter().write("{\"msg\":\"密码重置成功\"}");
        }
    }
}
