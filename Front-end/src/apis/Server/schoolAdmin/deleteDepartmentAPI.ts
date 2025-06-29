import request from "../../axios";

const deleteDepartmentAPI = async (departmentName) => {
    let token = localStorage.getItem("token")
    return request({
        url: "/api/schoolAdmin/deleteDepart",
        method: "post",
        headers: {"Content-Type":"application/json", "Authorization": token},
        data: {
            departmentName: departmentName,
        }
    })
}

export default deleteDepartmentAPI;