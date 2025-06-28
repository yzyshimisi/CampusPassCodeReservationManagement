<template>
<div class="my_page" id="my_page">
  <AppointmentNavbar @change_page="change_page"></AppointmentNavbar>
  <div class="my_container">
    <AddAppointment v-show="!check_reservation" @scrollToEnd="scrollToEnd()"></AddAppointment>
    <MyAppointment v-show="check_reservation"></MyAppointment>
  </div>
</div>
</template>

<script lang="ts" setup>
import {nextTick, ref} from "vue";
import { AppointmentNavbar, AddAppointment, MyAppointment } from "../../components"

const check_reservation = ref<boolean>(false)

const change_page = (v) => {
  check_reservation.value = v;
}

const scrollToEnd = () => {
  nextTick(()=>{
    let my_container = document.getElementById('my_page')
    console.log(my_container.scrollHeight)
    my_container.scrollTop = my_container.scrollHeight;
  })
}
</script>

<style>
@import "index.module.css";
/*@media  (max-width: 480px) {*/
/*  */
/*}*/
</style>