package org.thingsboard.server.dao.hs.entity.vo;

import lombok.*;
import lombok.experimental.Accessors;

@ToString
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceGraphAndPropertyVO  extends DictDeviceGraphPropertyVO{

    private String chartName;



}
