package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.thingsboard.server.dao.hs.entity.enums.DictDevicePropertyTypeEnum;

@Data
@Accessors(chain = true)
@ApiModel(value = "设备字典遥测属性实体类")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceTsPropertyResult {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "类型")
    private String type;
}
