package org.example.backend.model;

import java.sql.Timestamp;

public class DepartmentOperationLog {
    private int id;
    private int operatorId;
    private Integer targetDepartmentId;
    private int operationType;
    private Integer oldDepartmentType;
    private Integer newDepartmentType;
    private String oldDepartmentName;
    private String newDepartmentName;
    private Timestamp operationTime;

    // Getters
    public int getId() {
        return id;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public Integer getTargetDepartmentId() {
        return targetDepartmentId;
    }

    public int getOperationType() {
        return operationType;
    }

    public Integer getOldDepartmentType() {
        return oldDepartmentType;
    }

    public Integer getNewDepartmentType() {
        return newDepartmentType;
    }

    public String getOldDepartmentName() {
        return oldDepartmentName;
    }

    public String getNewDepartmentName() {
        return newDepartmentName;
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

    public void setTargetDepartmentId(Integer targetDepartmentId) {
        this.targetDepartmentId = targetDepartmentId;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public void setOldDepartmentType(Integer oldDepartmentType) {
        this.oldDepartmentType = oldDepartmentType;
    }

    public void setNewDepartmentType(Integer newDepartmentType) {
        this.newDepartmentType = newDepartmentType;
    }

    public void setOldDepartmentName(String oldDepartmentName) {
        this.oldDepartmentName = oldDepartmentName;
    }

    public void setNewDepartmentName(String newDepartmentName) {
        this.newDepartmentName = newDepartmentName;
    }

    public void setOperationTime(Timestamp operationTime) {
        this.operationTime = operationTime;
    }
}