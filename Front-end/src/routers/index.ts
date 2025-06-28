import { createWebHistory, createRouter } from "vue-router";
import { AppointmentPage } from "../views"

const routes = [
    {
        path : "/",
        name : "appointmentPage",
        component : AppointmentPage,
    },
]

const router = createRouter({
    history:createWebHistory(),    // 路由历史
    routes: routes                 // 编写的路由
})

export default router;