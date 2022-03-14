package org.thingsboard.server.common.data.vo.bodrd;

import lombok.Data;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2022-03-08 09:11
 **/
@Data
public class DashboardNameAndValueVo {

    private  String name;

    private  DashboardV3Vo  value;
}
