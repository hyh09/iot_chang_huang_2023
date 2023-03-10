package org.thingsboard.server.dao.sql.tskv.svc;

import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryTsKvHisttoryVo;
import org.thingsboard.server.dao.kafka.vo.DataBodayVo;
import org.thingsboard.server.dao.sql.tskv.entity.EnergyHistoryMinuteEntity;

import java.util.Map;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 提供外部模块的 能耗历史查询（分钟维度)
 * @author: HU.YUNHUI
 * @create: 2022-01-18 16:14
 **/
public interface EnergyHistoryMinuteSvc {

    /**
     * 保存数据
     */
    void  saveByMinute(UUID entityId, DataBodayVo tsKvEntry, String  title);


    /**
     * 分页查询
     */
    PageData<EnergyHistoryMinuteEntity> queryByDeviceIdAndTs(QueryTsKvHisttoryVo vo, PageLink pageLink);


    /**
     * 能耗
     * @param vo
     * @param deviceName
     * @param pageLink
     * @return
     */
    PageData<Map> queryTranslateTitle(QueryTsKvHisttoryVo vo, String deviceName,PageLink pageLink);


}
