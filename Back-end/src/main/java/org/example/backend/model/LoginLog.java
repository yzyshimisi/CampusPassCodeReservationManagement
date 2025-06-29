package org.example.backend.model;

import java.sql.Timestamp;

public class LoginLog {
    private int id;
    private int adminId;
    private Timestamp loginTime;
    private int loginStatus;

    // Constructor for LoginServlet (used in addLoginLog)
    public LoginLog(int adminId, Timestamp loginTime, int loginStatus) {
        this.adminId = adminId;
        this.loginTime = loginTime;
        this.loginStatus = loginStatus;
    }

    // No-arg constructor for queryWithFilters
    public LoginLog() {
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
    public Timestamp getLoginTime() { return loginTime; }
    public void setLoginTime(Timestamp loginTime) { this.loginTime = loginTime; }
    public int getLoginStatus() { return loginStatus; }
    public void setLoginStatus(int loginStatus) { this.loginStatus = loginStatus; }
}