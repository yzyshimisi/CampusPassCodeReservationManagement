interface MakeAppointmentType{
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