import request from "../../axios";

const deleteDepartAdminAPI = async (loginName:string) => {
    let token = localStorage.getItem("token")
    return request({
        url: '/api/schoolAdmin/deleteAdmin',
        method: 'post',
        headers: {"Content-Type":"application/json", "Authorization": token},
        data: {
            loginName: loginName
        }
    })
}

export default deleteDepartAdminAPI;