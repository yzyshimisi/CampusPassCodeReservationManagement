import request from "../../axios";

const updateDepartmentAPI = async (data:departmentType) => {
    let token = localStorage.getItem("token");
    return request({
        url: "/api/schoolAdmin/updateDepart",
        method: 'post',
        headers: {"Content-Type":"application/json", "Authorization": token},
        data: data
    })
}

export default updateDepartmentAPI;