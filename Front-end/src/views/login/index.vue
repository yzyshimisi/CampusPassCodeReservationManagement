<template>
<div class="my_page">
  <div class="card bg-base-100 w-96 shadow-xl">
    <div class="card-body">
      <h2 class="card-title">管理员登录</h2>
      <div class="loginForm">
        <label class="input input-bordered flex items-center gap-2">
          <el-icon><User /></el-icon>
          <input v-model="info.login_name" type="text" class="grow" placeholder="请输入用户名" />
        </label>
        <label class="input input-bordered flex items-center gap-2">
          <el-icon><Key /></el-icon>
          <input v-model="info.login_password" type="password" class="grow" placeholder="请输入密码" />
        </label>
        <div>
          <select v-model="info.admin_role" class="select select-bordered">
            <option disabled selected :value="-1">请选择管理员身份</option>
            <option :value="0">系统管理员</option>
            <option :value="1">学校管理员</option>
            <option :value="2">审计管理员</option>
            <option :value="3">部门管理员</option>
          </select>
        </div>
        <button @click="login()" class="btn btn-accent">登录</button>
      </div>
    </div>
  </div>
</div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import { useRequest } from "vue-hooks-plus";
import { loginAPI } from "../../apis"
import { ElMessage } from "element-plus";
import { useMainStore } from "../../stores"
import router from "../../routers";

const info = ref<loginDataType>({
  login_name: "",
  login_password: "",
  admin_role: -1,
})

const login = () => {
  useRequest(()=>loginAPI(info.value),{
    onSuccess(res){
      if(res.data['code']==200){
        localStorage.setItem("token", res.data['data']['token']);

        let loginStore = useMainStore().useLoginStore()
        loginStore.setLogin(true)
        loginStore.setAdminRole(res.data['data']['admin_role'])

        router.push("/home")

      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}
</script>

<style scoped>
@import "./index.module.css";
</style>