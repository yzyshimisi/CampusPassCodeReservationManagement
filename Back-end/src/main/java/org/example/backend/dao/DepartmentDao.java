package org.example.backend.dao;

import org.example.backend.model.Department;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDao extends BaseDao {

    /* ------------------ 查询全部 ------------------ */
    public List<Department> findAllDepartments() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM department";

        try(PreparedStatement ps = getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery();){

            while (rs.next()) {
                Department d = new Department();
                d.setId(rs.getInt("id"));
                d.setDepartmentType(rs.getInt("department_type"));
                d.setDepartmentName(rs.getString("department_name"));
                list.add(d);
            }

            return list;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    /* ------------------ 判断是否存在 ------------------ */
    public boolean exists(int id) {
        String sql = "SELECT 1 FROM department WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();   // 查询到即返回 true
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ------------------ 按 ID 查询 ------------------ */
    public Department findById(int id) {
        String sql = "SELECT * FROM department WHERE id = ?";
        Department dept = null;

        try(PreparedStatement ps = getConnection().prepareStatement(sql);){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery();){
                if (rs.next()) {
                    dept = new Department();
                    dept.setId(rs.getInt("id"));
                    dept.setDepartmentType(rs.getInt("department_type"));
                    dept.setDepartmentName(rs.getString("department_name"));
                }
                return dept;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    /* ------------------ 新增 ------------------ */
    public boolean insertDepartment(Department d){
        String sql = "INSERT INTO department (department_type, department_name) VALUES (?, ?)";

        try(PreparedStatement ps = getConnection().prepareStatement(sql);){
            ps.setInt(1, d.getDepartmentType());
            ps.setString(2, d.getDepartmentName());

            int rows = ps.executeUpdate();
            return rows > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /* ------------------ 更新 ------------------ */
    public boolean updateDepartment(Department d){
        String sql = "UPDATE department SET department_type = ?, department_name = ? WHERE id = ?";

        try(PreparedStatement ps = getConnection().prepareStatement(sql);){
            ps.setInt(1, d.getDepartmentType());
            ps.setString(2, d.getDepartmentName());
            ps.setInt(3, d.getId());

            int rows = ps.executeUpdate();
            return rows > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /* ------------------ 删除 ------------------ */
    public boolean deleteDepartment(int id){
        String sql = "DELETE FROM department WHERE id = ?";

        try(PreparedStatement ps = getConnection().prepareStatement(sql);){
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Department> findByFuzzyDepartmentName(String fuzzyName) {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM department WHERE department_name LIKE ?";

        try(PreparedStatement stmt = getConnection().prepareStatement(sql);){
            stmt.setString(1, "%" + fuzzyName + "%"); // 使用 % 实现模糊匹配
            try(ResultSet rs = stmt.executeQuery();){
                while (rs.next()) {
                    Department dept = new Department();
                    dept.setId(rs.getInt("id"));
                    dept.setDepartmentType(rs.getInt("department_type"));
                    dept.setDepartmentName(rs.getString("department_name"));
                    departments.add(dept);
                }

                return departments;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public Department findByDepartmentName(String departmentName) {
        String sql = "SELECT * FROM department WHERE department_name = ? LIMIT 1";

        try(PreparedStatement stmt = getConnection().prepareStatement(sql);){
            stmt.setString(1, departmentName);
            try(ResultSet rs = stmt.executeQuery();){
                Department dept = null;
                if (rs.next()) {
                    dept = new Department();
                    dept.setId(rs.getInt("id"));
                    dept.setDepartmentType(rs.getInt("department_type"));
                    dept.setDepartmentName(rs.getString("department_name"));
                }
                return dept;
            }
        }catch ( SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteByDepartmentName(String departmentName)  {
        String sql = "DELETE FROM department WHERE department_name = ?";

        try(PreparedStatement stmt = getConnection().prepareStatement(sql);){
            stmt.setString(1, departmentName);
            int rows = stmt.executeUpdate();

            return rows > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}