package org.example.backend.dao;

import org.example.backend.model.Admin;
import org.example.backend.utils.OperatorContext;

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

    public boolean addAdmin(Admin admin) {
        String sqlInsert = """
        INSERT INTO admins
            (admin_role, full_name, login_name, login_password,
             last_password_update, department_id, phone,
             auth_status, login_fail_count, last_login_fail_time)
        VALUES (?,?,?,?,?,?,?,?,?,?)
    """;

        Integer operatorId = OperatorContext.get();

        try {
            lookupConnection();
            connection.setAutoCommit(false);

            try (
                    Statement stmtSet = connection.createStatement();
                    PreparedStatement ps = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)
            ) {
                // 设置操作者 ID
                if (operatorId != null) {
                    String setSql = "SET LOCAL app.operator_id = " + operatorId;
                    stmtSet.execute(setSql);
                }

                ps.setInt(1, admin.getAdminRole());
                ps.setString(2, admin.getFullName());
                ps.setString(3, admin.getLoginName());
                ps.setString(4, admin.getLoginPassword());
                ps.setTimestamp(5, admin.getLastPasswordUpdate());

                if (admin.getDepartmentId() == null) {
                    ps.setNull(6, Types.INTEGER);
                } else {
                    ps.setInt(6, admin.getDepartmentId());
                }

                ps.setString(7, admin.getPhone());
                ps.setInt(8, admin.getAuthStatus());
                ps.setInt(9, admin.getLoginFailCount());
                ps.setTimestamp(10, admin.getLastLoginFailTime());

                int rows = ps.executeUpdate();
                if (rows == 1) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) admin.setId(rs.getInt(1));
                    }
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            } catch (Exception e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            } finally {
                releaseConnection();
            }

        } catch (Exception e) {
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

    /*------------------ 4. 更新管理员信息 ------------------*/
    public boolean modifyAdmin(Admin admin) {
        String sqlUpdate = """
        UPDATE admins SET
            admin_role = ?, full_name = ?, login_password = ?,
            last_password_update = ?, department_id = ?, phone = ?,
            auth_status = ?, login_fail_count = ?, last_login_fail_time = ?
        WHERE login_name = ?
    """;

        Integer operatorId = OperatorContext.get();

        try {
            lookupConnection();
            connection.setAutoCommit(false);  // 显式开启事务，确保 SET LOCAL 有效

            try (
                    Statement stmtSet = connection.createStatement();
                    PreparedStatement ps = connection.prepareStatement(sqlUpdate)
            ) {
                // ✨ 注入操作者 ID 到 PostgreSQL 的会话变量
                if (operatorId != null) {
                    String setSql = "SET LOCAL app.operator_id = " + operatorId;
                    stmtSet.execute(setSql);
                }

                // 设置更新参数
                ps.setInt(1, admin.getAdminRole());
                ps.setString(2, admin.getFullName());
                ps.setString(3, admin.getLoginPassword());
                ps.setTimestamp(4, admin.getLastPasswordUpdate());

                if (admin.getDepartmentId() == null) {
                    ps.setNull(5, Types.INTEGER);
                } else {
                    ps.setInt(5, admin.getDepartmentId());
                }

                ps.setString(6, admin.getPhone());
                ps.setInt(7, admin.getAuthStatus());
                ps.setInt(8, admin.getLoginFailCount());
                ps.setTimestamp(9, admin.getLastLoginFailTime());
                ps.setString(10, admin.getLoginName());

                boolean result = ps.executeUpdate() == 1;
                connection.commit();
                return result;

            } catch (Exception e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            } finally {
                releaseConnection();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteAdmin(String loginName) {
        String sqlDelete = "DELETE FROM admins WHERE login_name = ?";
        Integer operatorId = OperatorContext.get();  // 从上下文获取操作者 ID

        try {
            lookupConnection();  // 拿到统一事务连接
            connection.setAutoCommit(false);  // 显式开启事务

            try (
                    Statement stmtSet = connection.createStatement();
                    PreparedStatement psDelete = connection.prepareStatement(sqlDelete)
            ) {
                // ✨ 不能用占位符，必须拼接
                if (operatorId != null) {
                    String setSql = "SET LOCAL app.operator_id = " + operatorId;
                    stmtSet.execute(setSql);
                }

                psDelete.setString(1, loginName);
                boolean result = psDelete.executeUpdate() == 1;

                connection.commit();
                return result;
            } catch (Exception e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            } finally {
                releaseConnection();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /*------------------ 6. 全名模糊查询 ------------------*/
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
