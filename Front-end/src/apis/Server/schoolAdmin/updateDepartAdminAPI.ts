import request from "../../axios";

const updateDepartAdminAPI = async (data:addDepartAdminType) => {
    let token = localStorage.getItem("token")
    return request({
        url: "/api/schoolAdmin/updateAdmin",
        method: "post",
        headers: {"Content-Type":"application/json", "Authorization": token},
        data: data
    })
}

export default updateDepartAdminAPI;