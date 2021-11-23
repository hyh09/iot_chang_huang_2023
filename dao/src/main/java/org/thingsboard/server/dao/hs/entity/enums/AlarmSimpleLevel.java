package org.thingsboard.server.dao.hs.entity.enums;

import lombok.Getter;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;

import java.util.*;

/**
 * 报警级别枚举
 *
 * @author wwj
 * @since 2021.10.26
 */
@Getter
public enum AlarmSimpleLevel implements EnumGetter {
    ANY("ANY", "alarm-level-any"),
    CRITICAL("CRITICAL", "alarm-level-critical"),
    MAJOR("MAJOR", "alarm-level-major"),
    MINOR("MINOR", "alarm-level-minor"),
    WARNING("WARNING", "alarm-level-warning"),
    INDETERMINATE("INDETERMINATE", "alarm-level-indeterminate");

    private final String code;
    private final String name;

    AlarmSimpleLevel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public AlarmSeverity toAlarmSeverity() {
        if (this.equals(ANY))
            return null;
        return AlarmSeverity.valueOf(this.code);
    }

    public Set<AlarmSeverity> toAlarmSeveritySet() {
        if (this.equals(ANY))
            return Set.of(AlarmSeverity.values());
        return Set.of(AlarmSeverity.valueOf(this.code));
    }

    public static AlarmSimpleLevel toAlarmSimpleStatus(AlarmSeverity alarmSeverity) {
        switch (alarmSeverity) {
            case MAJOR:
                return MAJOR;
            case MINOR:
                return MINOR;
            case WARNING:
                return WARNING;
            case CRITICAL:
                return CRITICAL;
            case INDETERMINATE:
                return INDETERMINATE;
            default:
                return null;
        }
    }
}
