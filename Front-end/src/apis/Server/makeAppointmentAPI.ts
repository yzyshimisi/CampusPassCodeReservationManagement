import request from "../axios";

const makeAppointmentAPI = async (data:MakeAppointmentType) => {
    return request({
        url: '/api/appointment',
        method: 'post',
        headers: {"Content-Type":"application/json"},
        data: data
    })
}

export default makeAppointmentAPI;