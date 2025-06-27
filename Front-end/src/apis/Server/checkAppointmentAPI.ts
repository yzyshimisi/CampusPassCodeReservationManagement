import request from "../axios";

const checkAppointmentAPI = async (data:checkAppointmentType) => {
    return request({
        url: '/api/appointment/check',
        method: "post",
        headers: {"Content-Type":"application/json"},
        data: data,
    })
}

export default checkAppointmentAPI;