package org.example.backend.dao;

import org.example.backend.model.Admin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDao extends BaseDao {

    /*-------------------------------------------------
     * 私有工具：把 ResultSet 一行映射成 Admin 对象，减少代码重复
     *------------------------------------------------*/
    private Admin mapRow(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getInt("id"));
        admin.setAdminRole(rs.getInt("admin_role"));
        admin.setFullName(rs.getString("full_name"));
        admin.setLoginName(rs.getString("login_name"));
        admin.setLoginPassword(rs.getString("login_password"));
        admin.setLastPasswordUpdate(rs.getTimestamp("last_password_update"));
        // department_id 允许 NULL
        int depId = rs.getInt("department_id");
        admin.setDepartmentId(rs.wasNull() ? null : depId);
        admin.setPhone(rs.getString("phone"));
        admin.setAuthStatus(rs.getInt("auth_status"));
        admin.setLoginFailCount(rs.getInt("login_fail_count"));
        admin.setLastLoginFailTime(rs.getTimestamp("last_login_fail_time"));
        return admin;
    }

    /*------------------ 1. 新增管理员 ------------------*/
    public boolean addAdmin(Admin admin) {
        String sql = """
            INSERT INTO admins
                (admin_role, full_name, login_name, login_password,
                 last_password_update, department_id, phone,
                 auth_status, login_fail_count, last_login_fail_time)
            VALUES (?,?,?,?,?,?,?,?,?,?)
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt   (1, admin.getAdminRole());
            ps.setString(2, admin.getFullName());
            ps.setString(3, admin.getLoginName());
            ps.setString(4, admin.getLoginPassword());
            ps.setTimestamp(5, admin.getLastPasswordUpdate());
            /* department_id 可能为 NULL */
            if (admin.getDepartmentId() == null) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, admin.getDepartmentId());
            }
            ps.setString(7, admin.getPhone());
            ps.setInt   (8, admin.getAuthStatus());
            ps.setInt   (9, admin.getLoginFailCount());
            ps.setTimestamp(10, admin.getLastLoginFailTime());

            int rows = ps.executeUpdate();
            if (rows == 1) {                         // 拿到自增 id 回填到对象里
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) admin.setId(rs.getInt(1));
                }
            }
            return rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*------------------ 2. 查询全部 ------------------*/
    public List<Admin> findAllAdmin() {
        String sql = "SELECT * FROM admins ORDER BY id";
        List<Admin> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /*------------------ 3. 按 login_name 精确查询 ------------------*/
    public Admin findByLoginName(String loginName) {
        String sql = "SELECT * FROM admins WHERE login_name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, loginName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*------------------ 4. 按 id 查询 ------------------*/
    public Admin findAdminById(int id) {
        String sql = "SELECT * FROM admins WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*------------------ 5. 更新管理员信息 ------------------*/
    public boolean modifyAdmin(Admin admin) {
        String sql = """
            UPDATE admins SET
                admin_role = ?, full_name = ?, login_password = ?,
                last_password_update = ?, department_id = ?, phone = ?,
                auth_status = ?, login_fail_count = ?, last_login_fail_time = ?
            WHERE login_name = ?
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt   (1, admin.getAdminRole());
            ps.setString(2, admin.getFullName());
            ps.setString(3, admin.getLoginPassword());
            ps.setTimestamp(4, admin.getLastPasswordUpdate());
            if (admin.getDepartmentId() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, admin.getDepartmentId());
            }
            ps.setString(6, admin.getPhone());
            ps.setInt   (7, admin.getAuthStatus());
            ps.setInt   (8, admin.getLoginFailCount());
            ps.setTimestamp(9, admin.getLastLoginFailTime());
            ps.setString(10, admin.getLoginName());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*------------------ 6. 删除（按 login_name） ------------------*/
    public boolean deleteAdmin(String loginName) {
        String sql = "DELETE FROM admins WHERE login_name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loginName);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*------------------ 7. 全名模糊查询 ------------------*/
    public List<Admin> findByFuzzyName(String fuzzyName) {
        String sql = "SELECT * FROM admins WHERE full_name ILIKE ?"; // PostgreSQL 模糊匹配不区分大小写
        List<Admin> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + fuzzyName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}