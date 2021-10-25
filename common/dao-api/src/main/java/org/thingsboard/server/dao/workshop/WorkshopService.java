package org.thingsboard.server.dao.workshop;

import org.thingsboard.server.common.data.workshop.Workshop;

import java.util.List;
import java.util.UUID;

public interface WorkshopService {

    /**
     * 保存后刷新值
     * @param workshop
     * @return
     */
    Workshop saveWorkshop(Workshop workshop);

    /**
     * 修改后刷新值
     * @param workshop
     * @return
     */
    Workshop updWorkshop(Workshop workshop);

    /**
     * 删除后刷新值
     * @param id
     * @param id
     * @return
     */
    void delWorkshop(UUID id);


    /**
     * 查询工厂列表
     * @param tenantId
     * @return
     */
    List<Workshop> findWorkshopList(UUID tenantId);

    /**
     * 查询详情
     * @param id
     * @return
     */
    Workshop findById(UUID id);
}
