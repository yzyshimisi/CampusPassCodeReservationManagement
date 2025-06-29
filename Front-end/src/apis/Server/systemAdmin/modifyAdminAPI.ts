import request from "../../axios";

const modifyAdminAPI = async (data:modifyAdminType) => {
    let token = localStorage.getItem("token")
    return request({
        url: '/api/systemAdmin/modifyAdmin',
        method: 'post',
        headers: {"Content-Type":"application/json", "Authorization": token},
        data: data
    })
}

export default modifyAdminAPI;