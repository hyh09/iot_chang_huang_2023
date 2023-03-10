package org.thingsboard.server.dao.hs.service;

import org.thingsboard.server.common.data.vo.device.DictDeviceDataVo;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 设备字典属性的接口(由原接口DictDeviceService中提取出自己的避免git覆盖 ）
 * @author: HU.YUNHUI
 * @create: 2021-12-02 11:31
 **/
public interface DeviceDictPropertiesSvc {


    /**
     * @param dictDeviceId
     * @param name
     * @return map: key-name  ,value-name
     */
    List<String> findAllByName(UUID dictDeviceId, String name);

    /**
     * 2021-11-29 15:22
     * 查询初始化得数据 分组属性
     *
     * @return
     */
    List<DictDeviceGroupPropertyVO> findAllDictDeviceGroupVO(String name);

    /**
     * 初始化的数据的
     *
     * @return
     */
    Map<String, DictDeviceGroupPropertyVO> getMapPropertyVo();

    /**
     * 通过标题取数据
     */
    Map<String, DictDeviceGroupPropertyVO> getMapPropertyVoByTitle();

    /**
     * 获取初始化Title
     *
     * @return
     */
    Map<String, String> getUnit();


    List<DictDeviceDataVo> findGroupNameAndName(UUID dictDeviceId);

}
