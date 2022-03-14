package org.thingsboard.server.dao.kafka.vo;

import lombok.Data;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.TsKvEntry;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 遥测数据的消息体
 * @author: HU.YUNHUI
 * @create: 2022-03-10 17:47
 **/
@Data
public class DataBodayVo {

    private  UUID tenantId;

    private  UUID  entityId;

    private  long  ts;

    private  String  title;

    private  String key;
    private  String value;

    public DataBodayVo() {
    }

    public static  DataBodayVo  toDataBodayVo(TenantId tenantId,EntityId entityId , TsKvEntry tsKvEntry , String  title) {
        DataBodayVo  dataBodayV=   new DataBodayVo(entityId.getId(),tsKvEntry.getTs(),title,tsKvEntry.getKey(),tsKvEntry.getValue().toString());
        dataBodayV.setTenantId(tenantId.getId());
        return  dataBodayV;
    }

    public DataBodayVo(UUID entityId) {
        this.entityId = entityId;
    }

    public DataBodayVo(UUID entityId, long ts, String title, String key, String value) {
        this.entityId = entityId;
        this.ts = ts;
        this.title = title;
        this.key = key;
        this.value = value;
    }

//    private EntityId entityId;
//
//    private TsKvEntry tsKvEntry;
//
//    private  String  title;
//
//    public DataBodayVo() {
//    }
//
//    public DataBodayVo(EntityId entityId, TsKvEntry tsKvEntry, String title) {
//        this.entityId = entityId;
//        this.tsKvEntry = tsKvEntry;
//        this.title = title;
//    }


}
