package org.example.backend.dao;

import org.example.backend.model.AppointmentBean;
import org.example.backend.model.AppointmentPersonBean;

import java.sql.*;

public class AppointmentDAO extends BaseDao{

    public int addPublicAppointment(AppointmentBean appointmentBean) {
        String sql = "insert into public_appointment (campus,entry_time,end_time,organization,transport_mode,plate_number) values(?,?,?,?,?,?)";

        try(PreparedStatement pstmt = getConnection().prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS)){
            pstmt.setInt(1,appointmentBean.getCampus());
            pstmt.setString(2, appointmentBean.getEntry_time());
            pstmt.setString(3, appointmentBean.getEnd_time());
            pstmt.setString(4, appointmentBean.getOrganization());
            pstmt.setInt(5, appointmentBean.getTransport_mode());
            pstmt.setString(6, appointmentBean.getPlate_number());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public boolean addPublicAppointmentPerson(AppointmentPersonBean appointmentPersonBean){
        String sql = "insert into public_appointment_person (public_appointment_id,full_name,id_number,mask_id_number,phone,is_applicant) values(?,?,?,?,?,?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, appointmentPersonBean.getAppointment_id());
            pstmt.setString(2, appointmentPersonBean.getFull_name());
            pstmt.setString(3, appointmentPersonBean.getId_number());
            pstmt.setString(4, appointmentPersonBean.getMask_id_number());
            pstmt.setString(5, appointmentPersonBean.getPhone());
            pstmt.setInt(6, appointmentPersonBean.getIs_applicant());

            int affectRow = pstmt.executeUpdate();

            if(affectRow>0){
                return true;
            }else {
                throw new SQLException("Data insertion failed.");
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public int addOfficialAppointment(AppointmentBean appointmentBean) {
        String sql = "insert into official_appointment (entry_time,end_time,campus,organization,transport_mode,plate_number,visiting_department,contact_person,visit_purpose,approval_status) values(?,?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement pstmt = getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            pstmt.setString(1, appointmentBean.getEntry_time());
            pstmt.setString(2, appointmentBean.getEnd_time());
            pstmt.setInt(3, appointmentBean.getCampus());
            pstmt.setString(4, appointmentBean.getOrganization());
            pstmt.setInt(5, appointmentBean.getTransport_mode());
            pstmt.setString(6, appointmentBean.getPlate_number());
            pstmt.setString(7, appointmentBean.getVisiting_department());
            pstmt.setString(8, appointmentBean.getContact_person());
            pstmt.setString(9, appointmentBean.getVisit_purpose());
            pstmt.setInt(10, appointmentBean.getApproval_status());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }else{
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public boolean addOfficialAppointmentPerson(AppointmentPersonBean appointmentPersonBean){
        String sql = "insert into official_appointment_person (official_appointment_id, full_name, id_number, mask_id_number, phone, is_applicant) values(?,?,?,?,?,?)";
        try(PreparedStatement pstmt = getConnection().prepareStatement(sql)){
            pstmt.setInt(1, appointmentPersonBean.getAppointment_id());
            pstmt.setString(2, appointmentPersonBean.getFull_name());
            pstmt.setString(3, appointmentPersonBean.getId_number());
            pstmt.setString(4, appointmentPersonBean.getMask_id_number());
            pstmt.setString(5, appointmentPersonBean.getPhone());
            pstmt.setInt(6, appointmentPersonBean.getIs_applicant());

            int affectRow = pstmt.executeUpdate();

            if(affectRow>0){
                return true;
            }else {
                throw new SQLException("Data insertion failed.");
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
