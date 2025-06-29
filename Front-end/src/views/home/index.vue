<template>
<div class="h-full flex flex-col gap-2">
  <HomeNavbar></HomeNavbar>
  <SystemAdminManagement v-if="loginStore.adminRole===0"></SystemAdminManagement>
  <SchoolAdminManagement v-if="loginStore.adminRole===1"></SchoolAdminManagement>
</div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import { HomeNavbar, SystemAdminManagement, SchoolAdminManagement } from "../../components"
import { useMainStore } from "../../stores";

const loginStore = useMainStore().useLoginStore()
</script>

<style scoped>

</style>