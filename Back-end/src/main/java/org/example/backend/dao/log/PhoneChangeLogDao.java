package org.example.backend.dao.log;

import org.example.backend.dao.BaseDao;
import org.example.backend.model.PhoneChangeLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhoneChangeLogDao extends BaseDao {

    public List<PhoneChangeLog> queryWithFilters(Integer adminId, String newPhone, int page, int pageSize) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT * FROM phone_change_log WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (adminId != null) {
            sql.append(" AND admin_id = ?");
            params.add(adminId);
        }
        if (newPhone != null && !newPhone.isEmpty()) {
            sql.append(" AND new_phone = ?");
            params.add(newPhone);
        }
        sql.append(" ORDER BY change_time DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<PhoneChangeLog> logs = new ArrayList<>();
        lookupConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhoneChangeLog log = new PhoneChangeLog();
                    log.setId(rs.getInt("id"));
                    log.setAdminId(rs.getInt("admin_id"));
                    log.setold_phone(rs.getString("old_phone"));
                    log.setnew_phone(rs.getString("new_phone"));
                    log.setchange_time(rs.getTimestamp("change_time"));
                    logs.add(log);
                }
            }
        } finally {
            releaseConnection();
        }
        return logs;
    }

    public long countWithFilters(Integer adminId, String newPhone) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM phone_change_log WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (adminId != null) {
            sql.append(" AND admin_id = ?");
            params.add(adminId);
        }
        if (newPhone != null && !newPhone.isEmpty()) {
            sql.append(" AND new_phone = ?");
            params.add(newPhone);
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