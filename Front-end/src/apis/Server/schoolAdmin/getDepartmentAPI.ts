import request from "../../axios";

const getDepartmentAPI = async () => {
    let token = localStorage.getItem("token");
    return request({
        url: "/api/schoolAdmin/queryDepart",
        method: 'get',
        headers: {"Content-Type":"application/json", "Authorization": token},
    })
}

export default getDepartmentAPI;