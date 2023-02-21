package org.thingsboard.server.dao.hs.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceGraphAndPropertyVO extends DictDeviceGraphPropertyVO {

    private String chartName;


}
