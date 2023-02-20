package org.thingsboard.server.dao.kanban.vo.inside;

import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGraphAndPropertyVO;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGraphPropertyVO;

/**
 * @Project Name: thingsboard
 * @File Name: AttributesPropertiesGraphUnderVo
 * @Date: 2023/2/20 15:13
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
@Data
@ToString
public class AttributesPropertiesGraphUnderVo extends DictDeviceGraphPropertyVO {

    private String value;

    public AttributesPropertiesGraphUnderVo(DictDeviceGraphAndPropertyVO partVo,String value) {
        super(partVo.getId(), partVo.getName(), partVo.getTitle(), partVo.getUnit(), partVo.getSuffix(), partVo.getPropertyType());
        this.value = value;
    }

    public AttributesPropertiesGraphUnderVo(String value) {
        this.value = value;
    }
}
