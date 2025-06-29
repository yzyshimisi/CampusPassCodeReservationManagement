import request from "../axios";

const loginAPI = async (data:loginDataType) => {
    return request({
        url: "/api/login",
        method: "post",
        headers: {"Content-Type":"application/json"},
        data: data,
    })
}

export default loginAPI;