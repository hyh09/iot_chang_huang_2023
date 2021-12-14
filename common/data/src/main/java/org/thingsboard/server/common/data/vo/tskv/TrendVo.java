package org.thingsboard.server.common.data.vo.tskv;

import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.vo.tskv.consumption.TrendLineVo;

import java.util.List;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2021-12-14 16:04
 **/
@Data
@ToString
public class TrendVo {
    private List<TrendLineVo> solidLine;

    private List<TrendLineVo> dottedLine;
}
