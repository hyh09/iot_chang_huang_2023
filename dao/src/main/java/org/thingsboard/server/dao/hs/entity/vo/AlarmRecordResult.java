package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleLevel;
import org.thingsboard.server.dao.hs.entity.enums.AlarmSimpleStatus;

/**
 * 报警信息结果
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "报警信息结果")
public class AlarmRecordResult {
    /**
     * id
     */
    @ApiModelProperty("id")
    private String id;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Long createdTime;

    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String name;

    /**
     * 设备重命名名称
     */
    @ApiModelProperty("设备重命名名称")
    private String rename;

    /**
     * 报警标题
     */
    @ApiModelProperty("报警标题")
    private String title;

    /**
     * 报警信息
     */
    @ApiModelProperty("报警信息")
    private String info;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private AlarmSimpleStatus status;

    /**
     * 状态显示值
     */
    @ApiModelProperty("状态")
    private String statusStr;

    /**
     * 级别
     */
    @ApiModelProperty("级别")
    private AlarmSimpleLevel level;

    /**
     * 级别显示值
     */
    @ApiModelProperty("级别显示值")
    private String levelStr;

    /**
     * 是否可清除
     */
    @ApiModelProperty("是否可清除")
    private Boolean isCanBeClear;

    /**
     * 是否可确认
     */
    @ApiModelProperty("是否可确认")
    private Boolean isCanBeConfirm;

    /**
     * 工厂
     */
    @ApiModelProperty("工厂")
    private String factoryStr;

    /**
     * 车间
     */
    @ApiModelProperty("车间")
    private String workShopStr;

    /**
     * 产线
     */
    @ApiModelProperty("产线")
    private String productionLineStr;

    /**
     * 图片
     */
    @ApiModelProperty(value = "图片", notes = "仅用于app端显示")
    private String picture;

}
