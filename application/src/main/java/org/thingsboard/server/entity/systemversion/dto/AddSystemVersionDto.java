package org.thingsboard.server.entity.systemversion.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.entity.systemversion.AbstractSystemVersion;

@Data
@ApiModel("AddSystemVersionDto")
public class AddSystemVersionDto extends AbstractSystemVersion {
}
