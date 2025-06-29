import request from "../../axios";

const getDepartAdminAPI = async () => {
    let token = localStorage.getItem("token");
    return request({
        url: "/api/schoolAdmin/queryAdmin",
        method: 'get',
        headers: {"Content-Type":"application/json", "Authorization": token},
    })
}

export default getDepartAdminAPI;