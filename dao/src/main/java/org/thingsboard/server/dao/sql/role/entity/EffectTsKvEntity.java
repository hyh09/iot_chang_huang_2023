package org.thingsboard.server.dao.sql.role.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.checkerframework.checker.units.qual.C;
import org.thingsboard.server.dao.model.sql.AbstractTsKvEntity;
import org.thingsboard.server.dao.model.sqlts.timescale.ts.TimescaleTsKvCompositeKey;
import org.thingsboard.server.dao.util.StringUtilToll;

import javax.persistence.*;
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

                                        @ColumnResult(name = "deviceName",type = String.class),
                                        @ColumnResult(name = "factoryId",type = UUID.class),
                                        @ColumnResult(name = "workshopId",type = UUID.class),
                                        @ColumnResult(name = "productionLineId",type = UUID.class),

                                        @ColumnResult(name = "booleanValue",type=Boolean.class),
                                        @ColumnResult(name = "strValue",type = String.class),
                                        @ColumnResult(name = "longValue",type = Long.class),
                                        @ColumnResult(name = "doubleValue",type = Double.class),
                                        @ColumnResult(name = "jsonValue",type =String.class ),

                                        @ColumnResult(name = "ts2",type=Long.class),
                                        @ColumnResult(name = "bollV2",type = Boolean.class),
                                        @ColumnResult(name = "strV2",type = String.class),
                                        @ColumnResult(name = "longV2",type = Long.class),
                                        @ColumnResult(name = "doubleValue2",type = Double.class),
                                        @ColumnResult(name = "jsonValue2",type =String.class )

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
    private String keyName;

    @Transient
  private  String  deviceName;
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



//    protected Boolean booleanValue;

    /**
     * 如果 ts2 和 ts的值一样 ，就视为 一条数据
     */
    @Transient
    private Long ts2;

    @Transient  //bool_v
    private Boolean bollV2;

    @Transient //str_v
    protected String strV2;

    @Transient
    protected Long longV2;

    @Transient
    protected Double doubleValue2;

    @Transient
    protected String jsonValue2;


    /**
     * 用于计算总产能的数据
     *   long类型的产能差
     */
    @Transient private Long subtractLong=0L;

    /**
     * 用于计算总产能的数据
     *   Doubble类型的产能差
     */
    @Transient private Double subtractDouble=0.00;

//    @Transient private Double subtractStr=0.00;

//    /**
//     * 用于计算总产能的数据
//     *   String类型的产能差
//     */
//    @Transient private Object subtract;


    public EffectTsKvEntity() {
    }


    public EffectTsKvEntity(String onlyKeyId,UUID entityId,Long ts1,Integer key1,
                            String deviceName,UUID factoryId,UUID workshopId,UUID productionLineId,
                            Boolean booleanValue1,String strValue,Long longValue,Double doubleValue,String jsonValue,
                            Long ts2,Boolean bollV2,String strV2,Long longV2,Double doubleValue2,String jsonValue2
    ) {
        this.onlyKeyId = onlyKeyId;
        this.entityId = entityId;
        this.ts =ts1;
        this.key =key1;

        this.deviceName =deviceName;
        this.factoryId =factoryId;
        this.workshopId=workshopId;
        this.productionLineId=productionLineId;

        this.booleanValue=booleanValue1;
        this.strValue=strValue;
        this.longValue =longValue;
        this.doubleValue=doubleValue;
        this.jsonValue =jsonValue;


        this.ts2 =ts2;
        this.bollV2=bollV2;
        this.strV2 =strV2;
        this.longV2=longV2;
        this.doubleValue2=doubleValue2;
        this.jsonValue2 =jsonValue2;


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

        //如果时间不相同的时候 代表有最大值 和最小值
        if(ts!=ts2) {
            if (longV2 != null) {
                subtractLong = ((longV2 - longValue)>0?(longV2 - longValue):0);
            }
            if(doubleValue2 != null)
            {

                double sub=   StringUtilToll.sub(doubleValue2,doubleValue);
                subtractDouble= ((sub)>0?(sub):0.00);
            }
            if(StringUtilToll.isNumber(strV2) && StringUtilToll.isNumber(strValue))
            {
                Double d1=  Double.valueOf(strV2);
                Double d2= Double.valueOf(strValue);
                double sub=   StringUtilToll.sub(d1,d2);
                subtractDouble=( (sub)>0?(sub):0.00);
            }
        }
        //代表最大 最小就是自己
//        else {
//
//            if (longV2 != null) {
//                subtractLong = ((longV2>0)?longV2:0) ;
//            }
//            if(doubleValue2 != null)
//            {
//                subtractDouble= (doubleValue2 >0?doubleValue2:0.00);
//            }
//            if(StringUtilToll.isNumber(strV2))
//            {
//                Double d1=  Double.valueOf(strV2);
//                subtractDouble= (d1>0)?d1:0.00;
//            }
//
//        }

    }



    /**
     * 返回当前的设备的这个属性的值
     */
    public  String getValue()
    {
      System.out.println("打印当前的key:"+subtractDouble+"===#:"+subtractLong);
        if(this.subtractDouble>0)
        {
            return  subtractDouble.toString();
        }
        if(this.subtractLong>0)
        {
            return subtractLong.toString();
        }
        return  "0";
    }
}
