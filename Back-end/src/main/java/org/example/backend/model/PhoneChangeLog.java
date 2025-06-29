package org.example.backend.model;

import java.sql.Timestamp;

public class PhoneChangeLog {
    private int id;
    private int adminId;
    private String old_phone;
    private String new_phone;
    private Timestamp change_time;

    // Getters
    public int getId() {
        return id;
    }

    public int getAdminId() {
        return adminId;
    }

    public String getold_phone() {
        return old_phone;
    }

    public String getnew_phone() {
        return new_phone;
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

    public void setold_phone(String old_phone) {
        this.old_phone = old_phone;
    }

    public void setnew_phone(String new_phone) {
        this.new_phone = new_phone;
    }

    public void setchange_time(Timestamp change_time) {
        this.change_time = change_time;
    }
}