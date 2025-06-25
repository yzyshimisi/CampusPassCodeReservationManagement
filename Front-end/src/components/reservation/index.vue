<template>
<div class="my_container" id="my_container">
  <div class="title">社会公众预约</div>
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
            v-model="info.starting_time"
            type="date"
            placeholder="选择起始时间"
        />
      </td>
    </tr>
    <tr>
      <td>
        <el-date-picker
            v-model="info.end_time"
            type="date"
            placeholder="选择结束时间"
        />
      </td>
    </tr>
    <tr>
      <td>所在单位</td>
      <td><input type="text" placeholder="请输入所在单位" class="input input-sm input-bordered w-full" /></td>
    </tr>
    <tr>
      <th colspan="2" class="table_title">访客信息</th>
    </tr>
    <tr>
      <td>姓名</td>
      <td><input v-model="info.name" type="text" placeholder="请输入姓名" class="input input-sm input-bordered w-full" /></td>
    </tr>
    <tr>
      <td>身份证号</td>
      <td><input v-model="info.ID" type="text" placeholder="请输入身份证号" class="input input-sm input-bordered w-full" /></td>
    </tr>
    <tr>
      <td>手机号</td>
      <td><input v-model="info.phone" type="text" placeholder="请输入手机号" class="input input-sm input-bordered w-full" /></td>
    </tr>
    <tr>
      <td>交通方式</td>
      <td>
        <select v-model="info.transportation" class="select select-bordered select-sm w-full">
          <option value="步行" selected>步行</option>
          <option value="驾车">驾车</option>
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
  <div v-for="(value, index) in info.entourages">
    <table class="table">
      <tbody>
      <tr>
        <th colspan="2" class="table_title">
          <div>随行人员{{ index+1 }}<el-icon @click="deleteEntourage(index)" size="15"><CloseBold /></el-icon></div>
        </th>
      </tr>
      <tr>
        <td>姓名</td>
        <td><input v-model="value['name']" type="text" placeholder="请输入姓名" class="input input-sm input-bordered w-full" /></td>
      </tr>
      <tr>
        <td>身份证号</td>
        <td><input v-model="value['ID']" type="text" placeholder="请输入身份证号" class="input input-sm input-bordered w-full" /></td>
      </tr>
      <tr>
        <td>手机号</td>
        <td><input v-model="value['phone']" type="text" placeholder="请输入手机号" class="input input-sm input-bordered w-full" /></td>
      </tr>
      </tbody>
    </table>
  </div>
  <div>
    <button @click="addEntourage()" class="btn text-base text-green-700"><el-icon size="23"><CirclePlus /></el-icon>添加</button>
  </div>
</div>
</template>

<script lang="ts" setup>
import { ref, reactive } from "vue";
import { inputNumberEmits } from "element-plus";
import { nextTick, onBeforeMount  } from "vue";
import { useRequest } from "vue-hooks-plus";
import { testAPI } from "../../apis"

let date = new Date();
const nowDate = ref<String>(String(date.getFullYear()).concat('-').concat(String(date.getMonth())).concat('-').concat(String(date.getDate())));

const entourages = ref<Array<Object>>([])

const info = reactive({
  campus: -1,
  starting_time: '',
  end_time: '',
  unit: '',
  name: '',
  ID: '',
  phone: '',
  transportation: '步行',
  plate_number: '',
  entourages: []
})

const addEntourage = () => {
  info.entourages.push({
    name: '',
    ID: '',
    phone: '',
  })

   nextTick(()=>{
    let my_container = document.getElementById('my_container')
    my_container.scrollTop = my_container.scrollHeight;
  })
}

const deleteEntourage = (index) => {
  info.entourages = info.entourages.filter((_, i) => i !== index);
}
</script>

<style scoped>
@import "./index.module.css";
</style>