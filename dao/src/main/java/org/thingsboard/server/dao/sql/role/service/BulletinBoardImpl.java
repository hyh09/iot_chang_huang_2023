package org.thingsboard.server.dao.sql.role.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.vo.tskv.MaxTsVo;
import org.thingsboard.server.dao.sql.role.dao.EffectMaxValueKvRepository;

/**
 * @program: thingsboard
 * @description: 看板的相关接口
 * @author: HU.YUNHUI
 * @create: 2021-12-07 10:52
 **/
@Data
@Service
public class BulletinBoardImpl implements BulletinBoardSvc {

    @Autowired
    private EffectMaxValueKvRepository effectMaxValueKvRepository;

    /**
     * 查询历史产能的接口
     *
     * @return
     */
    @Override
    public String historicalCapacity(MaxTsVo maxTsVo) {
        return  effectMaxValueKvRepository.querySum(maxTsVo);

    }
}
