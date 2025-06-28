<template>
<div class="my_page">
  <div class="card bg-base-100 w-96 shadow-xl">
    <div class="card-body">
      <h2 class="card-title">管理员登录</h2>
      <div class="loginForm">
        <label class="input input-bordered flex items-center gap-2">
          <el-icon><User /></el-icon>
          <input type="text" class="grow" placeholder="Username" />
        </label>
        <label class="input input-bordered flex items-center gap-2">
          <el-icon><Key /></el-icon>
          <input type="password" class="grow" placeholder="Username" />
        </label>
        <div>
          <select class="select select-bordered">
            <option disabled selected>请选择管理员身份</option>
            <option>系统管理员</option>
            <option>学校管理员</option>
            <option>部门管理员</option>
            <option>审计管理员</option>
          </select>
        </div>
        <button class="btn btn-accent">登录</button>
      </div>
    </div>
  </div>
</div>
</template>

<script lang="ts" setup>

</script>

<style scoped>
@import "./index.module.css";
</style>