<template>
<div class="my_container" id="my_container">
  <div v-if="isPublic" class="title">社会公众预约</div>
  <div v-else class="title">社会公务预约</div>
  <div class="switchButton">
    <p>公众</p>
    <input
        @click="changeComponent"
        type="checkbox"
        class="toggle bg-black [--tglbg:white] hover:bg-black"
        :checked="!isPublic" />
    <p>公务</p>
  </div>
  <table class="table text-base">
    <tbody>
    <tr>
      <th colspan="2" class="table_title">预约信息</th>
    </tr>
    <tr>
      <td>申请日期</td>
      <td>{{ nowDate }}</td>
    </tr>
    <tr>
      <td>预约校区</td>
      <td class="Campus">
        <div><input v-model="info.campus" :value="0" type="radio" name="Campus" class="radio" checked="checked" />朝晖校区</div>
        <div><input v-model="info.campus" :value="1" type="radio" name="Campus" class="radio" />屏峰校区</div>
        <div><input v-model="info.campus" :value="2" type="radio" name="Campus" class="radio" />莫干山校区</div>
      </td>
    </tr>
    <tr>
      <td rowspan="2">预约进校时间</td>
      <td>
        <el-date-picker
            v-model="entry_time"
            type="date"
            placeholder="选择起始时间"
        />
      </td>
    </tr>
    <tr>
      <td>
        <el-date-picker
            v-model="end_time"
            type="date"
            placeholder="选择结束时间"
        />
      </td>
    </tr>
    <tr>
      <td>所在单位</td>
      <td><input v-model="info.organization" type="text" placeholder="请输入所在单位" class="input input-sm input-bordered w-full" /></td>
    </tr>
    <tr v-if="!isPublic">
      <th colspan="2" class="table_title">公务信息</th>
    </tr>
    <tr v-if="!isPublic">
      <td>访问部门</td>
      <td><input v-model="info.visiting_department" type="text" placeholder="请输入访问部门" class="input input-sm input-bordered w-full" /></td>
    </tr>
    <tr v-if="!isPublic">
      <td>接待人</td>
      <td><input v-model="info.contact_person" type="text" placeholder="请输入接待人" class="input input-sm input-bordered w-full" /></td>
    </tr>
    <tr v-if="!isPublic">
      <td>来访事由</td>
      <td><textarea v-model="info.visit_purpose" class="textarea textarea-bordered" placeholder="请输入来访事由"></textarea></td>
    </tr>
    <tr>
      <th colspan="2" class="table_title">访客信息</th>
    </tr>
    <tr>
      <td>姓名</td>
      <td><input v-model="info.full_name" type="text" placeholder="请输入姓名" class="input input-sm input-bordered w-full" /></td>
    </tr>
    <tr>
      <td>身份证号</td>
      <td><input v-model="info.id_number" type="text" placeholder="请输入身份证号" class="input input-sm input-bordered w-full" /></td>
    </tr>
    <tr>
      <td>手机号</td>
      <td><input v-model="info.phone" type="text" placeholder="请输入手机号" class="input input-sm input-bordered w-full" /></td>
    </tr>
    <tr>
      <td>交通方式</td>
      <td>
        <select v-model="info.transport_mode" class="select select-bordered select-sm w-full">
          <option :value="0" selected>步行</option>
          <option :value="1">驾车</option>
        </select>
      </td>
    </tr>
    <tr>
      <td>车牌号</td>
      <td><input v-model="info.plate_number" type="text" placeholder="请输车牌号（可选）" class="input input-sm input-bordered w-full" /></td>
    </tr>
    </tbody>
  </table>
  <div class="divider"></div>
  <div v-for="(value, index) in info.entourages" class="entouragesContainer">
    <table class="table">
      <tbody>
      <tr>
        <th colspan="2" class="table_title">
          <div>随行人员{{ index+1 }}<el-icon @click="deleteEntourage(index)" size="15"><CloseBold /></el-icon></div>
        </th>
      </tr>
      <tr>
        <td>姓名</td>
        <td><input v-model="value['full_name']" type="text" placeholder="请输入姓名" class="input input-sm input-bordered w-full" /></td>
      </tr>
      <tr>
        <td>身份证号</td>
        <td><input v-model="value['id_number']" type="text" placeholder="请输入身份证号" class="input input-sm input-bordered w-full" /></td>
      </tr>
      <tr>
        <td>手机号</td>
        <td><input v-model="value['phone']" type="text" placeholder="请输入手机号" class="input input-sm input-bordered w-full" /></td>
      </tr>
      </tbody>
    </table>
  </div>
  <div class="button_container">
    <button @click="addEntourage()" class="btn text-base text-green-700"><el-icon size="23"><CirclePlus /></el-icon>添加</button>
    <button @click="submit()" class="btn text-base submitButton">提交</button>
  </div>
</div>
</template>

<script lang="ts" setup>
import { nextTick, reactive, ref } from "vue";
import { makeAppointmentAPI } from "../../apis";
import { useRequest } from "vue-hooks-plus";
import { ElMessage } from "element-plus";

const varemit = defineEmits(['scrollToEnd'])  // 告诉父组件，滚动条滚至最低部

const isPublic = ref<boolean>(true)

const changeComponent = () => {     // 公众预约还是公务预约
  if(isPublic.value){
    info['visiting_department'] = ''
    info['contact_person'] = ''
    info['visit_purpose'] = ''
    info.isPublic = 0
  }else{
    delete info['visiting_department']
    delete info['contact_person']
    delete info['visit_purpose']
    info.isPublic = 1
  }
  isPublic.value = ! isPublic.value
}

let date = new Date();
const nowDate = ref<String>(String(date.getFullYear()).concat('-').concat('0'.concat(String(date.getMonth()+1))).concat('-').concat(String(date.getDate())));

const entry_time = ref<Date>()
const end_time = ref<Date>()

const info = reactive<addAppointmentType>({
  isPublic: 1,
  campus: 0,
  entry_time: '',
  end_time: '',
  organization: '',
  full_name: '',
  id_number: '',
  phone: '',
  transport_mode: 0,
  plate_number: '',
  entourages: []
})

const addEntourage = () => {
  info.entourages.push({
    full_name: '',
    id_number: '',
    phone: '',
  })

  varemit('scrollToEnd')
}

const deleteEntourage = (index) => {
  info.entourages = info.entourages.filter((_, i) => i !== index);
}

const submit = () => {
  if(
      !entry_time.value || !end_time.value || info.organization=="" || info.full_name=="" ||
      info.id_number=="" || info.phone=="" || (info.transport_mode==1 && info.plate_number == "") ||
      (info.isPublic == 0 && (info.visiting_department == "" || info.contact_person == "" || info.visit_purpose == ""))
  ){
    ElMessage({message: '表单字段不允许为空', type: 'warning',})
    return
  }

  for(let i=0; i<info.entourages.length; i++){
    if(info.entourages[i].full_name=="" || info.entourages[i].id_number=="" || info.entourages[i].phone==""){
      ElMessage({message: '表单字段不允许为空', type: 'warning',})
      return
    }
  }

  info.entry_time = String(entry_time.value.getFullYear()).concat("-").concat(String(entry_time.value.getMonth()+1).padStart(2,'0')).concat('-').concat(String(entry_time.value.getDate()).padStart(2,'0'))
  info.end_time = String(end_time.value.getFullYear()).concat("-").concat(String(end_time.value.getMonth()+1).padStart(2,'0')).concat('-').concat(String(end_time.value.getDate()).padStart(2,'0'))
  useRequest(()=>makeAppointmentAPI(info),{
    onSuccess(res){
      if(res.data['code']==200){
        Object.assign(info,{
          isPublic: 1,
          campus: 0,
          entry_time: '',
          end_time: '',
          organization: '',
          full_name: '',
          id_number: '',
          phone: '',
          transport_mode: 0,
          plate_number: '',
          entourages: []
        })
        ElMessage({message: '预约成功', type: 'success',})
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