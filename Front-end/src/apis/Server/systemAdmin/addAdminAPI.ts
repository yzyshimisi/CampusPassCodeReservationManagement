import request from "../../axios";

const addAdminAPI = (data:addAdminType) => {
    let token = localStorage.getItem("token")
    return request({
        url: '/api/systemAdmin/add',
        method: "post",
        headers: {"Content-Type":"application/json", "Authorization": token},
        data: data
    })
}

export default addAdminAPI;