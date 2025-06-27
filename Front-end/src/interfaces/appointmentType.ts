
interface addAppointmentType {
    isPublic: number,
    campus: number,
    entry_time: string,
    end_time: string,
    organization: string,
    full_name: string,
    id_number: string,
    phone: string,
    transport_mode: number,
    plate_number: string,
    visiting_department?: string,
    contact_person?: string,
    visit_purpose?: string,

    entourages: Array<{
        full_name: string,
        id_number: string,
        phone: string,
    }>
}

interface checkAppointmentType {
    full_name: string,
    id_number: string,
    phone: string,
}

interface getAppointmentStatusType {
    isPublic: number,
    appointment_person_id: number,
    full_name: string,
    id_number: string,
    phone: string
}

interface appointmentRecordType{
    id: number,
    campus: number,
    application_time: string,
    entry_time: string,
    end_time: string,
    organization: string,
    transport_mode: number,
    plate_number?: string,

    visiting_department?: string,
    contact_person?: string,
    visit_purpose?: string,
    approval_status?: number,

    entourages: Array<{
        appointment_id: number,
        full_name: string,
        mask_id_number: string,
        phone: string,
        is_applicant: number,
    }>
}