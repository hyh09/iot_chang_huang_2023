package org.thingsboard.server.common.data.devicecomponent;

import lombok.Data;

import java.util.UUID;

@Data
public class DeviceComponent {

    /**
     * 部件Id
     */
    private UUID id;
    /**
     * 设备Id
     */
    private UUID deviceId;

    /**
     * 父部件Id
     */
    private UUID parentId;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 供应商
     */
    private String supplier;

    /**
     * 型号
     */
    private String model;

    /**
     * 版本号
     */
    private String version;

    /**
     * 保修期
     */
    private String warrantyPeriod;

    /**
     * 备注
     */
    private String comment;

    /**
     * 图标
     */
    private String icon;

    /**
     * 图片
     */
    private String picture;

    private long createdTime;

    private UUID createdUser;

    private long updatedTime;

    private UUID updatedUser;

    public DeviceComponent(){}
}
