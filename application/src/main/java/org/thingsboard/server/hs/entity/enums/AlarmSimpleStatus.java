package org.thingsboard.server.hs.entity.enums;

import lombok.Getter;
import org.thingsboard.server.common.data.alarm.AlarmStatus;

import java.util.*;

/**
 * 报警状态枚举
 *
 * @author wwj
 * @since 2021.10.26
 */
@Getter
public enum AlarmSimpleStatus {
    ANY("ANY", "type-any"),
    UN_ACK("ACTIVE_UNACK", "type-unack"),
    ACK("ACTIVE_ACK", "type-ack"),
    CLEARED("CLEARED_ACK", "type-cleared");

    private final String code;
    private final String name;

    AlarmSimpleStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public AlarmStatus toAlarmStatus() {
        if (this.equals(ANY))
            return null;
        return AlarmStatus.valueOf(this.code);
    }

    public Set<AlarmStatus> toAlarmStatusSet() {
        if (this.equals(ANY))
            return Set.of(AlarmStatus.ACTIVE_ACK, AlarmStatus.ACTIVE_UNACK, AlarmStatus.CLEARED_ACK);
        return Set.of(AlarmStatus.valueOf(this.code));
    }

    public static AlarmSimpleStatus toAlarmSimpleStatus(AlarmStatus alarmStatus) {
        switch (alarmStatus) {
            case ACTIVE_UNACK:
                return UN_ACK;
            case ACTIVE_ACK:
                return ACK;
            case CLEARED_ACK:
                return CLEARED;
            default:
                return null;
        }
    }

    public static List<Map<String, String>> toResourceList() {
        List<Map<String, String>> list = new ArrayList<>();
        Arrays.stream(AlarmSimpleStatus.values()).forEach(e -> {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put("name", e.getName());
            map.put("code", e.toString());
            list.add(map);
        });
        return list;
    }
}
