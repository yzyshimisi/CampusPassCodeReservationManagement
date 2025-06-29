import request from "../../axios";

const listAllAdminAPI = async () => {
    let token = localStorage.getItem("token")
    return request({
        url: "/api/systemAdmin/listAllAdmin",
        method: "get",
        headers: {"Content-Type":"application/json", "Authorization": token},
    })
}

export default listAllAdminAPI;