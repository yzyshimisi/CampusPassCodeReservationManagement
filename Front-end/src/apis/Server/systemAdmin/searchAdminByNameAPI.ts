import request from "../../axios";

const searchAdminByNameAPI = async (fuzzyName:string) => {
    let token = localStorage.getItem('token')
    let url = `/api/systemAdmin/searchByName?fuzzyName=${fuzzyName}`
    return request({
        url: url,
        method: 'get',
        headers: {"Content-Type":"application/json", "Authorization": token},
    })
}

export default searchAdminByNameAPI;