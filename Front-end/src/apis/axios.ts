import axios from "axios";

const request = (config:object) => {
    const instance = axios.create({
        baseURL:"/api",       // 基地址
        timeout:6000,     // 设置超时时间（单位：毫秒），一旦超过该时间没有收到后端的数据，就直接报错。
    });
    return  instance(config);
};

export default request;