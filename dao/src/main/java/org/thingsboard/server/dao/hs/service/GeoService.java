package org.thingsboard.server.dao.hs.service;

import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.hs.entity.vo.GeoVO;

import java.io.IOException;
import java.util.List;

/**
 * geo 接口
 *
 * @author wwj
 * @since 2021.11.26
 */
public interface GeoService {

    /**
     * 查询城市列表
     *
     * @param geoVO  参数
     * @param userId 用户Id
     * @return 城市列表
     */
    List<GeoVO> listCitiesByQuery(GeoVO geoVO, UserId userId) throws IOException, InterruptedException;
}
