package org.thingsboard.server.dao.hs.entity.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.compress.utils.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DDMsg BO
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "DDMsgBO")
public class DDMsgBO {

    /**
     * url
     */
    @ApiModelProperty("url")
    private String url;

    /**
     * 租户名称
     */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /**
     * 工厂名称
     */
    @ApiModelProperty("工厂名称")
    private String factoryName;

    /**
     * 获得结果集
     */
    public Map<String, Object> getMsgResult() {
        Map<String, Object> map = new HashMap<>();
        Map<String, String> textMap = new HashMap<>();
        map.put("msgtype", "text");
        textMap.put("content", String.format("提示: 租户[%s]工厂[%s]所有设备离线，请检查！", tenantName, factoryName));
        map.put("text", textMap);
        return map;
    }
}

