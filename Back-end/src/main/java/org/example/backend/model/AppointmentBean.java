package org.example.backend.model;

import java.io.Serializable;
import java.util.ArrayList;

public class AppointmentBean implements Serializable {
    private int id;
    private int campus;
    private String application_time;
    private String entry_time;
    private String end_time;
    private String organization;
    private int transport_mode;
    private String plate_number;

    private String visiting_department;
    private String contact_person;
    private String visit_purpose;
    private int approval_status;

    public AppointmentBean(){}

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getApplication_time(){
        return application_time;
    }
    public void setApplication_time(String application_time){
        this.application_time = application_time;
    }
    public int getApproval_status(){
        return approval_status;
    }
    public void setApproval_status(int approval_status){
        this.approval_status = approval_status;
    }
    public int getCampus() {
        return campus;
    }
    public void setCampus(int campus) {
        this.campus = campus;
    }
    public String getEntry_time() {
        return entry_time;
    }
    public void setEntry_time(String entry_time) {
        this.entry_time = entry_time;
    }
    public String getEnd_time() {
        return end_time;
    }
    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
    public String getOrganization() {
        return organization;
    }
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    public int getTransport_mode() {
        return transport_mode;
    }
    public void setTransport_mode(int transport_mode) {
        this.transport_mode = transport_mode;
    }
    public String getPlate_number() {
        return plate_number;
    }
    public void setPlate_number(String plate_number) {
        this.plate_number = plate_number;
    }
    public String getVisiting_department() {
        return visiting_department;
    }
    public void setVisiting_department(String visiting_department) {
        this.visiting_department = visiting_department;
    }
    public String getContact_person() {
        return contact_person;
    }
    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
    }
    public String getVisit_purpose() {
        return visit_purpose;
    }
    public void setVisit_purpose(String visit_purpose) {
        this.visit_purpose = visit_purpose;
    }
}
