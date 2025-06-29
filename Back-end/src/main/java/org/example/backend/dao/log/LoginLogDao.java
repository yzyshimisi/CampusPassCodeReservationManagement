package org.example.backend.dao.log;

import org.example.backend.dao.BaseDao;
import org.example.backend.model.LoginLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginLogDao extends BaseDao {

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