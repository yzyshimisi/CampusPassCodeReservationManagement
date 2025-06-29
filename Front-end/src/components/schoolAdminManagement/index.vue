<template>
<div class="w-screen pt-[20px] flex justify-center gap-[50px]">
  <DepartmentManagement></DepartmentManagement>
  <DepartAdminManagement></DepartAdminManagement>
</div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import DepartmentManagement  from "./departmentManagement.vue"
import DepartAdminManagement from "./departAdminManagement.vue"
</script>

<style scoped>

</style>