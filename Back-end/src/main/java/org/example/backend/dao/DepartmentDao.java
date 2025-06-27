package org.example.backend.dao;

import org.example.backend.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDao extends BaseDao {

    public List<Department> findAllDepartments() throws Exception {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM department";

        lookupConnection();
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Department dept = new Department();
            dept.setId(rs.getInt("id"));
            dept.setDepartmentType(rs.getString("department_type"));
            dept.setDepartmentName(rs.getString("department_name"));
            departments.add(dept);
        }

        rs.close();
        stmt.close();
        releaseConnection();
        return departments;
    }

    public Department findById(int id) throws Exception {
        String sql = "SELECT * FROM department WHERE id = ?";
        Department dept = null;

        lookupConnection();
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            dept = new Department();
            dept.setId(rs.getInt("id"));
            dept.setDepartmentType(rs.getString("department_type"));
            dept.setDepartmentName(rs.getString("department_name"));
        }

        rs.close();
        stmt.close();
        releaseConnection();
        return dept;
    }

    public boolean insertDepartment(Department dept) throws Exception {
        String sql = "INSERT INTO department (department_type, department_name) VALUES (?, ?)";

        lookupConnection();
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setString(1, dept.getDepartmentType());
        stmt.setString(2, dept.getDepartmentName());
        int rows = stmt.executeUpdate();

        stmt.close();
        releaseConnection();
        return rows > 0;
    }

    public boolean updateDepartment(Department dept) throws Exception {
        String sql = "UPDATE department SET department_type = ?, department_name = ? WHERE id = ?";

        lookupConnection();
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setString(1, dept.getDepartmentType());
        stmt.setString(2, dept.getDepartmentName());
        stmt.setInt(3, dept.getId());
        int rows = stmt.executeUpdate();

        stmt.close();
        releaseConnection();
        return rows > 0;
    }

    public boolean deleteDepartment(int id) throws Exception {
        String sql = "DELETE FROM department WHERE id = ?";

        lookupConnection();
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setInt(1, id);
        int rows = stmt.executeUpdate();

        stmt.close();
        releaseConnection();
        return rows > 0;
    }
}
