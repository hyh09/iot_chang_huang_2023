package org.thingsboard.server.dao.hsms.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.thingsboard.server.dao.hs.entity.po.BasePO;
import org.thingsboard.server.dao.hsms.entity.vo.DictDevicePropertySwitchNewVO;

import javax.validation.Valid;
import java.util.List;

/**
 * Mes 车间
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWorkshopDTO {

    private static final long serialVersionUID = 4134987555236813704L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private String id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "name")
    private String name;
}
