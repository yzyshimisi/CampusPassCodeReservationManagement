package org.example.backend.model;

import java.sql.Timestamp;

public class CustomerQueryLog {
    private int id;
    private String fullName;
    private String maskedIdNumber;
    private Timestamp queryTime;

    // Getters
    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMaskedIdNumber() {
        return maskedIdNumber;
    }

    public Timestamp getQueryTime() {
        return queryTime;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setMaskedIdNumber(String maskedIdNumber) {
        this.maskedIdNumber = maskedIdNumber;
    }

    public void setQueryTime(Timestamp queryTime) {
        this.queryTime = queryTime;
    }
}