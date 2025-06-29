package org.example.backend.dao.log;

import org.example.backend.dao.BaseDao;
import org.example.backend.model.AdminOperationLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminOperationLogDao extends BaseDao {

    public List<AdminOperationLog> queryWithFilters(Timestamp start, Timestamp end, Integer operatorId, Integer targetId, Integer operationType, int page, int pageSize) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT * FROM admin_operation_log WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (start != null && end != null) {
            sql.append(" AND operation_time BETWEEN ? AND ?");
            params.add(start);
            params.add(end);
        }
        if (operatorId != null) {
            sql.append(" AND operator_id = ?");
            params.add(operatorId);
        }
        if (targetId != null) {
            sql.append(" AND target_id = ?");
            params.add(targetId);
        }
        if (operationType != null) {
            sql.append(" AND operation_type = ?");
            params.add(operationType);
        }
        sql.append(" ORDER BY operation_time DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<AdminOperationLog> logs = new ArrayList<>();
        lookupConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AdminOperationLog log = new AdminOperationLog();
                    log.setId(rs.getInt("id"));
                    log.setOperatorId(rs.getInt("operator_id"));
                    log.setTargetId(rs.getObject("target_id") != null ? rs.getInt("target_id") : null);
                    log.setOperationType(rs.getInt("operation_type"));
                    log.setOldFullName(rs.getString("old_full_name"));
                    log.setNewFullName(rs.getString("new_full_name"));
                    log.setOldAdminRole(rs.getObject("old_admin_role") != null ? rs.getInt("old_admin_role") : null);
                    log.setNewAdminRole(rs.getObject("new_admin_role") != null ? rs.getInt("new_admin_role") : null);
                    log.setOldDepartmentId(rs.getObject("old_department_id") != null ? rs.getInt("old_department_id") : null);
                    log.setNewDepartmentId(rs.getObject("new_department_id") != null ? rs.getInt("new_department_id") : null);
                    log.setOldAuthStatus(rs.getObject("old_auth_status") != null ? rs.getInt("old_auth_status") : null);
                    log.setNewAuthStatus(rs.getObject("new_auth_status") != null ? rs.getInt("new_auth_status") : null);
                    log.setOperationTime(rs.getTimestamp("operation_time"));
                    logs.add(log);
                }
            }
        } finally {
            releaseConnection();
        }
        return logs;
    }

    public long countWithFilters(Timestamp start, Timestamp end, Integer operatorId, Integer targetId, Integer operationType) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM admin_operation_log WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (start != null && end != null) {
            sql.append(" AND operation_time BETWEEN ? AND ?");
            params.add(start);
            params.add(end);
        }
        if (operatorId != null) {
            sql.append(" AND operator_id = ?");
            params.add(operatorId);
        }
        if (targetId != null) {
            sql.append(" AND target_id = ?");
            params.add(targetId);
        }
        if (operationType != null) {
            sql.append(" AND operation_type = ?");
            params.add(operationType);
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