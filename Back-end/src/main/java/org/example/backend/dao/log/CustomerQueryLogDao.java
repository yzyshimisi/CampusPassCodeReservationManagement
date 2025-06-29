package org.example.backend.dao.log;

import org.example.backend.dao.BaseDao;
import org.example.backend.model.CustomerQueryLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerQueryLogDao extends BaseDao {

    public List<CustomerQueryLog> queryWithFilters(String fullName, String maskedIdNumber, int page, int pageSize) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT * FROM customer_query_log WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (fullName != null && !fullName.isEmpty()) {
            sql.append(" AND full_name LIKE ?");
            params.add("%" + fullName + "%");
        }
        if (maskedIdNumber != null && !maskedIdNumber.isEmpty()) {
            sql.append(" AND masked_id_number = ?");
            params.add(maskedIdNumber);
        }
        sql.append(" ORDER BY query_time DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<CustomerQueryLog> logs = new ArrayList<>();
        lookupConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CustomerQueryLog log = new CustomerQueryLog();
                    log.setId(rs.getInt("id"));
                    log.setFullName(rs.getString("full_name"));
                    log.setMaskedIdNumber(rs.getString("masked_id_number"));
                    log.setQueryTime(rs.getTimestamp("query_time"));
                    logs.add(log);
                }
            }
        } finally {
            releaseConnection();
        }
        return logs;
    }

    public long countWithFilters(String fullName, String maskedIdNumber) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM customer_query_log WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (fullName != null && !fullName.isEmpty()) {
            sql.append(" AND full_name LIKE ?");
            params.add("%" + fullName + "%");
        }
        if (maskedIdNumber != null && !maskedIdNumber.isEmpty()) {
            sql.append(" AND masked_id_number = ?");
            params.add(maskedIdNumber);
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