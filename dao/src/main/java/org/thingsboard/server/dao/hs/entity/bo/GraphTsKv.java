package org.thingsboard.server.dao.hs.entity.bo;

/**
 * 图表遥测属性
 *
 * @author wwj
 * @since 2022.2.10
 */
public interface GraphTsKv {
    Long getTs();

    String getValue();
}
