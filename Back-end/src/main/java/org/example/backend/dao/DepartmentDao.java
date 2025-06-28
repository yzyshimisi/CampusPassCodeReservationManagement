package org.example.backend.dao;

import org.example.backend.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDao extends BaseDao {

    /* ------------------ 查询全部 ------------------ */
    public List<Department> findAllDepartments() throws Exception {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM department";

        lookupConnection();
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Department d = new Department();
            d.setId(rs.getInt("id"));
            d.setDepartmentType(rs.getString("department_type"));
            d.setDepartmentName(rs.getString("department_name"));
            list.add(d);
        }
        rs.close();
        ps.close();
        releaseConnection();
        return list;
    }

    /* ------------------ 判断是否存在 ------------------ */
    public boolean exists(int id) {
        String sql = "SELECT 1 FROM department WHERE id = ?";
        try {
            lookupConnection();                               // ① 拿连接
            try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();                         // ② 查询到即返回 true
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { releaseConnection(); } catch (Exception ignored) {}  // ③ 无论如何都归还连接
        }
    }


    /* ------------------ 按 ID 查询 ------------------ */
    public Department findById(int id) throws Exception {
        String sql = "SELECT * FROM department WHERE id = ?";
        Department dept = null;

        lookupConnection();
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            dept = new Department();
            dept.setId(rs.getInt("id"));
            dept.setDepartmentType(rs.getString("department_type"));
            dept.setDepartmentName(rs.getString("department_name"));
        }
        rs.close();
        ps.close();
        releaseConnection();
        return dept;
    }

    /* ------------------ 新增 ------------------ */
    public boolean insertDepartment(Department d) throws Exception {
        String sql = "INSERT INTO department (department_type, department_name) VALUES (?, ?)";

        lookupConnection();
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setString(1, d.getDepartmentType());
        ps.setString(2, d.getDepartmentName());
        int rows = ps.executeUpdate();
        ps.close();
        releaseConnection();
        return rows > 0;
    }

    /* ------------------ 更新 ------------------ */
    public boolean updateDepartment(Department d) throws Exception {
        String sql = "UPDATE department SET department_type = ?, department_name = ? WHERE id = ?";

        lookupConnection();
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setString(1, d.getDepartmentType());
        ps.setString(2, d.getDepartmentName());
        ps.setInt(3, d.getId());
        int rows = ps.executeUpdate();
        ps.close();
        releaseConnection();
        return rows > 0;
    }

    /* ------------------ 删除 ------------------ */
    public boolean deleteDepartment(int id) throws Exception {
        String sql = "DELETE FROM department WHERE id = ?";

        lookupConnection();
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setInt(1, id);
        int rows = ps.executeUpdate();
        ps.close();
        releaseConnection();
        return rows > 0;
    }
}
