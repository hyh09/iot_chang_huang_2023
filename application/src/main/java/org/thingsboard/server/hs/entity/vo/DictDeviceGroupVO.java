package org.thingsboard.server.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "设备字典分组实体类")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceGroupVO {

    @ApiModelProperty(value = "id", notes = "新增或修改时均为null")
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @Valid
    @ApiModelProperty(value = "分组属性列表")
    private List<DictDeviceGroupPropertyVO> groupPropertyList;
}
