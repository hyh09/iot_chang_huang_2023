package org.thingsboard.server.dao.hsms.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.thingsboard.server.dao.hs.entity.enums.EnumGetter;

/**
 * 设备字典属性开关类型
 *
 * @author wwj
 * @since 2021.10.18
 */
@Getter
public enum DictDevicePropertySwitchEnum {
    SHOW(1, "显示"),
    HIDE(0, "隐藏");

    @JsonValue
    private final Integer code;
    private final String name;

    DictDevicePropertySwitchEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DictDevicePropertySwitchEnum valueOfCode(Integer code) {
        if (code == 0)
            return HIDE;
        return SHOW;
    }
}
