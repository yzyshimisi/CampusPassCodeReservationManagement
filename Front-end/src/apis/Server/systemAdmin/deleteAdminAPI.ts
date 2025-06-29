import request from "../../axios";

const deleteAdminAPI = async (loginName:string) => {
    let token = localStorage.getItem("token")
    return request({
        url: '/api/systemAdmin/delete',
        method: 'post',
        headers: {"Content-Type":"application/json", "Authorization": token},
        data: {
            loginName : loginName
        }
    })
}

export default deleteAdminAPI;