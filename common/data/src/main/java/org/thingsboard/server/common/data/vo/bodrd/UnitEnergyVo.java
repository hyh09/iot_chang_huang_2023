package org.thingsboard.server.common.data.vo.bodrd;

import lombok.Data;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 单位能耗转换对象
 * @author: HU.YUNHUI
 * @create: 2022-04-15 13:34
 **/
@Data
public class UnitEnergyVo {

    private  String  waterUnit;

    private  String  gasUnit;

    private  String electricUnit;


    private UUID  deviceId;
}
