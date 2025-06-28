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

@WebServlet("/api/systemAdmin/modifyAdmin")
public class ModifyAdminServlet extends HttpServlet {
    private final AdminDao adminDao = new AdminDao();
    private final Gson gson = new Gson();
    private static final String DEFAULT_PASSWORD = "Wangpeining@123";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        // ✅ 从 Cookie 中提取 jwtToken
        String jwtToken = null;
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if ("jwtToken".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }

        if (jwtToken == null || jwtToken.isBlank()) {
            error(resp, 401, "未登录：未携带 token");
            return;
        }

        // ✅ 验证 token 有效性并提取操作者 ID
        Jwt jwtUtil = new Jwt();
        Claims claims = jwtUtil.validateJwt("jwtToken=" + jwtToken);
        if (claims == null) {
            error(resp, 401, "登录失效或 token 非法");
            return;
        }
        Integer operatorId = Integer.parseInt(claims.get("id").toString());
        OperatorContext.set(operatorId); // ✅ ThreadLocal 注入操作者 ID

        // ✅ 读取并解析请求体
        ModifyDTO dto = gson.fromJson(req.getReader(), ModifyDTO.class);
        if (dto == null || isBlank(dto.loginName)) {
            error(resp, 400, "loginName 不能为空");
            return;
        }

        Admin target = adminDao.findByLoginName(dto.loginName);
        if (target == null) {
            error(resp, 404, "该管理员不存在");
            return;
        }
        // 更新后是部门管理员
        if (Integer.valueOf(3).equals(dto.adminRole)) {
            if (dto.departmentId == null || dto.authStatus == null) {
                error(resp, 400, "切换为部门管理员必须提供 departmentId 和 authStatus");
                return;
            }
        }

        // ✅ 修改字段赋值
        if (!isBlank(dto.fullName)) target.setFullName(dto.fullName);
        if (dto.departmentId != null) target.setDepartmentId(dto.departmentId);
        if (dto.authStatus != null) target.setAuthStatus(dto.authStatus);
        if (dto.adminRole != null) target.setAdminRole(dto.adminRole);
        if (Boolean.TRUE.equals(dto.resetPassword)) {
            try {
                target.setLoginPassword(SM3Util.hash(DEFAULT_PASSWORD));
                target.setLastPasswordUpdate(new Timestamp(System.currentTimeMillis()));
            } catch (Exception e) {
                e.printStackTrace();
                error(resp, 500, "密码加密失败");
                return;
            }
        }

        // ✅ DAO 修改操作
        if (!adminDao.modifyAdmin(target)) {
            error(resp, 500, "数据库更新失败");
        } else {
            success(resp, "修改成功");
        }
    }

    /* ---------- DTO & utils ---------- */
    private static class ModifyDTO {
        String loginName;
        String fullName;
        Integer departmentId;
        Integer authStatus;
        Integer adminRole;
        Boolean resetPassword;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private void success(HttpServletResponse resp, String msg) throws IOException {
        JsonObject res = new JsonObject();
        res.addProperty("code", 200);
        res.addProperty("msg", msg);
        resp.getWriter().print(res.toString());
    }

    private void error(HttpServletResponse resp, int code, String msg) throws IOException {
        JsonObject res = new JsonObject();
        res.addProperty("code", code);
        res.addProperty("msg", msg);
        resp.setStatus(code);
        resp.getWriter().print(res.toString());
    }
}
