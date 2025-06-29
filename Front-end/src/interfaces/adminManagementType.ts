
interface adminType {
    id: number,
    adminRole: number,
    fullName: string,
    loginName: string,
    loginPassword: string,
    lastPasswordUpdate: string,
    departmentId: number | null,
    phone: string,
    authStatus: number,
    loginFailCount: number,
    lastLoginFailTime: string,
    isLock: number,
}

interface addAdminType {
    adminRole: number,
    fullName: string,
    loginName: string,
    phone: string
    departmentId: number | null,
    authStatus: number,
}

interface modifyAdminType {
    loginName: string,
    fullName: string,
    phone: string,
    adminRole: number,
    departmentId?: number | null,
    authStatus?: number,
}