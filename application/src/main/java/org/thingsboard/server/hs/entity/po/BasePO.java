package org.thingsboard.server.hs.entity.po;

import lombok.Data;

/**
 * 基础PO
 *
 * @author wwj
 * @since 2021.10.19
 */
@Data
public class BasePO {
    /**
     * 创建时间
     */
    private Long createdTime;

    /**
     * 创建人
     */
    private String createdUser;

    /**
     * 更新时间
     */
    private Long updatedTime;

    /**
     * 修改人
     */
    private String updatedUser;
}
