package org.thingsboard.server.common.data.vo.home;

import lombok.Data;
import lombok.ToString;

/**
 * @program: springboot-jpa-20210106
 * @description: 每个的的第一天0点和最后一天的时间
 * @author: HU.YUNHUI
 * @create: 2021-11-12 14:27
 **/

@Data
@ToString
public class EachMonthStartEndVo {


    private int flg;

    /**
     *当前月份
     */
    private int month;

    /**
     * 当前月的起始时刻
     */
    private  long startTime;

    //只是用来日志看的
    private String strStartTime;

    /**
     * 当前月的结束时刻
     */
    private  long endTime;

    //转换后的日期，用来看日志的
    private String strEndTime;

}
