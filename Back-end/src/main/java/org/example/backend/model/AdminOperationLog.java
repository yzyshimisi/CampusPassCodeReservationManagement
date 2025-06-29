package org.example.backend.model;

import java.sql.Timestamp;

public class AdminOperationLog {
    private int id;
    private int operatorId;
    private Integer targetId;
    private int operationType;
    private String oldFullName;
    private String newFullName;
    private Integer oldAdminRole;
    private Integer newAdminRole;
    private Integer oldDepartmentId;
    private Integer newDepartmentId;
    private Integer oldAuthStatus;
    private Integer newAuthStatus;
    private Timestamp operationTime;

    // Getters
    public int getId() {
        return id;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public int getOperationType() {
        return operationType;
    }

    public String getOldFullName() {
        return oldFullName;
    }

    public String getNewFullName() {
        return newFullName;
    }

    public Integer getOldAdminRole() {
        return oldAdminRole;
    }

    public Integer getNewAdminRole() {
        return newAdminRole;
    }

    public Integer getOldDepartmentId() {
        return oldDepartmentId;
    }

    public Integer getNewDepartmentId() {
        return newDepartmentId;
    }

    public Integer getOldAuthStatus() {
        return oldAuthStatus;
    }

    public Integer getNewAuthStatus() {
        return newAuthStatus;
    }

    public Timestamp getOperationTime() {
        return operationTime;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public void setOldFullName(String oldFullName) {
        this.oldFullName = oldFullName;
    }

    public void setNewFullName(String newFullName) {
        this.newFullName = newFullName;
    }

    public void setOldAdminRole(Integer oldAdminRole) {
        this.oldAdminRole = oldAdminRole;
    }

    public void setNewAdminRole(Integer newAdminRole) {
        this.newAdminRole = newAdminRole;
    }

    public void setOldDepartmentId(Integer oldDepartmentId) {
        this.oldDepartmentId = oldDepartmentId;
    }

    public void setNewDepartmentId(Integer newDepartmentId) {
        this.newDepartmentId = newDepartmentId;
    }

    public void setOldAuthStatus(Integer oldAuthStatus) {
        this.oldAuthStatus = oldAuthStatus;
    }

    public void setNewAuthStatus(Integer newAuthStatus) {
        this.newAuthStatus = newAuthStatus;
    }

    public void setOperationTime(Timestamp operationTime) {
        this.operationTime = operationTime;
    }
}