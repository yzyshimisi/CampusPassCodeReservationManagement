package org.example.backend.dao.log;

import org.example.backend.dao.BaseDao;
import org.example.backend.model.DepartmentOperationLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentOperationLogDao extends BaseDao {

    public List<DepartmentOperationLog> queryWithFilters(Integer operatorId, Integer targetDepartmentId, Integer operationType, int page, int pageSize) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT * FROM department_operation_log WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (operatorId != null) {
            sql.append(" AND operator_id = ?");
            params.add(operatorId);
        }
        if (targetDepartmentId != null) {
            sql.append(" AND target_department_id = ?");
            params.add(targetDepartmentId);
        }
        if (operationType != null) {
            sql.append(" AND operation_type = ?");
            params.add(operationType);
        }
        sql.append(" ORDER BY operation_time DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<DepartmentOperationLog> logs = new ArrayList<>();
        lookupConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DepartmentOperationLog log = new DepartmentOperationLog();
                    log.setId(rs.getInt("id"));
                    log.setOperatorId(rs.getInt("operator_id"));
                    log.setTargetDepartmentId(rs.getObject("target_department_id") != null ? rs.getInt("target_department_id") : null);
                    log.setOperationType(rs.getInt("operation_type"));
                    log.setOldDepartmentType(rs.getObject("old_department_type") != null ? rs.getInt("old_department_type") : null);
                    log.setNewDepartmentType(rs.getObject("new_department_type") != null ? rs.getInt("new_department_type") : null);
                    log.setOldDepartmentName(rs.getString("old_department_name"));
                    log.setNewDepartmentName(rs.getString("new_department_name"));
                    log.setOperationTime(rs.getTimestamp("operation_time"));
                    logs.add(log);
                }
            }
        } finally {
            releaseConnection();
        }
        return logs;
    }

    public long countWithFilters(Integer operatorId, Integer targetDepartmentId, Integer operationType) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM department_operation_log WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (operatorId != null) {
            sql.append(" AND operator_id = ?");
            params.add(operatorId);
        }
        if (targetDepartmentId != null) {
            sql.append(" AND target_department_id = ?");
            params.add(targetDepartmentId);
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