
interface departmentType {
    id: number,
    departmentType: number,
    departmentName: string
}

interface addDepartmentType {
    departmentType: number,
    departmentName: string
}

interface addDepartAdminType {
    fullName: string,
    loginName: string,
    phone: string,
    departmentId: number | null,
    authStatus: number
}