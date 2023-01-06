import { BaseData } from "@app/shared/public-api";
// 生产排班数据结构
export interface ProdSchedual extends BaseData<any> {
    nplanOutputQty: string,
    ntrackQty: string,
    scardNo: string,
    sorderNo: string,
    sworkerGroupName: string,
    sworkerNameList: string,
    sworkingProcedureName: string,
    timeout: string,
    tplanEndTime: string,
    ttrackTime: string
}

// 生产报工数据结构
export interface ProdReport extends BaseData<any>  {
    fnMESGetDiffTimeStr: string,
    ntrackQty: string,
    scardNo: string,
    scolorNo: string,
    sequipmentName: string,
    sorderNo: string,
    sworkerGroupName: string,
    tfactEndTime: string,
    tfactStartTime: string,
}

// 生产监控数据结构
export interface ProdMonitor extends BaseData<any>  {
    ddeliveryDate: string,
    fnMESGetDiffTimeStr: string,
    nplanOutputQty: string,
    scardNo: string,
    scolorName: string,
    scustomerName: string,
    smaterialName: string,
    sorderNo: string,
    sworkingProcedureName: string,
    sworkingProcedureNameFinish: string
}