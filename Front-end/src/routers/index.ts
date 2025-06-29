import { createWebHistory, createRouter } from "vue-router";
import { AppointmentPage, Login, Home } from "../views"
import { RouteRecordRaw } from 'vue-router';
import pinia from "../stores/createPinia"
import { useMainStore } from "../stores";

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
    },
    {
        path: "/home",
        name: "home",
        component: Home
    }
]

const router = createRouter({
    history:createWebHistory(),    // 路由历史
    routes: routes                 // 编写的路由
})


router.beforeEach((to,_,next) => {
    //loginSession 是登录的标记，true表示已经登录，false代表未登录
    const newloginStore = useMainStore(pinia).useLoginStore()
    if(newloginStore.loginSession === false && to.path != "/" && to.path != "/appointment"){
        //解决无限重定向的问题
        if(to.path === "/login"){
            next();
        }else{
            next("/login");
        }
    }else{
        next();
    }
});

export default router;