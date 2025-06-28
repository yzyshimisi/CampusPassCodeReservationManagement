package org.example.backend.control.systemAdmin;

import com.google.gson.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.backend.dao.AdminDao;
import org.example.backend.utils.Jwt;
import org.example.backend.utils.OperatorContext;

import java.io.IOException;

@WebServlet("/api/systemAdmin/deleteAdmin")
public class DeleteAdminServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final AdminDao adminDao = new AdminDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        // ✅ Step 1：从 Cookie 中提取 token 并验证
        String jwtToken = null;
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("jwtToken".equals(c.getName())) {
                    jwtToken = c.getValue();
                    break;
                }
            }
        }

        if (jwtToken == null || jwtToken.isBlank()) {
            error(resp, 401, "未登录，token 为空");
            return;
        }

        Jwt jwtUtil = new Jwt();
        Claims claims = jwtUtil.validateJwt("jwtToken=" + jwtToken);
        if (claims == null) {
            error(resp, 401, "token 无效或已过期");
            return;
        }

        Integer opId = Integer.parseInt(claims.get("id").toString());
        System.out.println("操作者 ID：" + opId);
        OperatorContext.set(opId); // ✅ 注入上下文

        // ✅ Step 2：读取请求体
        DeleteDTO dto = gson.fromJson(req.getReader(), DeleteDTO.class);
        if (dto == null || dto.loginName == null || dto.loginName.isBlank()) {
            error(resp, 400, "loginName 参数缺失");
            return;
        }

        if (!adminDao.deleteAdmin(dto.loginName)) {
            error(resp, 404, "删除失败：用户不存在或已删除");
            return;
        }

        ok(resp, "删除成功");
    }

    private static class DeleteDTO {
        String loginName;
    }

    private void ok(HttpServletResponse r, String msg) throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("code", 200);
        obj.addProperty("msg", msg);
        r.getWriter().print(obj);
    }

    private void error(HttpServletResponse r, int code, String msg) throws IOException {
        r.setStatus(code);
        JsonObject obj = new JsonObject();
        obj.addProperty("code", code);
        obj.addProperty("msg", msg);
        r.getWriter().print(obj);
    }
}
