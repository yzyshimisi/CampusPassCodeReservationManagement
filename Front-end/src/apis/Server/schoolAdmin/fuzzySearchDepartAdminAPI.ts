import request from "../../axios";

const fuzzySearchDepartAdminAPI = async (fuzzyName:string) => {
    let token = localStorage.getItem("token");
    let url = `/api/schoolAdmin/fuzzySearchAdmin?fuzzyName=${fuzzyName}`
    return request({
        url: url,
        method: 'get',
        headers: {"Content-Type":"application/json", "Authorization": token},
    })
}

export default fuzzySearchDepartAdminAPI;