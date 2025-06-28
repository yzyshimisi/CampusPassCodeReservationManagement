import { createWebHistory, createRouter } from "vue-router";
import { AppointmentPage } from "../views"
import { RouteRecordRaw } from 'vue-router';

const routes:RouteRecordRaw[] = [
    {
        path: '/',
        redirect: '/appointment'
    },

    {
        path : "/appointment",
        name : "appointmentPage",
        component : AppointmentPage,
    },
]

const router = createRouter({
    history:createWebHistory(),    // 路由历史
    routes: routes                 // 编写的路由
})

export default router;