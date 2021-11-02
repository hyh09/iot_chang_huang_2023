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
public enum AlarmSimpleLevel {
    ANY("ANY", "type-any"),
    CRITICAL("CRITICAL", "type-critical"),
    MAJOR("MAJOR", "type-major"),
    MINOR("MINOR", "type-minor"),
    WARNING("WARNING", "type-warning"),
    INDETERMINATE("INDETERMINATE", "type-indeterminate");

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

    public static List<Map<String, String>> toResourceList() {
        List<Map<String, String>> list = new ArrayList<>();
        Arrays.stream(AlarmSimpleLevel.values()).forEach(e -> {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put("name", e.getName());
            map.put("code", e.toString());
            list.add(map);
        });
        return list;
    }
}
