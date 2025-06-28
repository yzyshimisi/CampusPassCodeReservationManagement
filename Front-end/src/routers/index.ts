import { createWebHistory, createRouter } from "vue-router";
import { AppointmentPage, Login } from "../views"
import { RouteRecordRaw } from 'vue-router';

const routes: Array<RouteRecordRaw> = [
    {
        path : '/',
        redirect: '/appointment'
    },

    {
        path : "/appointment",
        name : "appointmentPage",
        component : AppointmentPage,
    },

    {
        path : "/login",
        name : "login",
        component: Login,
    }
]

const router = createRouter({
    history:createWebHistory(),    // 路由历史
    routes: routes                 // 编写的路由
})

export default router;