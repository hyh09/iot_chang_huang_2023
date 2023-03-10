package org.thingsboard.server.dao.sqlserver.mes.service;

import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.hs.entity.vo.HistoryGraphPropertyTsKvVO;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.dto.*;
import org.thingsboard.server.dao.sqlserver.mes.domain.production.vo.*;

import java.util.List;

public interface MesOrderService {

    /**
     * 查询订单列表
     *
     * @param dto
     * @param pageLink
     * @return
     */
    PageData<MesOrderListVo> findOrderList(MesOrderListDto dto, PageLink pageLink);

    /**
     * 查询订单进度列表
     *
     * @param dto
     * @param pageLink
     * @return
     */
    PageData<MesOrderProgressListVo> findOrderProgressList(MesOrderProgressListDto dto, PageLink pageLink);

    /**
     * 查询生产卡列表
     *
     * @param dto
     * @param pageLink
     * @return
     */
    PageData<MesProductionCardListVo> findProductionCardList(MesProductionCardListDto dto, PageLink pageLink);

    /**
     * 查询生产进度列表
     *
     * @param dto
     * @param pageLink
     * @return
     */
    PageData<MesProductionProgressListVo> findProductionProgressList(MesProductionProgressListDto dto, PageLink pageLink);


    /**
     * 查询生产卡列表
     *
     * @param dto
     * @param pageLink
     * @return
     */
    PageData<MesOrderCardListVo> findOrderCardList(MesOrderCardListDto dto, PageLink pageLink);

    /**
     * 工序选择
     *
     * @param cardNo
     * @return
     */
    PageData<MesProductedVo> findProductedList(String cardNo, PageLink pageLink);

    List<MesChartVo> getChart(MesChartDto dto);

    List<HistoryGraphPropertyTsKvVO> getParamChart(MesChartDto dto);
}
