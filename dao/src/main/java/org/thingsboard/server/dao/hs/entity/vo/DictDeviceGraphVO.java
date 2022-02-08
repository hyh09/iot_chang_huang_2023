package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@ApiModel(value = "设备字典图表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceGraphVO {

    @ApiModelProperty(value = "图表id", notes = "新增为null")
    private UUID id;

    @NotBlank(message = "图表名称不能为空")
    @ApiModelProperty(value = "图表名称")
    private String name;

    @NotNull(message = "是否显示图表不能为空")
    @ApiModelProperty(value = "是否显示图表")
    private Boolean enable;

    @Valid
    @NotNull(message = "属性列表不能为null")
    @ApiModelProperty(value = "属性列表")
    private List<DictDeviceGraphPropertyVO> properties;

    @ApiModelProperty(value = "创建时间")
    private Long createdTime;
}
