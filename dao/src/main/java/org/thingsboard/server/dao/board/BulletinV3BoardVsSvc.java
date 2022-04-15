package org.thingsboard.server.dao.board;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vo.BoardV3DeviceDictionaryVo;
import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.common.data.vo.bodrd.DashboardV3Vo;
import org.thingsboard.server.common.data.vo.tskv.TrendChart02Vo;
import org.thingsboard.server.common.data.vo.tskv.parameter.TrendParameterVo;
import org.thingsboard.server.dao.sql.role.entity.BoardV3DeviceDitEntity;

import java.util.List;

/**
 * @program: thingsboard
 * @description: 看板3期需求
 * @author: HU.YUNHUI
 * @create: 2022-03-07 11:18
 **/
public interface BulletinV3BoardVsSvc {

    /**
     *
      * @param tsSqlDayVo
     * @return
     */
   List<BoardV3DeviceDitEntity> queryDeviceDictionaryByEntityVo(TsSqlDayVo tsSqlDayVo);

    /**
     * 看板的仪表盘
     * @param vo
     * @return
     */
   List<DashboardV3Vo>  queryDashboardValue(BoardV3DeviceDictionaryVo vo,TenantId tenantId);


    TrendChart02Vo trendChart(TrendParameterVo vo, TenantId tenantId );
}
