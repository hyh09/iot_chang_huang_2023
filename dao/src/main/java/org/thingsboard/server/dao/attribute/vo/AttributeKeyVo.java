package org.thingsboard.server.dao.attribute.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

/**
 * @Project Name: thingsboard
 * @File Name: AttributeKeyVo
 * @Date: 2022/12/21 11:21
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@ToString
public class AttributeKeyVo implements Serializable {

    /**
     * 属性id
     */
    private UUID propertyId;

    /**
     * 属性类型
     */
    private String propertyType;

    /**
     * 遥测keyname
     */
    private String  keyName;


    /**
     * 默认只查询不显示； 用于其他接口过滤数据使用
     */
    private Integer switchValue;

}
