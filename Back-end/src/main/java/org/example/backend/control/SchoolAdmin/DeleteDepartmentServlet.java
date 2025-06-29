package org.example.backend.control.SchoolAdmin;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.DepartmentDao;
import org.example.backend.utils.Tools;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/schoolAdmin/deleteDepart")
public class DeleteDepartmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        JSONObject jsonData = Tools.getRequestJsonData(request);

        DepartmentDao departmentDao = new DepartmentDao();

        try{
            departmentDao.lookupConnection();

            // 提取参数
            String departmentName = jsonData.getString("departmentName");
            if(departmentName==null || departmentName.isEmpty()){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            if (departmentDao.deleteByDepartmentName(departmentName)) {
                out.println("{\"code\": 200, \"msg\": \"ok\", \"data\": { } }");
            } else {
                throw new Exception("{\"code\": 404, \"msg\": \"Department not found\", \"data\": { } }");
            }

            departmentDao.releaseConnection();
        }catch (Exception e){
            try{
                departmentDao.releaseConnection();
            }catch (Exception e2){
                e2.printStackTrace();
            }
            out.println(e.getMessage());
        }
    }
}