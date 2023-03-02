package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.compress.utils.Lists;
import org.thingsboard.server.common.data.factory.Factory;

import java.util.List;
import java.util.UUID;

/**
 * 工厂返回结果
 *
 * @author wwj
 * @since 2021.12.3
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "工厂返回结果")
public class SimpleFactoryResult {
    /**
     * 工厂Id
     */
    @ApiModelProperty("工厂Id")
    private UUID id;

    /**
     * 工厂名称
     */
    @ApiModelProperty("工厂名称")
    private String name;

    /**
     * 车间列表
     */
    @ApiModelProperty("车间列表")
    private List<SimpleWorkshopResult> workshops;

    public SimpleFactoryResult(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.workshops = Lists.newArrayList();
    }

    public SimpleFactoryResult(Factory factory) {
        this.id = factory.getId();
        this.name = factory.getName();
        this.workshops = Lists.newArrayList();
    }
}
