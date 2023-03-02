package org.thingsboard.server.dao.hs.entity.enums;

import lombok.Getter;

/**
 * Excel数据类型枚举
 *
 * @author wwj
 * @since 2021.11.10
 */
@Getter
public enum ExcelDataTypeEnum {
    STRING, NUMBER, DATE_TIME, CURRENCY;
}
