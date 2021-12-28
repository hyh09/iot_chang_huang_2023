package org.thingsboard.server.entity.systemversion.qry;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.entity.systemversion.AbstractSystemVersion;

@Data
@ApiModel(value = "SystemVersionQry",description = "查询条件")
public class SystemVersionQry extends AbstractSystemVersion {
}
