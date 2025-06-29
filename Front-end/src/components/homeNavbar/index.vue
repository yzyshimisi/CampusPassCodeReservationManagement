<template>
<div class="navbar bg-base-100 shadow-lg">
  <div class="flex-1">
    <a class="btn btn-ghost text-xl">{{ loginStore.adminRole >= 0 ? ADMIN_ROLE[loginStore.adminRole] : '未登录'  }}</a>
<!--    <button v-if="loginStore.adminRole===0" @click="varemit('changeComp',0)" class="btn btn-ghost">管理员管理</button>-->
<!--    <button v-if="loginStore.adminRole===1" @click="varemit('changeComp',1)" class="btn btn-ghost">部门管理</button>-->
<!--    <button v-if="loginStore.adminRole===1" @click="varemit('changeComp',2)" class="btn btn-ghost">部门管理员管理</button>-->
  </div>
  <div class="flex-none">
    <div class="dropdown dropdown-end">
      <div tabindex="0" role="button" class="btn btn-square btn-ghost"><el-icon size="20"><MoreFilled /></el-icon></div>
      <ul tabindex="0" class="dropdown-content menu bg-base-100 rounded-box z-[1] p-2 shadow">
        <li><a @click="logout()" class="whitespace-nowrap text-base">退出登录</a></li>
      </ul>
    </div>
  </div>
</div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import { useMainStore } from "../../stores";
import router from "../../routers";

const loginStore = useMainStore().useLoginStore()
const ADMIN_ROLE = ['系统管理员','学校管理员','审计管理员','部门管理员']

// const varemit = defineEmits(['changeComp'])

const logout = () => {
  loginStore.setLogin(false);
  loginStore.setAdminRole(-1);

  localStorage.removeItem("token")

  router.push("/login")
}
</script>

<style scoped>

</style>