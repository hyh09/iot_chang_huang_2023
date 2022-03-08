package org.thingsboard.server.dao.board;

import lombok.Data;

/**
 * @program: thingsboard
 * @description: 计算的
 * @author: HU.YUNHUI
 * @create: 2022-03-08 14:51
 **/
@Data
public class TotalCalculationVo {


    private  String water;

    private  String gas;

    private  String electric;
}
