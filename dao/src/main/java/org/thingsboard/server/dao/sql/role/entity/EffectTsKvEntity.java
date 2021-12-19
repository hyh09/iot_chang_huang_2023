package org.thingsboard.server.dao.sql.role.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.checkerframework.checker.units.qual.C;
import org.thingsboard.server.dao.model.sql.AbstractTsKvEntity;
import org.thingsboard.server.dao.model.sqlts.timescale.ts.TimescaleTsKvCompositeKey;
import org.thingsboard.server.dao.util.StringUtilToll;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 效能分析之产能分析的统计接口
 * @author: HU.YUNHUI
 * @create: 2021-11-09 09:18

 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ts_kv")
@IdClass(TimescaleTsKvCompositeKey.class)
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "result001",
                classes = {
                        @ConstructorResult(
                                targetClass = EffectTsKvEntity.class,
                                columns = {
                                        @ColumnResult(name = "onlyKeyId", type = String.class),
                                        @ColumnResult(name = "entity_id", type = UUID.class),
                                        @ColumnResult(name = "ts" ,type = Long.class),
                                        @ColumnResult(name = "key" ,type = Integer.class),

                                        @ColumnResult(name = "keyName",type = String.class),


                                        @ColumnResult(name = "deviceName",type = String.class),
                                        @ColumnResult(name = "picture",type = String.class),
                                        @ColumnResult(name = "flg",type = Boolean.class),
                                        @ColumnResult(name = "factoryId",type = UUID.class),
                                        @ColumnResult(name = "workshopId",type = UUID.class),
                                        @ColumnResult(name = "productionLineId",type = UUID.class),
                                       @ColumnResult(name = "ts2",type=Long.class),
                                       @ColumnResult(name = "valueLast",type = String.class),
                                        @ColumnResult(name = "valueLast2",type = String.class),

                                        @ColumnResult(name = "localValue",type = String.class)

                                }
                        ),
                }

        )

})
@ToString
public class EffectTsKvEntity extends AbstractTsKvEntity {

    /**
     * entityId + key 组合的id
     */
    @Transient
    private String onlyKeyId;

    /**
     * 前端传入的keyName
     */
    @Transient
    private String keyName="";

    @Transient
  private  String  deviceName;


    @Transient
    private  String  picture;
    @Transient
    private  Boolean  flg=false;

    /**
     * 工厂
     */
    @Transient
    private UUID factoryId;
    /**
     * 车间
     */
    @Transient
    private UUID workshopId;
    /**
     * 生产线
     */
    @Transient
    private UUID productionLineId;



    /**
     * 如果 ts2 和 ts的值一样 ，就视为 一条数据
     */
    @Transient
    private Long ts2;


    @Transient //最后的一个值
    private  String valueLast;

    @Transient //最后的一个值
    private  String valueLast2="0";

    @Transient
    private  String localValue;

    public EffectTsKvEntity() {
    }




    public  EffectTsKvEntity(String  onlyKeyId,UUID entityId,Long ts,Integer key,String keyName,String deviceName,String picture,Boolean  flg,
                             UUID factoryId, UUID workshopId, UUID productionLineId, Long ts2, String valueLast, String valueLast2,String  localValue){

        this.onlyKeyId = onlyKeyId;
        this.entityId =entityId;
        this.ts = ts;
        this.key =(key==null?-1:key);
        this.keyName = keyName;



        this.deviceName =deviceName;
        this.picture = picture;
       this.flg =flg;

        this.factoryId = factoryId;
        this.workshopId = workshopId;
        this.productionLineId = productionLineId;
        this.ts2 = ts2;
        this.valueLast = valueLast;
        this.valueLast2 = valueLast2;

        this.localValue = localValue;
    }





    @Override
    public boolean isNotEmpty() {
        return strValue != null || longValue != null || doubleValue != null || booleanValue != null || jsonValue != null;
    }


    /**
     * 计算差值
     * @return
     */
    public  void  subtraction()
    {

//        localValue = valueLast2;
        //如果时间不相同的时候 代表有最大值 和最小值
        if(ts == null || ts2 == null)
        {
            //时间为空说明是没有遥测数据的
            valueLast2 ="0";
        }else {

                if (ts.equals(ts2)) {
                    valueLast2 = "0";
                } else {
                    valueLast2 = StringUtilToll.sub(valueLast2, valueLast);
                }
        }


    }



//    /**
//     * 返回当前的设备的这个属性的值
//     */
//    public  String getValue()
//    {
//      System.out.println("打印当前的key:"+subtractDouble+"===#:"+subtractLong);
//        if(this.subtractDouble>0)
//        {
//            return  subtractDouble.toString();
//        }
//        if(this.subtractLong>0)
//        {
//            return subtractLong.toString();
//        }
//        return  "0";
//    }



}
