package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 文件信息
 *
 * @author wwj
 * @since 2021.10.18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "文件")
public class FileInfoDictDeviceModelVO {

    @ApiModelProperty(value = "租户Id")
    private String tenantId;

    @ApiModelProperty(value = "文件Id")
    private String id;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "校验和")
    private String checkSum;

    @ApiModelProperty(value = "文件内容类型")
    private String contentType;

    @ApiModelProperty(value = "算法")
    private String checksumAlgorithm;

    @ApiModelProperty(value = "内容大小")
    private Long dataSize;

    @ApiModelProperty(value = "附加信息")
    private String additionalInfo;

    @ApiModelProperty(value = "范围")
    private String scope;

    @ApiModelProperty(value = "实体Id")
    private String entityId;

    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty(value = "图片")
    private String picture;
}
