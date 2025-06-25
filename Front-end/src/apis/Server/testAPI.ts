import request from "../axios";

const testAPI = async () => {
    return request({
        url: '/test',
        method: 'post',
        headers:{
            "Content-Type":"application/json",    // 发送json格式的数据
        },
    })
}

export default testAPI;