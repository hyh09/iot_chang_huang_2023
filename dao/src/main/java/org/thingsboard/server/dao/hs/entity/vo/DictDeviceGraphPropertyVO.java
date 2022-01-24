package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.thingsboard.server.dao.hs.entity.enums.DictDevicePropertyTypeEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@ApiModel(value = "设备字典图表属性")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceGraphPropertyVO {

    @NotNull(message = "属性id不能为空")
    @ApiModelProperty(value = "属性id")
    private UUID id;

    @ApiModelProperty(value = "属性名称", notes = "仅显示")
    private String name;

    @ApiModelProperty(value = "属性标题", notes = "仅显示")
    private String title;

    @NotNull(message = "属性类型不能为空")
    @ApiModelProperty(value = "属性类型")
    private DictDevicePropertyTypeEnum propertyType;
}
