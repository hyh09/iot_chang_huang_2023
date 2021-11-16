package org.thingsboard.server.dao.hs.entity.enums;

/**
 * 枚举实体类Getter
 *
 * @author wwj
 * @since 2021.11.16
 */
public interface EnumGetter {
    /**
     * 获得code
     *
     * @return code
     */
    String getCode();

    /**
     * 获得name
     *
     * @return name
     */
    String getName();
}
