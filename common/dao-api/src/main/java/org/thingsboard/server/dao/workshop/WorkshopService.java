package org.thingsboard.server.dao.workshop;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.workshop.WorkshopId;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;

import java.util.List;
import java.util.UUID;

public interface WorkshopService {

    /**
     * 保存后刷新值
     * @param workshop
     * @return
     */
    Workshop saveWorkshop(Workshop workshop) throws ThingsboardException;

    /**
     * 修改后刷新值
     * @param workshop
     * @return
     */
    Workshop updWorkshop(Workshop workshop) throws ThingsboardException;

    /**
     * 删除后刷新值
     * @param id
     * @param id
     * @return
     */
    void delWorkshop(UUID id) throws ThingsboardException ;


    /**
     * 查询租户下所有车间列表
     * @param tenantId
     * @return
     */
    List<Workshop> findWorkshopListByTenant(UUID tenantId,UUID factoryId);

    /**
     * 查询详情
     * @param id
     * @return
     */
    Workshop findById(UUID id);

    ListenableFuture<Workshop> findWorkshopByIdAsync(TenantId callerId, WorkshopId workshopId);

}
