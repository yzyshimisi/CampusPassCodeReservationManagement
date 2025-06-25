import { createWebHistory, createRouter } from "vue-router";

const routes = [
    {
        path : "/hello",
        name : "Home",
        component : HelloWorld,
    },
]

const router = createRouter({
    history:createWebHistory(),    //路由历史
    routes: routes                  //编写的路由
})

export default router;