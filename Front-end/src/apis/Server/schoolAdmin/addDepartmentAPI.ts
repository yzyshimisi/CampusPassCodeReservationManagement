import request from "../../axios";

const addDepartmentAPI = async (data: addDepartmentType) => {
    let token = localStorage.getItem("token")
    return request({
        url: "/api/schoolAdmin/addDepart",
        method: "post",
        headers: {"Content-Type":"application/json", "Authorization": token},
        data: data
    })
}

export default addDepartmentAPI;