import makeAppointmentAPI from "./Server/makeAppointmentAPI"
import checkAppointmentAPI from "./Server/checkAppointmentAPI"
import getPassCodeAPI from "./Server/getPassCodeAPI"
import loginAPI from "./Server/loginAPI"
import listAllAdminAPI from "./Server/systemAdmin/listAllAdminAPI"
import addAdminAPI from "./Server/systemAdmin/addAdminAPI"
import deleteAdminAPI from "./Server/systemAdmin/deleteAdminAPI"
import resetPasswordAPI from "./Server/systemAdmin/resetPasswordAPI"
import modifyAdminAPI from "./Server/systemAdmin/modifyAdminAPI";
import searchAdminByNameAPI from "./Server/systemAdmin/searchAdminByNameAPI";
import getDepartmentAPI from "./Server/schoolAdmin/getDepartmentAPI";
import addDepartmentAPI from "./Server/schoolAdmin/addDepartmentAPI";
import deleteDepartmentAPI from "./Server/schoolAdmin/deleteDepartmentAPI";
import updateDepartmentAPI from "./Server/schoolAdmin/updateDepartmentAPI";
import fuzzySearchDepartmentAPI from "./Server/schoolAdmin/fuzzySearchDepartmentAPI";
import getDepartAdminAPI from "./Server/schoolAdmin/getDepartAdminAPI";
import fuzzySearchDepartAdminAPI from "./Server/schoolAdmin/fuzzySearchDepartAdminAPI";
import addDepartAdminAPI from "./Server/schoolAdmin/addDepartAdminAPI";
import deleteDepartAdminAPI from "./Server/schoolAdmin/deleteDepartAdminAPI";
import updateDepartAdminAPI from "./Server/schoolAdmin/updateDepartAdminAPI";

export {
    makeAppointmentAPI,
    checkAppointmentAPI,
    getPassCodeAPI,
    loginAPI,
    listAllAdminAPI,
    addAdminAPI,
    deleteAdminAPI,
    resetPasswordAPI,
    modifyAdminAPI,
    searchAdminByNameAPI,
    getDepartmentAPI,
    addDepartmentAPI,
    deleteDepartmentAPI,
    updateDepartmentAPI,
    fuzzySearchDepartmentAPI,
    getDepartAdminAPI,
    fuzzySearchDepartAdminAPI,
    addDepartAdminAPI,
    deleteDepartAdminAPI,
    updateDepartAdminAPI,
}