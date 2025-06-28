<template>
<div>
  <h3 class="text-xl font-bold flex items-center relative">
    {{ appointmentRecord['appointment_info']['application_time'].split(' ')[0] }}（{{ appointmentRecord['appointment_info']['attribute'] }}）
    <button @click="closeDetail()" class="btn btn-sm btn-circle btn-ghost absolute right-2 ">✕</button>
  </h3>
  <div class="overflow-x-auto mt-4">
    <table class="table">
      <tbody>
      <tr>
        <td colspan="2" class="table_title">预约信息</td>
      </tr>
      <tr>
        <td>预约校区</td>
        <td>{{ campus[appointmentRecord['appointment_info']['campus']] }}</td>
      </tr>
      <tr>
        <td>进校日期</td>
        <td>{{ appointmentRecord['appointment_info']['entry_time'] }}</td>
      </tr>
      <tr>
        <td>截至日期</td>
        <td>{{ appointmentRecord['appointment_info']['end_time'] }}</td>
      </tr>
      <tr>
        <td>所在单位</td>
        <td>{{ appointmentRecord['appointment_info']['organization'] }}</td>
      </tr>
      <tr>
        <td>交通方式</td>
        <td>{{ appointmentRecord['appointment_info']['transport_mode'] ? '驾车' : '步行' }}</td>
      </tr>
      <tr v-if="appointmentRecord['appointment_info']['transport_mode']">
        <td>车牌号码</td>
        <td>{{ appointmentRecord['appointment_info']['plate_number'] }}</td>
      </tr>
      </tbody>
    </table>
    <table v-if="appointmentRecord['appointment_info']['attribute']==='公务预约'" class="table">
      <tbody>
      <tr>
        <td colspan="2" class="table_title">公务信息</td>
      </tr>
      <tr>
        <td>公务访问部门</td>
        <td>{{ appointmentRecord['appointment_info']['visiting_department'] }}</td>
      </tr>
      <tr>
        <td>公务访问接待人</td>
        <td>{{ appointmentRecord['appointment_info']['contact_person'] }}</td>
      </tr>
      <tr>
        <td>来访事由</td>
        <td>{{ appointmentRecord['appointment_info']['visit_purpose'] }}</td>
      </tr>
      </tbody>
    </table>
    <table class="table">
      <tbody>
      <tr>
        <td colspan="2" class="table_title">预约状态</td>
      </tr>
      <tr>
        <td>{{ appointmentRecord['appointment_info']['attribute']==='公务预约' ? status[appointmentRecord['appointment_info']['approval_status']] : '自动审核' }}</td>
        <td><button @click="getPassCode()" class="btn btn-sm btn-outline" :disabled="appointmentRecord['appointment_info']['attribute']==='公务预约' && appointmentRecord['appointment_info']['approval_status']!==2 ">查看通行码</button></td>
      </tr>
      </tbody>
    </table>
    <table class="table">
      <tbody>
      <tr>
        <td colspan="2" class="table_title">人员1（{{ appointmentRecord['appointment_person']['is_applicant'] ? '申请人' : '随行人员' }}）</td>
      </tr>
      <tr>
        <td>姓名</td>
        <td>{{ appointmentRecord['appointment_person']['full_name'] }}</td>
      </tr>
      <tr>
        <td>身份证号</td>
        <td>{{ appointmentRecord['appointment_person']['mask_id_number'] }}</td>
      </tr>
      <tr>
        <td>手机号</td>
        <td>{{ appointmentRecord['appointment_person']['phone'] }}</td>
      </tr>
      </tbody>
    </table>
    <table v-for="(value, index) in appointmentRecord['entourages']" class="table">
      <tbody>
      <tr>
        <td colspan="2" class="table_title">人员{{index+2}}（{{ value['is_applicant'] ? '申请人' : '随行人员' }}）</td>
      </tr>
      <tr>
        <td>姓名</td>
        <td>{{ value['full_name'] }}</td>
      </tr>
      <tr>
        <td>身份证号</td>
        <td>{{ value['mask_id_number'] }}</td>
      </tr>
      <tr>
        <td>手机号</td>
        <td>{{ value['phone'] }}</td>
      </tr>
      </tbody>
    </table>
  </div>
  <dialog v-if="passCodeInfo" id="passCodeDia" class="modal">
    <div class="modal-box">
      <form method="dialog">
        <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
      </form>
      <h3 class="text-xl font-bold">{{ appointmentRecord['appointment_person']['full_name'] }}的通行码</h3>
      <div class="passCodeContainer">
        <p>{{ passCodeInfo.generation_time }}</p>
        <img :src="'data:image/png;base64,' + passCodeInfo.base64_image">
        <p v-show="passCodeInfo.effective">有效时间至：{{ appointmentRecord['appointment_info']['end_time'] }}</p>
        <p v-show="passCodeInfo.effective">凭此码或身份证进校，并服从学校相关管理规定</p>
        <p v-show="!passCodeInfo.effective">当前时间不在有效预约时间内，暂不可进校</p>
      </div>
    </div>
  </dialog>
</div>
</template>

<script lang="ts" setup>
import { nextTick, ref } from "vue";
import { useRequest } from "vue-hooks-plus";
import { getPassCodeAPI } from "../../apis"
import { ElMessage } from "element-plus";

const props = defineProps(['appointmentRecord','id_number'])
const varemit = defineEmits(['closeDetail'])

const appointmentRecord = ref<appointmentRecordType>(props.appointmentRecord)

const campus = ['朝晖校区','屏峰校区','莫干山校区']
const status = ['待审批','拒绝','审批通过']

const passCodeInfo = ref<passCodeResType>();

const closeDetail = () => {
  varemit('closeDetail')
}

const getPassCode = () => {
  let isPublic = 1

  if(
      appointmentRecord.value['appointment_info'].hasOwnProperty('visiting_department') &&
      appointmentRecord.value['appointment_info'].hasOwnProperty('contact_person') &&
      appointmentRecord.value['appointment_info'].hasOwnProperty('visit_purpose') &&
      appointmentRecord.value['appointment_info'].hasOwnProperty('approval_status')
  ) {
    isPublic = 0;
  }

  let data:getPassCodeType = {
    isPublic: isPublic,
    appointment_person_id: appointmentRecord.value['appointment_person']['id'],
    full_name: appointmentRecord.value['appointment_person']['full_name'],
    id_number: props.id_number,
    phone: appointmentRecord.value['appointment_person']['phone']
  }

  useRequest(()=>getPassCodeAPI(data),{
    onSuccess(res){
      if(res.data['code']==200){
        passCodeInfo.value = res.data['data']
        nextTick(()=>{passCodeDia.showModal()})
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