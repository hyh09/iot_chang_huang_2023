package org.thingsboard.server.hs.service;

import org.thingsboard.server.hs.entity.vo.DictDeviceVO;

/**
 * 设备字典接口
 *
 * @author wwj
 * @since 2021.10.21
 */
public interface DictDeviceService {

    /**
     *  新增或修改设备字典
     * @param dictDeviceVO 设备字典入参
     */
    void updateOrSaveDictDevice(DictDeviceVO dictDeviceVO);
}
