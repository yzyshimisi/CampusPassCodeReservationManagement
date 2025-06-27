<template>
<div class="my_container">
  <CheckForm v-if="appointmentRecords.length===0" @getAppointmentRecord="getAppointmentRecord"></CheckForm>
  <MyRecord v-else :appointmentRecords="appointmentRecords" :id_number="id_number"></MyRecord>
</div>
</template>

<script lang="ts" setup>
import { onBeforeMount, ref, reactive } from "vue";
import { useRequest } from "vue-hooks-plus/es";
import { checkAppointmentAPI } from "../../apis"
import { ElMessage } from "element-plus"
import CheckForm from "./checkForm.vue"
import MyRecord from "./myRecord.vue"

const appointmentRecords = ref<Array<appointmentRecordType>>([]);
const id_number = ref<string>("");

const getAppointmentRecord = (data:Array<appointmentRecordType>, _id_number: string) => {
  if(data.length == 0){
    ElMessage({message: '没有预约记录', type: 'warning',})
    return
  }else{
    appointmentRecords.value = data;
    id_number.value = _id_number;
  }
}
</script>

<style scoped>

</style>