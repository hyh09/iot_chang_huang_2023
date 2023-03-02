package org.thingsboard.server.dao.board.factoryBoard.impl;

import org.thingsboard.server.common.data.StringUtils;

import java.math.BigDecimal;

/**
 * @Project Name: thingsboard
 * @File Name: FileldNmaePostfixMehodUtil
 * @Date: 2023/2/17 13:04
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
public class FiledNamePostfixMethodUtil {

    private final  String SUFFIX_IDENTIFIER_PERCENT="";

    /**
     * 将字符串转 百分比字符
     * 如果是 0.00 返回都是 0% ##等测试提出再改
     *
     * @param value
     * @return
     */
    public String formatPercentage(String value) {
        if (StringUtils.isEmpty(value)) {
            return BigDecimal.ZERO.toPlainString() +SUFFIX_IDENTIFIER_PERCENT;
        }
        return value + SUFFIX_IDENTIFIER_PERCENT;
    }

}
