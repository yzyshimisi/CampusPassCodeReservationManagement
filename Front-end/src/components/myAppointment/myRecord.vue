<template>
<div v-show="showDetailInd===-1">
  <div class="iconContainer">
    <el-icon @click="()=>{ varemit('logout') }" size="25"><User /></el-icon>
    <el-icon @click="refresh()" size="25" :class="isLoadding ? 'spinner': ''"><Refresh /></el-icon>
  </div>
  <div class="recordContainer" v-loading="isLoadding">
    <div v-for="(appointmentRecord,index) in appointmentRecords" class="card bg-base-100 shadow-xl">
      <div @click="showDetail(index)" class="card-body text-base">
        <h2 class="card-title text-lg">{{ appointmentRecord['appointment_info']['application_time'].split(' ')[0] }}（{{ appointmentRecord['appointment_info']['attribute'] }}）</h2>
        <div>
          <span class="text-base">进校日期：{{ appointmentRecord['appointment_info']['entry_time'].split(' ')[0] }}</span>
          <p>预约校区：{{ campus[appointmentRecord['appointment_info']['campus']] }}</p>
        </div>
        <h2 class="card-title text-lg">预约身份：{{ appointmentRecord['appointment_person']['is_applicant'] ? '申请人' : '随行人员' }}</h2>
      </div>
    </div>
  </div>
</div>
<AppointmentDetail
    v-if="showDetailInd>=0"
    :appointmentRecord="appointmentRecords[showDetailInd]"
    :id_number="props.id_number"
    @closeDetail="closeDetail()"
></AppointmentDetail>
</template>

<script lang="ts" setup>
import { onBeforeMount, ref } from "vue"
import AppointmentDetail from "./appointmentDetail.vue"

const props = defineProps(['appointmentRecords', 'id_number'])
const varemit = defineEmits(['refresh', 'logout'])

const appointmentRecords = ref(props.appointmentRecords)
const showDetailInd = ref<number>(-1)

const campus = ['朝晖校区','屏峰校区','莫干山校区']

const isLoadding = ref<boolean>(false)

for(let i=0; i<appointmentRecords.value.length; i++){
  if(appointmentRecords.value[i]['appointment_info'].hasOwnProperty('visiting_department') && appointmentRecords.value[i]['appointment_info'].hasOwnProperty('contact_person') && appointmentRecords.value[i]['appointment_info'].hasOwnProperty('visit_purpose') && appointmentRecords.value[i]['appointment_info'].hasOwnProperty('approval_status')){
    appointmentRecords.value[i]['appointment_info']['attribute'] = '公务预约'
  }else{
    appointmentRecords.value[i]['appointment_info']['attribute'] = '公众预约'
  }
}

const showDetail = (ind) => {
  showDetailInd.value = ind;
}

const closeDetail = () => {
  showDetailInd.value = -1
}

const refresh = () => {
  isLoadding.value = true
  varemit('refresh',{
    full_name: appointmentRecords.value[0]['appointment_person']['full_name'],
    id_number: props.id_number,
    phone: appointmentRecords.value[0]['appointment_person']['phone'],
  });
}
</script>

<style scoped>
@import "./index.module.css";

.spinner {
  border-radius: 50%;
  border-top-color: #3498db;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>