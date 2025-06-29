import request from "../../axios";

// @ts-ignore
const addDepartAdminAPI = async (data: addDepartAdminType) => {
    let token = localStorage.getItem("token")
    return request({
        url: "/api/schoolAdmin/addAdmin",
        method: "post",
        headers: {"Content-Type":"application/json", "Authorization": token},
        data: data
    })
}

export default addDepartAdminAPI;
