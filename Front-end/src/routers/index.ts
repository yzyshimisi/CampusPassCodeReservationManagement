import { createWebHistory, createRouter } from "vue-router";
import { Reservation_page } from "../views"

const routes = [
    {
        path : "/",
        name : "reservation_page",
        component : Reservation_page,
    },
]

const router = createRouter({
    history:createWebHistory(),    // 路由历史
    routes: routes                 // 编写的路由
})

export default router;