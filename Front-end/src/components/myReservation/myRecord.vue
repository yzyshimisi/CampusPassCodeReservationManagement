<template>
<div class="recordContainer">
  <div v-for="(value,index) in props.appointmentRecords" class="card bg-base-100 shadow-xl">
    <div @click="showDetail(index)" class="card-body text-base">
      <h2 class="card-title text-lg">{{ value['application_time'].split(' ')[0] }}（{{ value['attribute'] }}）</h2>
      <div>
        <span class="text-base">进校日期：{{ value['entry_time'].split(' ')[0] }}</span>
        <p>预约校区：{{ campus[value['campus']] }}</p>
      </div>
      <h2 class="card-title text-lg">预约身份：{{ value['entourages'].length > 1 ? '申请人' : '随行人员' }}</h2>
    </div>
  </div>
</div>
<dialog id="recordDetailModal" class="modal">
  <div class="modal-box">
    <form method="dialog">
      <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
    </form>
    <h3 class="text-xl font-bold">{{ appointmentRecords[showDetailInd]['application_time'].split(' ')[0] }}（{{appointmentRecords[showDetailInd]['attribute'] }}）</h3>
    <div class="overflow-x-auto mt-4">
      <table class="table">
        <tbody>
        <tr>
          <td colspan="2" class="table_title">预约信息</td>
        </tr>
        <tr>
          <td>预约校区</td>
          <td>{{ campus[appointmentRecords[showDetailInd]['campus']] }}</td>
        </tr>
        <tr>
          <td>进校日期</td>
          <td>{{ appointmentRecords[showDetailInd]['entry_time'] }}</td>
        </tr>
        <tr>
          <td>截至日期</td>
          <td>{{ appointmentRecords[showDetailInd]['end_time'] }}</td>
        </tr>
        <tr>
          <td>所在单位</td>
          <td>{{ appointmentRecords[showDetailInd]['organization'] }}</td>
        </tr>
        <tr>
          <td>交通方式</td>
          <td>{{ appointmentRecords[showDetailInd]['transport_mode'] ? '驾车' : '步行' }}</td>
        </tr>
        <tr v-if="appointmentRecords[showDetailInd]['transport_mode']">
          <td>车牌号码</td>
          <td>{{ appointmentRecords[showDetailInd]['plate_number'] }}</td>
        </tr>
        </tbody>
      </table>
      <table v-if="appointmentRecords[showDetailInd]['attribute']==='公务预约'" class="table">
        <tbody>
        <tr>
          <td colspan="2" class="table_title">公务信息</td>
        </tr>
        <tr>
          <td>公务访问部门</td>
          <td>{{ appointmentRecords[showDetailInd]['visiting_department'] }}</td>
        </tr>
        <tr>
          <td>公务访问接待人</td>
          <td>{{ appointmentRecords[showDetailInd]['contact_person'] }}</td>
        </tr>
        <tr>
          <td>来访事由</td>
          <td>{{ appointmentRecords[showDetailInd]['visit_purpose'] }}</td>
        </tr>
        </tbody>
      </table>
      <table class="table">
        <tbody>
        <tr>
          <td colspan="2" class="table_title">预约状态</td>
        </tr>
        <tr>
          <td>公务访问部门</td>
          <td>{{ appointmentRecords[showDetailInd]['visiting_department'] }}</td>
        </tr>
        </tbody>
      </table>
      <table v-for="(value, index) in appointmentRecords[showDetailInd]['entourages']" class="table">
        <tbody>
        <tr>
          <td colspan="2" class="table_title">人员{{index+1}}（{{ value['is_applicant'] ? '申请人' : '随行人员' }}）</td>
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
  </div>
</dialog>
</template>

<script lang="ts" setup>
import { onBeforeMount, ref } from "vue"
import { useRequest } from "vue-hooks-plus";
import { getAppointmentStatusAPI } from "../../apis"
import {ElMessage} from "element-plus";

const props = defineProps(['appointmentRecords', 'id_number'])

const appointmentRecords = ref(props.appointmentRecords)
const showDetailInd = ref<number>(0)

const campus = ['朝晖校区','屏峰校区','莫干山校区']

for(let i=0; i<appointmentRecords.value.length; i++){
  if(appointmentRecords.value[i].hasOwnProperty('visiting_department') && appointmentRecords.value[i].hasOwnProperty('contact_person') && appointmentRecords.value[i].hasOwnProperty('visit_purpose') && appointmentRecords.value[i].hasOwnProperty('approval_status')){
    appointmentRecords.value[i]['attribute'] = '公务预约'
  }else{
    appointmentRecords.value[i]['attribute'] = '公众预约'
  }
}

const showDetail = (ind) => {
  showDetailInd.value = ind;

  let appointmentRecord : appointmentRecordType = appointmentRecords.value[ind]
  let data: getAppointmentStatusType = getInfo(appointmentRecord)

  useRequest(()=>getAppointmentStatusAPI(data),{
    onSuccess(res){
      if(res.data['code']==200){
        recordDetailModal.showModal()
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}

// 获取要构造通行码的数据
const getInfo = (appointmentRecord) => {
  let isPublic = 1

  if(
      appointmentRecord.hasOwnProperty('visiting_department') &&
      appointmentRecord.hasOwnProperty('contact_person') &&
      appointmentRecord.hasOwnProperty('visit_purpose') &&
      appointmentRecord.hasOwnProperty('approval_status')
  ) {
    isPublic = 0;
  }

  if(appointmentRecord['entourages'].length > 1){   // 是申请人
    for(let i=0; i<appointmentRecord['entourages'].length; i++){
      if(appointmentRecord['entourages'][i]['is_applicant']){
        return {
          isPublic: isPublic,
          appointment_person_id: appointmentRecord['id'],
          full_name: appointmentRecord['entourages'][i]['full_name'],
          id_number: props.id_number,
          phone: appointmentRecord['entourages'][i]['phone']
        }
      }
    }
  }else{
    return {
      isPublic: isPublic,
      appointment_person_id: appointmentRecord['id'],
      full_name: appointmentRecord['entourages'][0]['full_name'],
      id_number: props.id_number,
      phone: appointmentRecord['entourages'][0]['phone']
    }
  }

}
</script>

<style scoped>
@import "./index.module.css";
</style>