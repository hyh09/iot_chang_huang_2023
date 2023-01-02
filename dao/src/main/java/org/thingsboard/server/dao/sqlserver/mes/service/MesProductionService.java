package org.thingsboard.server.dao.sqlserver.mes.service;

import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesProductionMonitorDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesProductionPlanDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.MesProductionWorkDto;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesProductionMonitorVo;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesProductionPlanVo;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.MesProductionWorkVo;

import java.util.List;

public interface MesProductionService {

    /**
     * 查询生产班组列表
     * @param dto
     * @param pageLink
     * @return
     */
    PageData<MesProductionPlanVo> findPlanList(MesProductionPlanDto dto, PageLink pageLink);

    /**
     * 查询生产报工列表
     * @param dto
     * @param pageLink
     * @return
     */
    PageData<MesProductionWorkVo> findWorkList(MesProductionWorkDto dto, PageLink pageLink);

    /**
     * 查询生产监控列表
     * @param dto
     * @param pageLink
     * @return
     */
    PageData<MesProductionMonitorVo> findMonitorList(MesProductionMonitorDto dto, PageLink pageLink);

    /**
     * 查询工序名称下拉
     * @return
     */
    List<String> findWorkingProcedureNameList();
}
