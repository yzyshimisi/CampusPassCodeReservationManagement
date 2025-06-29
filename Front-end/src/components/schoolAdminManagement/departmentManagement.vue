<template>
<div class="flex flex-col gap-2">
  <div class="w-[400px]">
    <label class="input input-bordered flex items-center gap-2 w-[250px] float-right">
      <input v-model="fuzzyDepartName" type="text" class="grow" placeholder="通过部门名模糊搜索" />
      <el-icon><Search /></el-icon>
    </label>
  </div>
  <div class="overflow-x-auto h-[80vh] w-[400px]">
    <table class="table text-base">
      <thead>
      <tr class="text-base text-center bg-base-300">
        <th>ID</th>
        <th>部门类型</th>
        <th>部门名称</th>
        <th><button onclick="addDepartDia.showModal()" class="btn btn-outline btn-sm hover:bg-base-200 hover:text-black">添加部门</button></th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(value,index) in departments" class="hover text-center">
        <td>{{ value.id }}</td>
        <td>{{ DEPARTMENT_TYPE[value.departmentType] }}</td>
        <td>{{ value.departmentName }}</td>
        <td>
          <div class="dropdown dropdown-bottom dropdown-end" id="dropdown">
            <div tabindex="0" role="button" class="btn m-1 btn-ghost"><el-icon size="20"><MoreFilled /></el-icon></div>
            <ul tabindex="0" id="dropdown-content" class="dropdown-content menu open bg-base-100 rounded-box z-[1] w-32 p-2 shadow text-base" >
              <li><a @click="deleteDepartment(value.departmentName)">删除</a></li>
              <li><a @click="openUpdateDia(index)">修改信息</a></li>
            </ul>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</div>
<div>
  <!--  添加部门对话框 -->
  <dialog id="addDepartDia" class="modal">
    <div class="modal-box w-[350px]">
      <form method="dialog">
        <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
      </form>
      <h3 class="text-xl font-bold">添加部门</h3>
      <div class="flex flex-col gap-4 mt-4">
        <div>
          <select v-model="addDepartData.departmentType" class="select select-bordered text-base">
            <option disabled selected :value="-1">请选择部门类型</option>
            <option :value="0">行政部门</option>
            <option :value="1">直属部门</option>
            <option :value="2">学院</option>
          </select>
        </div>
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          部门名称：
          <input v-model="addDepartData.departmentName" type="text" class="grow" placeholder="请输入部门名称" />
        </label>
        <div class="flex justify-center">
          <button @click="addDepartment" class="btn btn-active px-16">添加</button>
        </div>
      </div>
    </div>
  </dialog>
  <!--  修改信息对话框 -->
  <dialog id="updateDepartDia" class="modal">
    <div class="modal-box w-[350px]">
      <form method="dialog">
        <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
      </form>
      <h3 class="text-lg font-bold">修改部门信息</h3>
      <div class="flex flex-col gap-4 mt-4">
        <div>
          <select v-model="updateDepartData.departmentType" class="select select-bordered text-base">
            <option :value="0">行政部门</option>
            <option :value="1">直属部门</option>
            <option :value="2">学院</option>
          </select>
        </div>
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          部门名称：
          <input v-model="updateDepartData.departmentName" type="text" class="grow w-[20px]" placeholder="请输入部门名称" />
        </label>
        <div class="flex justify-center">
          <button @click="updateDepartment" class="btn btn-active px-16">修改</button>
        </div>
      </div>
    </div>
  </dialog>
</div>
</template>

<script lang="ts" setup>
import { onBeforeMount, ref, watch } from "vue";
import { useRequest } from "vue-hooks-plus";
import {
  getDepartmentAPI,
  addDepartmentAPI,
  deleteDepartmentAPI,
  updateDepartmentAPI,
  fuzzySearchDepartmentAPI,
  searchAdminByNameAPI
} from "../../apis"
import { ElMessage } from "element-plus";

const departments = ref<Array<departmentType>>()
const DEPARTMENT_TYPE = ['行政部门','直属部门','学院']

const addDepartData = ref<addDepartmentType>({
  departmentType: -1,
  departmentName: ""
})

const updateDepartData = ref<departmentType>({
  id: -1,
  departmentType: -1,
  departmentName: "",
})

const fuzzyDepartName = ref<string>("")

onBeforeMount(()=>{
  getDepartment()
})

const getDepartment = () => {
  useRequest(()=>getDepartmentAPI(),{
    onSuccess(res){
      if(res.data['code']==200){
        departments.value = res.data['data']
        console.log(departments.value)
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}

const { data, run } = useRequest(() => fuzzySearchDepartmentAPI(fuzzyDepartName.value), {
  debounceWait: 1000,
  manual: true,
  onSuccess(res){
    if(fuzzyDepartName.value != ""){
      if(res.data['code']==200){
        departments.value = res.data['data']
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  }
})

watch(()=>fuzzyDepartName, c => {
  if(fuzzyDepartName.value == ""){
    getDepartment()
  }else{
    run()
  }
},{
  deep: true
})

const addDepartment = () => {
  useRequest(()=>addDepartmentAPI(addDepartData.value),{
    onSuccess(res){
      if(res.data['code']==200){
        getDepartment()
        addDepartData.value = {
          departmentType: -1,
          departmentName: ""
        }
        addDepartDia.close()
        ElMessage({message: "添加成功", type: 'success',})
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}

const deleteDepartment = (departmentName:string) => {
  useRequest(()=>deleteDepartmentAPI(departmentName),{
    onSuccess(res){
      if(res.data['code']==200){
        getDepartment()
        ElMessage({message: "删除成功", type: 'success',})
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}

const openUpdateDia = (ind) => {
  Object.assign(updateDepartData.value, departments.value[ind])
  updateDepartDia.showModal()
}

const updateDepartment = () => {
  useRequest(()=>updateDepartmentAPI(updateDepartData.value),{
    onSuccess(res){
      if(res.data['code']==200){
        getDepartment()
        updateDepartDia.close()
        ElMessage({message: "更新成功", type: 'success',})
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}
</script>

<style scoped>

</style>