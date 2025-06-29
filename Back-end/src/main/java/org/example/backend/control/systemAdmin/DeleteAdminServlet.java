package org.example.backend.control.systemAdmin;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.backend.dao.AdminDao;
import org.example.backend.utils.Tools;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/systemAdmin/delete")
public class DeleteAdminServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

//        Integer opId = Integer.parseInt(claims.get("id").toString());
//        System.out.println("操作者 ID：" + opId);
//        OperatorContext.set(opId); // ✅ 注入上下文

        // 读取请求体
        JSONObject jsonData = Tools.getRequestJsonData(req);
        AdminDao adminDao = new AdminDao();

        try{
            adminDao.lookupConnection();

            String loginName = jsonData.getString("loginName");

            if(loginName==null || loginName.isEmpty()){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            if (!adminDao.deleteAdmin(loginName)) {
                throw new Exception("{ \"code\": 404, \"msg\": \"删除失败：用户不存在或已删除\", \"data\": { } }");
            }

            out.print("{ \"code\": 200, \"msg\": \"ok\", \"data\": { } }");

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
