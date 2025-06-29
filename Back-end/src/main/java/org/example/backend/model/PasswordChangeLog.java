package org.example.backend.model;

import java.sql.Timestamp;

public class PasswordChangeLog {
    private int id;
    private int adminId;
    private int operation;  // 0 = reset, 1 = modified by self
    private Timestamp change_time;

    // Getters
    public int getId() {
        return id;
    }

    public int getAdminId() {
        return adminId;
    }

    public int getOperation() {
        return operation;
    }

    public Timestamp getchange_time() {
        return change_time;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public void setchange_time(Timestamp change_time) {
        this.change_time = change_time;
    }
}