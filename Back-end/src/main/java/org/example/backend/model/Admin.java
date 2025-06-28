package org.example.backend.model;

import java.sql.Timestamp;

public class Admin {
    private int id;
    private int adminRole; // 0：系统管理员，1：学校管理员，2：审计管理员，3：部门管理员
    private String fullName;
    private String loginName;
    private String loginPassword;
    private Timestamp lastPasswordUpdate;
    private Integer departmentId; // 可为空
    private String phone;
    private int authStatus; // 0: 未授权, 1: 已授权
    private int loginFailCount;
    private Timestamp lastLoginFailTime;

    // 构造函数
    public Admin() {
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(int adminRole) {
        this.adminRole = adminRole;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public Timestamp getLastPasswordUpdate() {
        return lastPasswordUpdate;
    }

    public void setLastPasswordUpdate(Timestamp lastPasswordUpdate) {
        this.lastPasswordUpdate = lastPasswordUpdate;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(int authStatus) {
        this.authStatus = authStatus;
    }

    public int getLoginFailCount() {
        return loginFailCount;
    }

    public void setLoginFailCount(int loginFailCount) {
        this.loginFailCount = loginFailCount;
    }

    public Timestamp getLastLoginFailTime() {
        return lastLoginFailTime;
    }

    public void setLastLoginFailTime(Timestamp lastLoginFailTime) {
        this.lastLoginFailTime = lastLoginFailTime;
    }
}