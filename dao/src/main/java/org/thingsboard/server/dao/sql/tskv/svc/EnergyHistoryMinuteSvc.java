package org.thingsboard.server.dao.sql.tskv.svc;

import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.QueryTsKvHisttoryVo;
import org.thingsboard.server.dao.sql.tskv.entity.EnergyHistoryMinuteEntity;

import java.util.Map;

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
    void  saveByMinute(EntityId entityId, TsKvEntry tsKvEntry, String  title);


    /**
     * 分页查询
     */
    PageData<EnergyHistoryMinuteEntity> queryByDeviceIdAndTs(QueryTsKvHisttoryVo vo, PageLink pageLink);


    PageData<Map> queryTranslateTitle(QueryTsKvHisttoryVo vo, String deviceName,PageLink pageLink);


}
