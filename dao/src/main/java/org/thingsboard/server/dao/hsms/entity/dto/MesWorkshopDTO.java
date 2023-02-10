package org.thingsboard.server.dao.hsms.entity.dto;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
