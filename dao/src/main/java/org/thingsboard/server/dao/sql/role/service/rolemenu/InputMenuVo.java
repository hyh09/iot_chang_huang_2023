package org.thingsboard.server.dao.sql.role.service.rolemenu;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 *
 */
@Data
@ToString
public class InputMenuVo {

    private UUID  menuId;

    private String remark;

}
