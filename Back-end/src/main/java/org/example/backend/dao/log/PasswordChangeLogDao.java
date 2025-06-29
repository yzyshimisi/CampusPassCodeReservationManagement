package org.example.backend.dao.log;

import org.example.backend.dao.BaseDao;
import org.example.backend.model.PasswordChangeLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordChangeLogDao extends BaseDao {

    public List<PasswordChangeLog> queryWithFilters(Integer adminId, Integer operation, int page, int pageSize) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT * FROM password_change_log WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (adminId != null) {
            sql.append(" AND admin_id = ?");
            params.add(adminId);
        }
        if (operation != null) {
            sql.append(" AND operation = ?");
            params.add(operation);
        }
        sql.append(" ORDER BY change_time DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<PasswordChangeLog> logs = new ArrayList<>();
        lookupConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PasswordChangeLog log = new PasswordChangeLog();
                    log.setId(rs.getInt("id"));
                    log.setAdminId(rs.getInt("admin_id"));
                    log.setOperation(rs.getInt("operation"));
                    log.setchange_time(rs.getTimestamp("change_time"));
                    logs.add(log);
                }
            }
        } finally {
            releaseConnection();
        }
        return logs;
    }

    public long countWithFilters(Integer adminId, Integer operation) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM password_change_log WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (adminId != null) {
            sql.append(" AND admin_id = ?");
            params.add(adminId);
        }
        if (operation != null) {
            sql.append(" AND operation = ?");
            params.add(operation);
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