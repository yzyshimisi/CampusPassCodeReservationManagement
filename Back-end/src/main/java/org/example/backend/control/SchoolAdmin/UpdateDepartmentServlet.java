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


@WebServlet("/api/schoolAdmin/updateDepart")
public class UpdateDepartmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        JSONObject jsonData = Tools.getRequestJsonData(request);

        DepartmentDao departmentDao = new DepartmentDao();

        try{
            departmentDao.lookupConnection();

            List<String> fieldNames = Arrays.asList("departmentType", "departmentName", "id");
            if(Tools.areRequestFieldNull(jsonData, fieldNames)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            int id = jsonData.getIntValue("id");
            int departmentType = jsonData.getIntValue("departmentType");
            String departmentName = jsonData.getString("departmentName");

            if(!(departmentType>=0 && departmentType<=2)){
                throw new Exception("{ \"code\": 420, \"msg\": \"请求数据错误\", \"data\": { } }");
            }

            // 更新现有部门
            Department dept = departmentDao.findById(id);
            if (dept == null) {
                throw new Exception("{ \"code\": 404, \"msg\": \"未找到对应的部门信息\", \"data\": { } }");
            }

            // DepartmentName 唯一性检查
            Department department = departmentDao.findByDepartmentName(departmentName);
            if(department != null && department.getId()!=id){
                throw new Exception("{ \"code\": 500, \"msg\": \"该部门名称已存在\", \"data\": { } }");
            }

            dept.setDepartmentType(departmentType);     // 更新 departmentType，如果提供
            dept.setDepartmentName(departmentName);     // 更新 departmentName

            if (departmentDao.updateDepartment(dept)) {
                out.print("{\"code\": 200, \"msg\": \"更新成功\", \"data\": { } }");
            } else {
                throw new Exception("{ \"code\": 500, \"msg\": \"更新失败\", \"data\": { } }");
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