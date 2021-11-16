package org.thingsboard.server.dao.hs.entity.enums;

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
public enum AlarmSimpleStatus implements EnumGetter{
    ANY("ANY", "alarm-status-any"),
    ACTIVE_UNACK("ACTIVE_UNACK", "alarm-status-active-unack"),
    ACTIVE_ACK("ACTIVE_ACK", "alarm-status-active-ack"),
    CLEARED_ACK("CLEARED_ACK", "alarm-status-cleared-ack");

    private final String code;
    private final String name;

    public Boolean isCanBeClear() {
        if (this.equals(ACTIVE_UNACK) || this.equals(ACTIVE_ACK)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean isCanBeConfirm() {
        if (this.equals(ACTIVE_UNACK)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

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
                return ACTIVE_UNACK;
            case ACTIVE_ACK:
                return ACTIVE_ACK;
            case CLEARED_ACK:
                return CLEARED_ACK;
            default:
                return null;
        }
    }
}
