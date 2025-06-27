<template>
<div class="checkForm">
  <p class="formTitle">预约查询</p>
  <label class="input input-bordered flex items-center gap-2">
    姓名：
    <input v-model="info.full_name" type="text" class="grow" placeholder="请输入您的姓名" />
  </label>
  <label class="input input-bordered flex items-center gap-2">
    身份证号：
    <input v-model="info.id_number" type="text" class="grow" placeholder="请输出身份证号" />
  </label>
  <label class="input input-bordered flex items-center gap-2">
    手机号：
    <input v-model="info.phone" type="text" class="grow" placeholder="请输入手机号" />
  </label>
  <div class="checkButton">
    <button @click="getAppointmentRecord" class="btn">查询</button>
    <button @click="resetForm" class="btn">重置表单</button>
  </div>
  </div>
</template>

<script lang="ts" setup>
import { onBeforeMount, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRequest } from "vue-hooks-plus/es";
import { checkAppointmentAPI } from "../../apis/index.js";

const varemit = defineEmits(['getAppointmentRecord'])

const info = reactive<checkAppointmentType>({
  full_name: 'yzyshimisi',
  id_number: '330302200506248815',
  phone: '15867791873',
})

const appointmentRecords = ref<Array<appointmentRecordType>>([])
const id_number = ref<string>("")   // 后续需要请求获取二维码信息，要用到身份证，这里后端直接返回源身份证

const resetForm = () => {
  Object.assign(info,{
    full_name: '',
    id_number: '',
    phone: '',
  })
}

const getAppointmentRecord = () => {
  if(info.full_name=="" || info.id_number=="" || info.phone==""){
    ElMessage({message: '表单字段不允许为空', type: 'warning',})
    return
  }
  useRequest(()=>checkAppointmentAPI(info),{
    onSuccess(res){
      if(res.data['code']==200){
        appointmentRecords.value = res.data['data']['appointmentRecords']
        id_number.value = res.data['data']['id_number']
        varemit('getAppointmentRecord',appointmentRecords.value, id_number.value);
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