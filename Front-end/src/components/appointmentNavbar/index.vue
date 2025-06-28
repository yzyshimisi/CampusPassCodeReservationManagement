<template>
  <div class="navbar bg-base-10 reservation_navbar">
    <button @click="change_page(false)" :class="{'button_selected': !check_reservation, 'button_unselected': check_reservation}">我要预约</button>
    <button @click="change_page(true)" :class="{'button_selected': check_reservation, 'button_unselected': !check_reservation}">我的预约</button>
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";

const check_reservation = ref<boolean>(false)

const varemit = defineEmits(['change_page'])

const change_page = (v:boolean) => {
  check_reservation.value = v
  varemit("change_page", v)
}
</script>

<style>
@import "./index.module.css";
</style>