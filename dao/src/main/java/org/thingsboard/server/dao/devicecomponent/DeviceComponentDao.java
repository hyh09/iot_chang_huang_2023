package org.thingsboard.server.dao.devicecomponent;

import org.thingsboard.server.common.data.devicecomponent.DeviceComponent;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.List;
import java.util.UUID;

public interface DeviceComponentDao{

    /**
     * 批量保存
     * @param deviceComponentList
     * @return
     * @throws ThingsboardException
     */
    List<DeviceComponent> saveDeviceComponentList(List<DeviceComponent> deviceComponentList) throws ThingsboardException;

    /**
     * 单条保存/修改
     * @param deviceComponent
     * @return
     */
    DeviceComponent saveDeviceComponent(DeviceComponent deviceComponent);

    /**
     * 删除设备所有构成
     * @param deviceId
     */
    void delDeviceComponentByDeviceId(UUID deviceId);

    /**
     * 删除指定设备部件
     * @param id
     */
    void delDeviceComponent(UUID id);

    /**
     * 分页查询
     * @param deviceComponent
     * @param pageLink
     * @return
     */
    PageData<DeviceComponent> findDeviceComponentByPage(DeviceComponent deviceComponent, PageLink pageLink);

    /**
     * 多条件列表查询
     * @param deviceComponent
     * @return
     */
    List<DeviceComponent> findDeviceComponentListByCdn(DeviceComponent deviceComponent);

    /**
     * 查询部件详情
     * @param id
     * @return
     */
    DeviceComponent getDeviceComponentById(UUID id);

    /**
     * 根据设备标识查询设备构成
     * @param deviceId
     * @return
     */
    List<DeviceComponent>  getDeviceComponentByDeviceId(UUID deviceId);

}
