import request from "../../axios";

const fuzzySearchDepartmentAPI = async (fuzzyName:string) => {
    let token = localStorage.getItem("token");
    let url = `/api/schoolAdmin/fuzzySearchDepart?fuzzyName=${fuzzyName}`
    return request({
        url: url,
        method: 'get',
        headers: {"Content-Type":"application/json", "Authorization": token},
    })
}

export default fuzzySearchDepartmentAPI;
