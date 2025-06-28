package org.example.backend.control.systemAdmin;

import com.google.gson.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.backend.dao.AdminDao;
import org.example.backend.dao.DepartmentDao;
import org.example.backend.model.Admin;
import org.example.backend.model.SM3Util;
import org.example.backend.utils.Jwt;
import org.example.backend.utils.OperatorContext;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Timestamp;

@WebServlet("/api/systemAdmin/addAdmin")
public class AddAdminServlet extends HttpServlet {

    private static final String DEFAULT_PWD = "Wangpeining@123";
    private final Gson gson = new Gson();
    private final AdminDao adminDao = new AdminDao();
    private final DepartmentDao deptDao = new DepartmentDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        // ✅ Step 1：提取操作者 ID 并注入上下文
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

        // ✅ Step 2：解析请求参数并校验
        AddDTO dto = gson.fromJson(req.getReader(), AddDTO.class);

        if (dto == null || dto.role == null || blank(dto.name) || blank(dto.loginName)) {
            error(resp, 400, "role/name/loginName 不能为空");
            return;
        }

        if (adminDao.findByLoginName(dto.loginName) != null) {
            error(resp, 409, "登录名已存在");
            return;
        }

        if (dto.role == 3 && (dto.departmentId == null || dto.authStatus == null)) {
            error(resp, 400, "部门管理员须提供 departmentId & authStatus");
            return;
        }

//        if (dto.role == 3 && !deptDao.exists(dto.departmentId)) {
//            error(resp, 400, "departmentId 不存在");
//            return;
//        }

        // ✅ Step 3：构建 Admin 实体并调用 DAO 保存
        Admin a = new Admin();
        a.setAdminRole(dto.role);
        a.setFullName(dto.name);
        a.setLoginName(dto.loginName);
        try {
            a.setLoginPassword(SM3Util.hash(DEFAULT_PWD));
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            error(resp, 500, "密码加密失败");
            return;
        }
        a.setLastPasswordUpdate(new Timestamp(System.currentTimeMillis()));
        a.setDepartmentId(dto.departmentId);
        a.setAuthStatus(dto.authStatus);
        a.setPhone(dto.phone);
        a.setLoginFailCount(0);

        if (!adminDao.addAdmin(a)) {
            error(resp, 500, "数据库写入失败");
            return;
        }

        ok(resp, "添加成功");
    }

    private static class AddDTO {
        Integer role;
        String name;
        String loginName;
        Integer departmentId;
        Integer authStatus;
        String phone;
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }

    private void ok(HttpServletResponse r, String msg) throws IOException {
        JsonObject res = new JsonObject();
        res.addProperty("code", 200);
        res.addProperty("msg", msg);
        r.getWriter().print(res);
    }

    private void error(HttpServletResponse r, int code, String msg) throws IOException {
        r.setStatus(code);
        JsonObject res = new JsonObject();
        res.addProperty("code", code);
        res.addProperty("msg", msg);
        r.getWriter().print(res);
    }
}
