<template>
<div class="w-screen pt-[20px] flex flex-col items-center gap-4">
  <div class="w-[1200px]">
    <label class="input input-bordered flex items-center gap-2 w-[250px] float-right">
      <input v-model="fuzzyName" type="text" class="grow" placeholder="通过姓名模糊搜索" />
      <el-icon><Search /></el-icon>
    </label>
  </div>
  <div class="overflow-x-auto h-[80vh] w-[1200px]">
    <table class="table text-base">
      <thead>
      <tr class="text-base text-center bg-base-300">
        <th>ID</th>
        <th>姓名</th>
        <th>用户名</th>
        <th>手机号</th>
        <th>管理员角色</th>
        <th>上一次密码修改时间</th>
        <th>部门ID</th>
        <th>授权状态</th>
        <th>是否锁定</th>
        <th><button onclick="addAdminDia.showModal()" class="btn btn-outline btn-sm hover:bg-base-200 hover:text-black">添加管理员</button></th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(value,index) in admins" class="hover text-center">
        <td>{{ value.id }}</td>
        <td>{{ value.fullName }}</td>
        <td>{{ value.loginName }}</td>
        <td>{{ value.phone }}</td>
        <td>{{ ADMIN_ROLE[value.adminRole] }}</td>
        <td>{{ value.lastPasswordUpdate.split('.')[0] }}</td>
        <td>{{ value.departmentId ? value.departmentId : 'null' }}</td>
        <td>{{ value.authStatus ? '已授权' : '未授权' }}</td>
        <td>{{ value.isLock ? '锁定' : '未锁定' }}</td>
        <td>
          <div class="dropdown dropdown-bottom dropdown-end" id="dropdown">
            <div tabindex="0" role="button" class="btn m-1 btn-ghost"><el-icon size="20"><MoreFilled /></el-icon></div>
            <ul tabindex="0" id="dropdown-content" class="dropdown-content menu open bg-base-100 rounded-box z-[1] w-32 p-2 shadow text-base" >
              <li><a @click="deleteAdmin(value.loginName)">删除</a></li>
              <li><a @click="openModifyDia(index)">修改信息</a></li>
              <li><a @click="resetPassword(value.loginName)">重置密码</a></li>
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
          <input v-model="addAdminData.fullName" type="text" class="grow w-[20px]" placeholder="请输入姓名" />
        </label>
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          用户名：
          <input v-model="addAdminData.loginName" type="text" class="grow" placeholder="请输入用户名" />
        </label>
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          手机号：
          <input v-model="addAdminData.phone" type="text" class="grow" placeholder="请输入手机号" />
        </label>
        <div>
          <select v-model="addAdminData.adminRole" class="select select-bordered text-base">
            <option disabled selected :value="-1">请选择管理员角色</option>
            <option :value="0">系统管理员</option>
            <option :value="1">学校管理员</option>
            <option :value="2">审计管理员</option>
            <option :value="3">部门管理员</option>
          </select>
        </div>
        <div v-show="addAdminData['adminRole']===3" class="flex flex-col gap-4">
          <label class="input input-bordered flex items-center gap-2 w-[300px]">
            部门ID：
            <input v-model="addAdminData.departmentId" type="text" class="grow w-[20px]" placeholder="请输入部门ID" />
          </label>
          <div class="flex gap-4">
            <input @click="()=>{addAdminData.authStatus = 0}" type="radio" name="addAdminData" class="radio" :checked="addAdminData.authStatus === 0" />未授权
            <input @click="()=>{addAdminData.authStatus = 1}" type="radio" name="addAdminData" class="radio" :checked="addAdminData.authStatus === 1" />授权
          </div>
        </div>
        <div class="flex justify-center">
          <button @click="addAdmin" class="btn btn-active px-16">添加</button>
        </div>
      </div>
    </div>
  </dialog>
<!--  修改信息对话框 -->
  <dialog id="modifyAdminDia" class="modal">
    <div class="modal-box w-[350px]">
      <form method="dialog">
        <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
      </form>
      <h3 class="text-lg font-bold">修改管理员信息</h3>
      <div class="flex flex-col gap-4 mt-4">
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          用户名：
          <input v-model="modifyAdminData.loginName" type="text" class="grow" placeholder="请输入用户名" disabled />
        </label>
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          姓名：
          <input v-model="modifyAdminData.fullName" type="text" class="grow w-[20px]" placeholder="请输入姓名" />
        </label>
        <label class="input input-bordered flex items-center gap-2 w-[300px]">
          手机号：
          <input v-model="modifyAdminData.phone" type="text" class="grow" placeholder="请输入手机号" />
        </label>
        <div>
          <select v-model="modifyAdminData.adminRole" class="select select-bordered text-base">
            <option disabled selected :value="-1">请选择管理员角色</option>
            <option :value="0">系统管理员</option>
            <option :value="1">学校管理员</option>
            <option :value="2">审计管理员</option>
            <option :value="3">部门管理员</option>
          </select>
        </div>
        <div v-show="modifyAdminData['adminRole']===3" class="flex flex-col gap-4">
          <label class="input input-bordered flex items-center gap-2 w-[300px]">
            部门ID：
            <input v-model="modifyAdminData.departmentId" type="text" class="grow w-[20px]" placeholder="请输入部门ID" />
          </label>
          <div class="flex gap-4">
            <input @click="()=>{ modifyAdminData.authStatus = 0}" type="radio" name="modifyAdminData" class="radio" :checked="modifyAdminData.authStatus === 0" />未授权
            <input @click="()=>{ modifyAdminData.authStatus = 1}" type="radio" name="modifyAdminData" class="radio" :checked="modifyAdminData.authStatus === 1" />授权
          </div>
        </div>
        <div class="flex justify-center">
          <button @click="modifyAdmin" class="btn btn-active px-16">修改</button>
        </div>
      </div>
    </div>
  </dialog>
</div>
</template>

<script lang="ts" setup>
import {onBeforeMount, ref, watch} from "vue";
import { useRequest } from "vue-hooks-plus";
import { listAllAdminAPI, addAdminAPI, deleteAdminAPI, resetPasswordAPI, modifyAdminAPI, searchAdminByNameAPI } from "../../apis"
import { ElMessage } from "element-plus";

const admins = ref<Array<adminType>>();

const ADMIN_ROLE = ['系统管理员','学校管理员','审计管理员','部门管理员']

const addAdminData = ref<addAdminType>({
  adminRole: -1,
  fullName: '',
  loginName: '',
  phone: '',
  departmentId: null,
  authStatus: 0,
})

const modifyAdminData = ref<modifyAdminType>({
  adminRole: -1,
  fullName: '',
  loginName: '',
  phone: '',
  departmentId: null,
  authStatus: 0,
})

const fuzzyName = ref<string>("");

onBeforeMount(()=>{
  getAllAdmin()
})

const getAllAdmin = () => {
  useRequest(()=>listAllAdminAPI(),{
    onSuccess(res){
      if(res.data['code']==200){
        admins.value = res.data['data']
        console.log(admins.value)
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}

// 通过防抖机制来实现自动请求
const { data, run } = useRequest(() => searchAdminByNameAPI(fuzzyName.value), {
  debounceWait: 1000,
  manual: true,
  onSuccess(res){
    if(fuzzyName.value != ""){
      if(res.data['code']==200){
        admins.value = res.data['data']
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  }
})

watch(()=>fuzzyName, c => {
  if(fuzzyName.value == ""){
    getAllAdmin()
  }else{
    run()
  }
},{
  deep: true
})

const addAdmin = () => {
  useRequest(()=>addAdminAPI(addAdminData.value),{
    onSuccess(res){
      if(res.data['code']==200){
        addAdminDia.close()
        addAdminData.value = {
          adminRole: -1,
          fullName: '',
          loginName: '',
          phone: '',
          departmentId: null,
          authStatus: 0,
        }
        getAllAdmin()
        ElMessage({message: "添加成功", type: 'success',})
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}

const deleteAdmin = (loginName:string) => {
  useRequest(()=>deleteAdminAPI(loginName),{
    onSuccess(res){
      if(res.data['code']==200){
        getAllAdmin()
        ElMessage({message: "删除成功", type: 'success',})
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}

const resetPassword = (loginName:string) => {
  useRequest(()=>resetPasswordAPI(loginName),{
    onSuccess(res){
      if(res.data['code']==200){
        getAllAdmin()
        ElMessage({message: "密码重置成功", type: 'success',})
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}

const openModifyDia = (ind) => {
  Object.assign(modifyAdminData.value,admins.value[ind])
  modifyAdminDia.showModal()
}

const modifyAdmin = () => {
  if(modifyAdminData.value.adminRole !== 3){
    modifyAdminData.value.departmentId = null
    modifyAdminData.value.authStatus = 0
  }
  useRequest(()=>modifyAdminAPI(modifyAdminData.value),{
    onSuccess(res){
      if(res.data['code']==200){
        getAllAdmin()
        modifyAdminDia.close()
        ElMessage({message: "信息修改成功", type: 'success',})
      }else{
        ElMessage({message: res.data['msg'], type: 'warning',})
      }
    }
  })
}
</script>

<style scoped>

</style>