package org.example.backend.dao;

import org.example.backend.model.AppointmentBean;
import org.example.backend.model.AppointmentPersonBean;
import java.sql.*;
import java.util.ArrayList;

public class AppointmentManagementDao extends BaseDao {

    // 查询公众预约记录（按 ID）
    public AppointmentBean findPublicAppointmentById(int id) throws Exception {
        String sql = "SELECT * FROM public_appointment WHERE id = ? LIMIT 1";
        lookupConnection();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                AppointmentBean appointment = new AppointmentBean();
                appointment.setId(rs.getInt("id"));
                appointment.setCampus(rs.getInt("campus"));
                appointment.setEntry_time(rs.getString("entry_time"));
                appointment.setEnd_time(rs.getString("end_time"));
                appointment.setOrganization(rs.getString("organization"));
                appointment.setTransport_mode(rs.getInt("transport_mode"));
                appointment.setPlate_number(rs.getString("plate_number"));
                return appointment;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            releaseConnection();
        }
    }

    // 查询公务预约记录（按 ID）
    public AppointmentBean findOfficialAppointmentById(int id) throws Exception {
        String sql = "SELECT * FROM official_appointment WHERE id = ? LIMIT 1";
        lookupConnection();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                AppointmentBean appointment = new AppointmentBean();
                appointment.setId(rs.getInt("id"));
                appointment.setEntry_time(rs.getString("entry_time"));
                appointment.setEnd_time(rs.getString("end_time"));
                appointment.setCampus(rs.getInt("campus"));
                appointment.setOrganization(rs.getString("organization"));
                appointment.setTransport_mode(rs.getInt("transport_mode"));
                appointment.setPlate_number(rs.getString("plate_number"));
                appointment.setVisiting_department(rs.getString("visiting_department"));
                appointment.setContact_person(rs.getString("contact_person"));
                appointment.setVisit_purpose(rs.getString("visit_purpose"));
                appointment.setApproval_status(rs.getInt("approval_status"));
                return appointment;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            releaseConnection();
        }
    }

    // 查询公众预约人员
    public ArrayList<AppointmentPersonBean> findPublicAppointmentPersons(int publicAppointmentId) throws Exception {
        String sql = "SELECT * FROM public_appointment_person WHERE public_appointment_id = ?";
        lookupConnection();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, publicAppointmentId);
            ResultSet rs = ps.executeQuery();
            ArrayList<AppointmentPersonBean> persons = new ArrayList<>();
            while (rs.next()) {
                AppointmentPersonBean person = new AppointmentPersonBean();
                person.setId(rs.getInt("id"));
                person.setAppointment_id(rs.getInt("public_appointment_id"));
                person.setFull_name(rs.getString("full_name"));
                person.setId_number(rs.getString("id_number"));
                person.setMask_id_number(rs.getString("mask_id_number"));
                person.setPhone(rs.getString("phone"));
                person.setIs_applicant(rs.getInt("is_applicant"));
                persons.add(person);
            }
            return persons;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            releaseConnection();
        }
    }

    // 查询公务预约人员
    public ArrayList<AppointmentPersonBean> findOfficialAppointmentPersons(int officialAppointmentId) throws Exception {
        String sql = "SELECT * FROM official_appointment_person WHERE official_appointment_id = ?";
        lookupConnection();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, officialAppointmentId);
            ResultSet rs = ps.executeQuery();
            ArrayList<AppointmentPersonBean> persons = new ArrayList<>();
            while (rs.next()) {
                AppointmentPersonBean person = new AppointmentPersonBean();
                person.setId(rs.getInt("id"));
                person.setAppointment_id(rs.getInt("official_appointment_id"));
                person.setFull_name(rs.getString("full_name"));
                person.setId_number(rs.getString("id_number"));
                person.setMask_id_number(rs.getString("mask_id_number"));
                person.setPhone(rs.getString("phone"));
                person.setIs_applicant(rs.getInt("is_applicant"));
                persons.add(person);
            }
            return persons;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            releaseConnection();
        }
    }

    // 更新公务预约审批状态
    public boolean updateOfficialAppointmentApproval(int id, int approvalStatus) throws Exception {
        String sql = "UPDATE official_appointment SET approval_status = ? WHERE id = ?";
        lookupConnection();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, approvalStatus);
            ps.setInt(2, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            releaseConnection();
        }
    }

    // 查询所有公众预约
    public ArrayList<AppointmentBean> getAllPublicAppointments() throws Exception {
        String sql = "SELECT * FROM public_appointment";
        lookupConnection();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            ArrayList<AppointmentBean> appointments = new ArrayList<>();
            while (rs.next()) {
                AppointmentBean appointment = new AppointmentBean();
                appointment.setId(rs.getInt("id"));
                appointment.setCampus(rs.getInt("campus"));
                appointment.setEntry_time(rs.getString("entry_time"));
                appointment.setEnd_time(rs.getString("end_time"));
                appointment.setOrganization(rs.getString("organization"));
                appointment.setTransport_mode(rs.getInt("transport_mode"));
                appointment.setPlate_number(rs.getString("plate_number"));
                appointments.add(appointment);
            }
            return appointments;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            releaseConnection();
        }
    }

    // 查询公众预约（模糊查询按姓名和手机号）
    public ArrayList<AppointmentBean> queryPublicAppointmentsByNamePhone(String fullName, String phone) throws Exception {
        String sql = "SELECT DISTINCT pa.* FROM public_appointment pa " +
                "JOIN public_appointment_person pap ON pa.id = pap.public_appointment_id " +
                "WHERE 1=1";
        if (fullName != null) sql += " AND pap.full_name LIKE ?";
        if (phone != null) sql += " AND pap.phone LIKE ?";

        lookupConnection();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            int paramIndex = 1;
            if (fullName != null) ps.setString(paramIndex++, "%" + fullName + "%");
            if (phone != null) ps.setString(paramIndex++, "%" + phone + "%");

            ResultSet rs = ps.executeQuery();
            ArrayList<AppointmentBean> appointments = new ArrayList<>();
            while (rs.next()) {
                AppointmentBean appointment = new AppointmentBean();
                appointment.setId(rs.getInt("id"));
                appointment.setCampus(rs.getInt("campus"));
                appointment.setEntry_time(rs.getString("entry_time"));
                appointment.setEnd_time(rs.getString("end_time"));
                appointment.setOrganization(rs.getString("organization"));
                appointment.setTransport_mode(rs.getInt("transport_mode"));
                appointment.setPlate_number(rs.getString("plate_number"));
                appointments.add(appointment);
            }
            return appointments;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            releaseConnection();
        }
    }

    // 查询公务预约（模糊查询按姓名和手机号）
    public ArrayList<AppointmentBean> queryOfficialAppointmentsByNamePhone(String fullName, String phone) throws Exception {
        String sql = "SELECT DISTINCT oa.* FROM official_appointment oa " +
                "JOIN official_appointment_person oap ON oa.id = oap.official_appointment_id " +
                "WHERE 1=1";
        if (fullName != null) sql += " AND oap.full_name LIKE ?";
        if (phone != null) sql += " AND oap.phone LIKE ?";

        lookupConnection();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            int paramIndex = 1;
            if (fullName != null) ps.setString(paramIndex++, "%" + fullName + "%");
            if (phone != null) ps.setString(paramIndex++, "%" + phone + "%");

            ResultSet rs = ps.executeQuery();
            ArrayList<AppointmentBean> appointments = new ArrayList<>();
            while (rs.next()) {
                AppointmentBean appointment = new AppointmentBean();
                appointment.setId(rs.getInt("id"));
                appointment.setEntry_time(rs.getString("entry_time"));
                appointment.setEnd_time(rs.getString("end_time"));
                appointment.setCampus(rs.getInt("campus"));
                appointment.setOrganization(rs.getString("organization"));
                appointment.setTransport_mode(rs.getInt("transport_mode"));
                appointment.setPlate_number(rs.getString("plate_number"));
                appointment.setVisiting_department(rs.getString("visiting_department"));
                appointment.setContact_person(rs.getString("contact_person"));
                appointment.setVisit_purpose(rs.getString("visit_purpose"));
                appointment.setApproval_status(rs.getInt("approval_status"));
                appointments.add(appointment);
            }
            return appointments;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            releaseConnection();
        }
    }

    // 查询所有公务预约
    public ArrayList<AppointmentBean> getAllOfficialAppointments() throws Exception {
        String sql = "SELECT * FROM official_appointment";
        lookupConnection();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            ArrayList<AppointmentBean> appointments = new ArrayList<>();
            while (rs.next()) {
                AppointmentBean appointment = new AppointmentBean();
                appointment.setId(rs.getInt("id"));
                appointment.setEntry_time(rs.getString("entry_time"));
                appointment.setEnd_time(rs.getString("end_time"));
                appointment.setCampus(rs.getInt("campus"));
                appointment.setOrganization(rs.getString("organization"));
                appointment.setTransport_mode(rs.getInt("transport_mode"));
                appointment.setPlate_number(rs.getString("plate_number"));
                appointment.setVisiting_department(rs.getString("visiting_department"));
                appointment.setContact_person(rs.getString("contact_person"));
                appointment.setVisit_purpose(rs.getString("visit_purpose"));
                appointment.setApproval_status(rs.getInt("approval_status"));
                appointments.add(appointment);
            }
            return appointments;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            releaseConnection();
        }
    }
}