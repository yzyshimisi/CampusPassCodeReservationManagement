package org.example.backend.control.SchoolAdmin;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.DepartmentDao;
import org.example.backend.model.Department;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/schoolAdmin/fuzzySearchDepart")
public class FuzzySearchDepartmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        DepartmentDao departmentDao = new DepartmentDao();

        try{
            departmentDao.lookupConnection();

            // 提取参数
            String fuzzyName = request.getParameter("fuzzyName");
            if (fuzzyName == null || fuzzyName.trim().isEmpty()) {
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            List<Department> departments = departmentDao.findByFuzzyDepartmentName(fuzzyName);
            if(departments==null){
                throw new Exception("{ \"code\": 420, \"msg\": \"未找到记录\", \"data\": { } }");
            }

            JSONObject jsonRes = new JSONObject();
            jsonRes.put("code", 200);
            jsonRes.put("msg", "ok");
            jsonRes.put("data", departments);

            out.println(jsonRes.toJSONString());

            departmentDao.releaseConnection();
        }catch (Exception e){
            try{
                departmentDao.releaseConnection();
            }catch (Exception e2){
                e2.printStackTrace();
            }
            out.print(e.getMessage());
        }
    }
}