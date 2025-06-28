package org.example.backend.model;

import java.io.Serializable;

public class AppointmentPersonBean implements Serializable {
    private int id;
    private int appointment_id;
    private String full_name;
    private String id_number;
    private String mask_id_number;
    private String phone;
    private int is_applicant;

    public AppointmentPersonBean(){}

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getAppointment_id() {
        return appointment_id;
    }
    public void setAppointment_id(int appointment_id) {
        this.appointment_id = appointment_id;
    }
    public String getFull_name() {
        return full_name;
    }
    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }
    public String getId_number() {
        return id_number;
    }
    public void setId_number(String id_number) {
        this.id_number = id_number;
    }
    public String getMask_id_number() {
        return mask_id_number;
    }
    public void setMask_id_number(String mask_id_number) {
        this.mask_id_number = mask_id_number;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public int getIs_applicant() {
        return is_applicant;
    }
    public void setIs_applicant(int is_applicant) {
        this.is_applicant = is_applicant;
    }
}
