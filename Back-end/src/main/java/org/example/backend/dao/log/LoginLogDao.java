package org.example.backend.dao.log;

import org.example.backend.dao.BaseDao;
import org.example.backend.model.LoginLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginLogDao extends BaseDao {

    public void addLoginLog(LoginLog log) {
        String sql = "INSERT INTO login_log (admin_id, login_time, login_status) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, log.getAdminId());
            stmt.setTimestamp(2, log.getLoginTime());
            stmt.setInt(3, log.getLoginStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // 可以添加日志记录或抛出自定义异常
        }
    }

    public List<LoginLog> queryWithFilters(Integer adminId, Timestamp start, Timestamp end, Integer loginStatus, int page, int pageSize) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT * FROM login_log WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (adminId != null) {
            sql.append(" AND admin_id = ?");
            params.add(adminId);
        }
        if (start != null && end != null) {
            sql.append(" AND login_time BETWEEN ? AND ?");
            params.add(start);
            params.add(end);
        }
        if (loginStatus != null) {
            sql.append(" AND login_status = ?");
            params.add(loginStatus);
        }
        sql.append(" ORDER BY login_time DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<LoginLog> logs = new ArrayList<>();
        lookupConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoginLog log = new LoginLog();
                    log.setId(rs.getInt("id"));
                    log.setAdminId(rs.getInt("admin_id"));
                    log.setLoginTime(rs.getTimestamp("login_time"));
                    log.setLoginStatus(rs.getInt("login_status"));
                    logs.add(log);
                }
            }
        } finally {
            releaseConnection();
        }
        return logs;
    }

    public long countWithFilters(Integer adminId, Timestamp start, Timestamp end, Integer loginStatus) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM login_log WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (adminId != null) {
            sql.append(" AND admin_id = ?");
            params.add(adminId);
        }
        if (start != null && end != null) {
            sql.append(" AND login_time BETWEEN ? AND ?");
            params.add(start);
            params.add(end);
        }
        if (loginStatus != null) {
            sql.append(" AND login_status = ?");
            params.add(loginStatus);
        }

        lookupConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } finally {
            releaseConnection();
        }
        return 0;
    }
}