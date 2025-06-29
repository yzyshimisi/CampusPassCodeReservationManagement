import { defineStore } from 'pinia';
import { ref } from "vue";

// 第一个参数是应用程序中 store 的唯一 id
const loginStore = defineStore(
    'login',
    ()=>{
        const loginSession = ref(false);
        const adminRole = ref<number>(0)

        const setLogin = (loginNewSession: boolean) =>{
            loginSession.value=loginNewSession;
        };
        const setAdminRole = (newAdminRole: number) => {
            adminRole.value = newAdminRole
        }
        return{
            loginSession,
            setLogin,
            adminRole,
            setAdminRole,
        };
    },
    {
        persist: true
    }
);

export default loginStore;