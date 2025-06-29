<template>
<div class="my_container">
  <CheckForm v-if="appointmentRecords.length===0" @getAppointmentRecord="getAppointmentRecord"></CheckForm>
  <MyRecord
      v-else
      :key="refreshRecord"
      :appointmentRecords="appointmentRecords"
      :id_number="id_number"
      @refresh="refresh"
      @logout="logout"
  ></MyRecord>
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

const refreshRecord = ref<number>(0)

const getAppointmentRecord = (data:Array<appointmentRecordType>, id: string) => { // 返回输入的身份证
  if(data.length == 0){
    ElMessage({message: '没有预约记录', type: 'warning',})
    return
  }else{
    appointmentRecords.value = data;
    id_number.value = id;
  }
}

const refresh = (data:checkAppointmentType) => {
  setTimeout(()=>{
    useRequest(()=>checkAppointmentAPI(data),{
      onSuccess(res){
        if(res.data['code']==200){
          appointmentRecords.value = res.data['data']['appointmentRecords']
          id_number.value = res.data['data']['id_number']
          refreshRecord.value = (refreshRecord.value + 2) % 20
          ElMessage({message: '刷新成功', type: 'success',})
        }else{
          ElMessage({message: res.data['msg'], type: 'warning',})
        }
      },
    })
  },1000)
}

const logout = () => {
  appointmentRecords.value = []
  id_number.value = ""
}
</script>

<style scoped>

</style>