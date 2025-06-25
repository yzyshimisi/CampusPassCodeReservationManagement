<template>
<div class="my_page">
  <Reservation_navbar @change_page="change_page"></Reservation_navbar>
  <div class="my_container">
    <Reservation v-if="!check_reservation"></Reservation>
  </div>
</div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import { Reservation_navbar, Reservation } from "../../components"

const check_reservation = ref<boolean>(false)

const change_page = (v) => {
  check_reservation.value = v;
}

</script>

<style>
@import "index.module.css";
/*@media  (max-width: 480px) {*/
/*  */
/*}*/
</style>