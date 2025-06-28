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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

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
            error(resp, 401, "未登录：缺少 token");
            return;
        }

        Jwt jwtUtil = new Jwt();
        Claims claims = jwtUtil.validateJwt("jwtToken=" + jwtToken);
        if (claims == null) {
            error(resp, 401, "登录失效或 token 非法");
            return;
        }

        Integer operatorId = Integer.parseInt(claims.get("id").toString());
        OperatorContext.set(operatorId);

        ModifyDTO dto = new Gson().fromJson(req.getReader(), ModifyDTO.class);
        if (dto == null || isBlank(dto.loginName)) {
            error(resp, 400, "loginName 不能为空");
            return;
        }

        Admin admin = adminDao.findByLoginName(dto.loginName);
        if (admin == null) {
            error(resp, 404, "管理员不存在");
            return;
        }

        if (Integer.valueOf(3).equals(dto.adminRole)) {
            if (dto.departmentId == null || dto.authStatus == null) {
                error(resp, 400, "切换为部门管理员必须提供 departmentId 和 authStatus");
                return;
            }
        }

        // 只修改基础信息，不改密码
        if (!isBlank(dto.fullName)) admin.setFullName(dto.fullName);
        if (dto.departmentId != null) admin.setDepartmentId(dto.departmentId);
        if (dto.authStatus != null) admin.setAuthStatus(dto.authStatus);
        if (dto.adminRole != null) admin.setAdminRole(dto.adminRole);

        if (!adminDao.modifyAdmin(admin)) {
            error(resp, 500, "更新失败");
        } else {
            success(resp, "修改成功");
        }
    }

    private static class ModifyDTO {
        String loginName;
        String fullName;
        Integer departmentId;
        Integer authStatus;
        Integer adminRole;
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
