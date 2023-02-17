package org.thingsboard.server.dao.board.factoryBoard.impl;

import java.text.NumberFormat;

/**
 * @Project Name: thingsboard
 * @File Name: FileldNmaePostfixMehodUtil
 * @Date: 2023/2/17 13:04
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
public class FiledNamePostfixMethodUtil {

    public String formatPercentage(String value) {
        NumberFormat nf = NumberFormat.getPercentInstance();
        return nf.format(value);

    }


}
