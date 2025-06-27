import request from "../axios";

const getAppointmentStatusAPI = async (data:getAppointmentStatusType) => {
    return request({
        url: "/api/appointment/status",
        method: 'post',
        headers: {"Content-Type":"application/json"},
        data: data
    })
}

export default getAppointmentStatusAPI;