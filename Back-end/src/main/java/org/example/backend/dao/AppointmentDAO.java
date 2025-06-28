package org.example.backend.dao;

import cn.hutool.crypto.SmUtil;
import org.example.backend.model.AppointmentBean;
import org.example.backend.model.AppointmentPersonBean;

import java.sql.*;
import java.util.ArrayList;

public class AppointmentDAO extends BaseDao{

    // 添加公众预约记录
    public int addPublicAppointment(AppointmentBean appointmentBean) {
        String sql = "insert into public_appointment (campus,entry_time,end_time,organization,transport_mode,plate_number,application_time) values(?,?,?,?,?,?,?)";

        try(PreparedStatement pstmt = getConnection().prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS)){
            pstmt.setInt(1,appointmentBean.getCampus());
            pstmt.setString(2, appointmentBean.getEntry_time());
            pstmt.setString(3, appointmentBean.getEnd_time());
            pstmt.setString(4, appointmentBean.getOrganization());
            pstmt.setInt(5, appointmentBean.getTransport_mode());
            pstmt.setString(6, appointmentBean.getPlate_number());
            pstmt.setString(7, appointmentBean.getApplication_time());

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

    // 添加公众预约人员
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

    // 添加公务预约记录
    public int addOfficialAppointment(AppointmentBean appointmentBean) {
        String sql = "insert into official_appointment (entry_time,end_time,campus,organization,transport_mode,plate_number,visiting_department,contact_person,visit_purpose,approval_status,application_time) values(?,?,?,?,?,?,?,?,?,?,?)";
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
            pstmt.setString(11, appointmentBean.getApplication_time());

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

    // 添加公务预约人员
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

    // 查找公众预约记录（通过id）
    public AppointmentBean findPublicAppointment(int id){
        String sql = "select * from public_appointment where id=?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try(ResultSet result = pstmt.executeQuery();){
                if (result.next()) {
                    AppointmentBean appointmentBean = new AppointmentBean();
                    appointmentBean.setId(result.getInt("id"));
                    appointmentBean.setCampus(result.getInt("campus"));
                    appointmentBean.setApplication_time(result.getString("application_time"));
                    appointmentBean.setEntry_time(result.getString("entry_time"));
                    appointmentBean.setEnd_time(result.getString("end_time"));
                    appointmentBean.setOrganization(result.getString("organization"));
                    appointmentBean.setTransport_mode(result.getInt("transport_mode"));
                    appointmentBean.setPlate_number(result.getString("plate_number"));

                    return appointmentBean;
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 查找公务预约记录（通过id）
    public AppointmentBean findOfficialAppointment(int id){
        String sql = "select * from official_appointment where id=?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql);) {
            pstmt.setInt(1, id);
            try(ResultSet result = pstmt.executeQuery();){
                if (result.next()) {
                    AppointmentBean appointmentBean = new AppointmentBean();
                    appointmentBean.setId(result.getInt("id"));
                    appointmentBean.setCampus(result.getInt("campus"));
                    appointmentBean.setApplication_time(result.getString("application_time"));
                    appointmentBean.setEntry_time(result.getString("entry_time"));
                    appointmentBean.setEnd_time(result.getString("end_time"));
                    appointmentBean.setOrganization(result.getString("organization"));
                    appointmentBean.setTransport_mode(result.getInt("transport_mode"));
                    appointmentBean.setPlate_number(result.getString("plate_number"));
                    appointmentBean.setVisiting_department(result.getString("visiting_department"));
                    appointmentBean.setContact_person(result.getString("contact_person"));
                    appointmentBean.setVisit_purpose(result.getString("visit_purpose"));
                    appointmentBean.setApproval_status(result.getInt("approval_status"));

                    return appointmentBean;
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 查找公众预约人员（通过id）
    public AppointmentPersonBean findPublicAppointmentPersonOne(int id){
        String sql = "select * from public_appointment_person where id=?";
        try(PreparedStatement pstmt = getConnection().prepareStatement(sql);){
            pstmt.setInt(1, id);
            try(ResultSet result = pstmt.executeQuery();){
                if (result.next()) {
                    AppointmentPersonBean appointmentPersonBean = new AppointmentPersonBean();
                    appointmentPersonBean.setId(result.getInt("id"));
                    appointmentPersonBean.setAppointment_id(result.getInt("public_appointment_id"));
                    appointmentPersonBean.setFull_name(result.getString("full_name"));
                    appointmentPersonBean.setId_number(result.getString("id_number"));
                    appointmentPersonBean.setMask_id_number(result.getString("mask_id_number"));
                    appointmentPersonBean.setPhone(result.getString("phone"));
                    appointmentPersonBean.setIs_applicant(result.getInt("is_applicant"));

                    return appointmentPersonBean;
                }
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // 查找公众预约人员（通过公众预约id）
    public ArrayList<AppointmentPersonBean> findPublicAppointmentPerson(int public_appointment_id){
        String sql = "select * from public_appointment_person where public_appointment_id=?";
        try(PreparedStatement pstmt = getConnection().prepareStatement(sql);){
            pstmt.setInt(1,public_appointment_id);
            try(ResultSet result = pstmt.executeQuery();){
                ArrayList<AppointmentPersonBean> appointmentPersonBeans = new ArrayList<>();
                while (result.next()) {
                    AppointmentPersonBean appointmentPersonBean = new AppointmentPersonBean();
                    appointmentPersonBean.setId(result.getInt("id"));
                    appointmentPersonBean.setAppointment_id(result.getInt("public_appointment_id"));
                    appointmentPersonBean.setFull_name(result.getString("full_name"));
                    appointmentPersonBean.setId_number(result.getString("mask_id_number"));
                    appointmentPersonBean.setMask_id_number(result.getString("mask_id_number"));
                    appointmentPersonBean.setPhone(result.getString("phone"));
                    appointmentPersonBean.setIs_applicant(result.getInt("is_applicant"));

                    appointmentPersonBeans.add(appointmentPersonBean);
                }
                return appointmentPersonBeans;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // 查找公众预约人员（通过基本信息）
    public ArrayList<AppointmentPersonBean> findPublicAppointmentPerson(String full_name, String id_number, String phone){
        String sql = "select * from public_appointment_person where full_name=? and id_number=? and phone=?";
        try(PreparedStatement pstmt = getConnection().prepareStatement(sql);){
            pstmt.setString(1, full_name);
            pstmt.setString(2, SmUtil.sm3(id_number));
            pstmt.setString(3, phone);
            try(ResultSet result = pstmt.executeQuery();){
                ArrayList<AppointmentPersonBean> appointmentPersonBeans = new ArrayList<>();
                while (result.next()) {
                    AppointmentPersonBean appointmentPersonBean = new AppointmentPersonBean();
                    appointmentPersonBean.setId(result.getInt("id"));
                    appointmentPersonBean.setAppointment_id(result.getInt("public_appointment_id"));
                    appointmentPersonBean.setFull_name(result.getString("full_name"));
                    appointmentPersonBean.setId_number(result.getString("mask_id_number"));
                    appointmentPersonBean.setMask_id_number(result.getString("mask_id_number"));
                    appointmentPersonBean.setPhone(result.getString("phone"));
                    appointmentPersonBean.setIs_applicant(result.getInt("is_applicant"));

                    appointmentPersonBeans.add(appointmentPersonBean);
                }
                return appointmentPersonBeans;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // 查找公务预约人员（通过id）
    public AppointmentPersonBean findOfficialAppointmentPersonOne(int id){
        String sql = "select * from official_appointment_person where id=?";
        try(PreparedStatement pstmt = getConnection().prepareStatement(sql)){
            pstmt.setInt(1, id);
            try(ResultSet result = pstmt.executeQuery();){
                if (result.next()) {
                    AppointmentPersonBean appointmentPersonBean = new AppointmentPersonBean();
                    appointmentPersonBean.setId(result.getInt("id"));
                    appointmentPersonBean.setAppointment_id(result.getInt("official_appointment_id"));
                    appointmentPersonBean.setFull_name(result.getString("full_name"));
                    appointmentPersonBean.setId_number(result.getString("id_number"));
                    appointmentPersonBean.setMask_id_number(result.getString("mask_id_number"));
                    appointmentPersonBean.setPhone(result.getString("phone"));
                    appointmentPersonBean.setIs_applicant(result.getInt("is_applicant"));

                    return appointmentPersonBean;
                }
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // 查找公务预约人员（通过公务预约id）
    public ArrayList<AppointmentPersonBean> findOfficialAppointmentPerson(int official_appointment_id){
        String sql = "select * from official_appointment_person where official_appointment_id=?";
        try(PreparedStatement pstmt = getConnection().prepareStatement(sql)){
            pstmt.setInt(1,official_appointment_id);
            try(ResultSet result = pstmt.executeQuery();){
                ArrayList<AppointmentPersonBean> appointmentPersonBeans = new ArrayList<>();
                while (result.next()) {
                    AppointmentPersonBean appointmentPersonBean = new AppointmentPersonBean();
                    appointmentPersonBean.setId(result.getInt("id"));
                    appointmentPersonBean.setAppointment_id(result.getInt("official_appointment_id"));
                    appointmentPersonBean.setFull_name(result.getString("full_name"));
                    appointmentPersonBean.setId_number(result.getString("mask_id_number"));
                    appointmentPersonBean.setMask_id_number(result.getString("mask_id_number"));
                    appointmentPersonBean.setPhone(result.getString("phone"));
                    appointmentPersonBean.setIs_applicant(result.getInt("is_applicant"));

                    appointmentPersonBeans.add(appointmentPersonBean);
                }
                return appointmentPersonBeans;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // 查找公务预约人员（通过基本信息）
    public ArrayList<AppointmentPersonBean> findOfficialAppointmentPerson(String full_name, String id_number, String phone){
        String sql = "select * from official_appointment_person where full_name=? and id_number=? and phone=?";
        try(PreparedStatement pstmt = getConnection().prepareStatement(sql)){
            pstmt.setString(1, full_name);
            pstmt.setString(2, SmUtil.sm3(id_number));
            pstmt.setString(3, phone);
            try(ResultSet result = pstmt.executeQuery();){
                ArrayList<AppointmentPersonBean> appointmentPersonBeans = new ArrayList<>();
                while (result.next()) {
                    AppointmentPersonBean appointmentPersonBean = new AppointmentPersonBean();
                    appointmentPersonBean.setId(result.getInt("id"));
                    appointmentPersonBean.setAppointment_id(result.getInt("official_appointment_id"));
                    appointmentPersonBean.setFull_name(result.getString("full_name"));
                    appointmentPersonBean.setId_number(result.getString("mask_id_number"));
                    appointmentPersonBean.setMask_id_number(result.getString("mask_id_number"));
                    appointmentPersonBean.setPhone(result.getString("phone"));
                    appointmentPersonBean.setIs_applicant(result.getInt("is_applicant"));

                    appointmentPersonBeans.add(appointmentPersonBean);
                }
                return appointmentPersonBeans;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
