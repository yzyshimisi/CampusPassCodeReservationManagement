import request from "../axios";

const getPassCodeAPI = async (data:getPassCodeType) => {
    return request({
        url: "/api/appointment/passCode",
        method: 'post',
        headers: {"Content-Type":"application/json"},
        data: data
    })
}

export default getPassCodeAPI;