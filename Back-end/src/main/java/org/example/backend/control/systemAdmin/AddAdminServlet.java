package org.example.backend.control.systemAdmin;

import cn.hutool.crypto.SmUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.backend.dao.AdminDao;
import org.example.backend.dao.DepartmentDao;
import org.example.backend.model.Admin;
import org.example.backend.utils.Tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@WebServlet("/api/systemAdmin/add")
public class AddAdminServlet extends HttpServlet {

    private static final String DEFAULT_PWD = "12345678";
    private final DepartmentDao deptDao = new DepartmentDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

//        Integer opId = Integer.parseInt(claims.get("id").toString());
//        System.out.println("操作者 ID：" + opId);
//        OperatorContext.set(opId); // ✅ 注入上下文peining@

        JSONObject jsonData = Tools.getRequestJsonData(req);

        AdminDao adminDao = new AdminDao();
        try{
            adminDao.lookupConnection();

            List<String> fieldNames = Arrays.asList("adminRole", "fullName", "loginName","phone");
            if(Tools.areRequestFieldNull(jsonData,fieldNames)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            int adminRole = jsonData.getIntValue("adminRole");
            String fullName = jsonData.getString("fullName");
            String loginName = jsonData.getString("loginName");
            String phone = jsonData.getString("phone");

            if(!(adminRole>=0 && adminRole<=3) || !Tools.isValidPhone(phone)) {
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            if (adminDao.findByLoginName(loginName) != null) {
                throw new Exception("{ \"code\": 409, \"msg\": \"登录名已存在\", \"data\": { } }");
            }

            Integer departmentId = jsonData.getInteger("departmentId");
            Integer authStatus = jsonData.getInteger("authStatus");

            if (adminRole == 3 && departmentId!=null && !deptDao.exists(departmentId)) {
                throw new Exception("{ \"code\": 400, \"msg\": \"department 不存在\", \"data\": { } }");
            }

            // 构建 Admin 实体并调用 DAO 保存
            Admin a = new Admin();
            a.setAdminRole(adminRole);
            a.setFullName(fullName);
            a.setLoginName(loginName);
            a.setLoginPassword(SmUtil.sm3(DEFAULT_PWD));

            Timestamp now = new Timestamp(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(now.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, -90);
            Timestamp newTimestamp = new Timestamp(calendar.getTimeInMillis());

            a.setLastPasswordUpdate(newTimestamp);
            a.setDepartmentId(departmentId);
            a.setAuthStatus(authStatus);
            a.setPhone(phone);
            a.setLastLoginFailTime(newTimestamp);
            a.setLoginFailCount(0);

            if (!adminDao.addAdmin(a)) {
                throw new Exception("{ \"code\": 500, \"msg\": \"数据库写入失败\", \"data\": { } }");
            }

            out.print("{ \"code\": 200, \"msg\": \"添加成功\", \"data\": { } }");

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
