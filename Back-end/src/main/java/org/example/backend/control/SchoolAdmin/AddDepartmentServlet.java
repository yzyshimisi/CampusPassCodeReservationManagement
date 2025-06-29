package org.example.backend.control.SchoolAdmin;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.dao.DepartmentDao;
import org.example.backend.model.Department;

import org.example.backend.utils.Tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@WebServlet("/api/schoolAdmin/addDepart")
public class AddDepartmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        JSONObject jsonData = Tools.getRequestJsonData(request);

        DepartmentDao departmentDao = new DepartmentDao();

        try{
            departmentDao.lookupConnection();

            List<String> fieldNames = Arrays.asList("departmentType", "departmentName");
            if(Tools.areRequestFieldNull(jsonData, fieldNames)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            int departmentType = jsonData.getIntValue("departmentType");
            String departmentName = jsonData.getString("departmentName");

            if(!(departmentType>=0 && departmentType<=2)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            // 检查 departmentName 唯一性
            Department existingDept = departmentDao.findByDepartmentName(departmentName);
            if (existingDept != null) {
                throw new Exception("{ \"code\": 400, \"msg\": \"Department name already exists\", \"data\": { } }");
            }

            // 创建并保存部门
            Department dept = new Department();
            dept.setDepartmentType(departmentType);
            dept.setDepartmentName(departmentName);


            if (departmentDao.insertDepartment(dept)) {
                out.print("{\"code\": 200, \"msg\": \"ok\", \"data\": { } }");
            } else {
                throw new Exception("{ \"code\": 500, \"msg\": \"Failed to add department\", \"data\": { } }");
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