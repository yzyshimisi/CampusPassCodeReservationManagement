<template>
<div class="flex flex-col gap-2">
  <div class="w-[1100px]">
    <label class="input input-bordered flex items-center gap-2 w-[250px] float-right">
      <input v-model="fuzzyName" type="text" class="grow" placeholder="通过姓名模糊搜索" />
      <el-icon><Search /></el-icon>
    </label>
  </div>
  <div class="overflow-x-auto h-[80vh] w-[1100px]">
    <table class="table text-base">
      <thead>
      <tr class="text-base text-center bg-base-300">
        <th>ID</th>
        <th>姓名</th>
        <th>用户名</th>
        <th>手机号</th>
        <th>管理员角色</th>
        <th>部门ID</th>
        <th>授权状态</th>
        <th><button onclick="addAdminDia.showModal()" class="btn btn-outline btn-sm hover:bg-base-200 hover:text-black">添加管理员</button></th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(value,index) in departAdmins" class="hover text-center">
        <td>{{ value.id }}</td>
        <td>{{ value.fullName }}</td>
        <td>{{ value.loginName }}</td>
        <td>{{ value.phone }}</td>
        <td>{{ ADMIN_ROLE[value.adminRole] }}</td>
        <td>{{ value.departmentId ? value.departmentId : 'null' }}</td>
        <td>{{ value.authStatus ? '已授权' : '未授权' }}</td>
        <td>
          <div class="dropdown dropdown-bottom dropdown-end" id="dropdown">
            <div tabindex="0" role="button" class="btn m-1 btn-ghost"><el-icon size="20"><MoreFilled /></el-icon></div>
            <ul tabindex="0" id="dropdown-content" class="dropdown-content menu open bg-base-100 rounded-box z-[1] w-32 p-2 shadow text-base" >
              <li><a @click="deleteDepartAdmin(value.loginName)">删除</a></li>
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
  <!--  添加管理员对话框 -->
  <dialog id="addAdminDia" class="modal">
    <div class="modal-box w-[350px]">
      <form method="dialog">
        <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
      </form>
      <h3 class="text-lg font-bold">添加管理员</h3>
      <div class="flex flex-col gap-4 mt-4">
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          姓名：
          <input v-model="addDepartAdminData.fullName" type="text" class="grow w-[20px]" placeholder="请输入姓名" />
        </label>
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          用户名：
          <input v-model="addDepartAdminData.loginName" type="text" class="grow" placeholder="请输入用户名" />
        </label>
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          手机号：
          <input v-model="addDepartAdminData.phone" type="text" class="grow" placeholder="请输入手机号" />
        </label>
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          部门ID：
          <input v-model="addDepartAdminData.departmentId" type="text" class="grow w-[20px]" placeholder="请输入部门ID" />
        </label>
        <div class="flex gap-4">
          <input @click="()=>{addDepartAdminData.authStatus = 0}" type="radio" name="addAdminDia" class="radio" :checked="addDepartAdminData.authStatus === 0" />未授权
          <input @click="()=>{addDepartAdminData.authStatus = 1}" type="radio" name="addAdminDia" class="radio" :checked="addDepartAdminData.authStatus === 1"/>授权
        </div>
        <div class="flex justify-center">
          <button @click="addDepartAdmin" class="btn btn-active px-16">添加</button>
        </div>
      </div>
    </div>
  </dialog>
  <!--  修改信息对话框 -->
  <dialog id="updateAdminDia" class="modal">
    <div class="modal-box w-[350px]">
      <form method="dialog">
        <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
      </form>
      <h3 class="text-lg font-bold">修改管理员信息</h3>
      <div class="flex flex-col gap-4 mt-4">
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          用户名：
          <input v-model="updateDepartAdminData.loginName" type="text" class="grow" placeholder="请输入用户名" disabled />
        </label>
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          姓名：
          <input v-model="updateDepartAdminData.fullName" type="text" class="grow w-[20px]" placeholder="请输入姓名" />
        </label>
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          手机号：
          <input v-model="updateDepartAdminData.phone" type="text" class="grow" placeholder="请输入手机号" />
        </label>
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          部门ID：
          <input v-model="updateDepartAdminData.departmentId" type="text" class="grow w-[20px]" placeholder="请输入部门ID" />
        </label>
        <div class="flex gap-4">
          <input @click="()=>{updateDepartAdminData.authStatus = 0}" type="radio" name="updateAdminDia" class="radio" :checked="updateDepartAdminData.authStatus === 0" />未授权
          <input @click="()=>{updateDepartAdminData.authStatus = 1}" type="radio" name="updateAdminDia" class="radio" :checked="updateDepartAdminData.authStatus === 1"/>授权
        </div>
        <div class="flex justify-center">
          <button @click="updateDepartAdmin" class="btn btn-active px-16">修改</button>
        </div>
      </div>
    </div>
  </dialog>
</div>
</template>

<script lang="ts" setup>
import { onBeforeMount, ref, watch } from "vue"
import { useRequest } from "vue-hooks-plus";
import { getDepartAdminAPI, fuzzySearchDepartAdminAPI, addDepartAdminAPI, deleteDepartAdminAPI, updateDepartAdminAPI } from "../../apis"
import { ElMessage } from "element-plus";

const departAdmins = ref<Array<adminType>>();

const ADMIN_ROLE = ['系统管理员','学校管理员','审计管理员','部门管理员']

const addDepartAdminData = ref<addDepartAdminType>({
  fullName: '',
  loginName: '',
  phone: '',
  departmentId: null,
  authStatus: 0,
})

const updateDepartAdminData = ref<addDepartAdminType>({
  fullName: '',
  loginName: '',
  phone: '',
  departmentId: -1,
  authStatus: 0,
})

const fuzzyName = ref<string>("");

onBeforeMount(()=>{
  getDepartAdmin()
})

const getDepartAdmin = () => {
  useRequest(()=>getDepartAdminAPI(),{
    onSuccess(res){
      if(res.data['code']==200){
        console.log(res.data)
        departAdmins.value = res.data['data']
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}

const { data, run } = useRequest(() => fuzzySearchDepartAdminAPI(fuzzyName.value), {
  debounceWait: 1000,
  manual: true,
  onSuccess(res){
    if(fuzzyName.value != ""){
      if(res.data['code']==200){
        departAdmins.value = res.data['data']
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  }
})

watch(()=>fuzzyName, c => {
  if(fuzzyName.value == ""){
    getDepartAdmin()
  }else{
    run()
  }
},{
  deep: true
})

const addDepartAdmin = () => {
  useRequest(()=>addDepartAdminAPI(addDepartAdminData.value),{
    onSuccess(res){
      if(res.data['code']==200){
        getDepartAdmin()
        addDepartAdminData.value = {
          fullName: '',
          loginName: '',
          phone: '',
          departmentId: null,
          authStatus: 0,
        }
        addAdminDia.close()
        ElMessage({message: "添加成功", type: 'success',})
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}

const deleteDepartAdmin = (loginName:string) => {
  useRequest(()=>deleteDepartAdminAPI(loginName),{
    onSuccess(res){
      if(res.data['code']==200){
        getDepartAdmin()
        ElMessage({message: "删除成功", type: 'success',})
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}

const openUpdateDia = ( ind ) => {
  Object.assign(updateDepartAdminData.value, departAdmins.value[ind])
  updateAdminDia.showModal()
}

const updateDepartAdmin = () => {
  useRequest(()=>updateDepartAdminAPI(updateDepartAdminData.value),{
    onSuccess(res){
      if(res.data['code']==200){
        getDepartAdmin()
        updateAdminDia.close()
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