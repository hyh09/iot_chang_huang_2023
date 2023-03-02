package org.thingsboard.server.dao.sql.energyTime.entity.dto;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2021-12-16 15:29
 **/
@Data
@ToString
public class EneryTimeGapDto {


    private  String days;

    private long timeGap;

    public EneryTimeGapDto(String days, long timeGap) {
        this.days = days;
        this.timeGap = timeGap;
    }
}
