import request from "../../axios";

const resetPasswordAPI = async (loginName:string) => {
    let token = localStorage.getItem('token')
    return request({
        url: '/api/systemAdmin/resetPassword',
        method: 'post',
        headers: {"Content-Type":"application/json", "Authorization": token},
        data: {
            loginName: loginName
        }
    })
}

export default resetPasswordAPI;