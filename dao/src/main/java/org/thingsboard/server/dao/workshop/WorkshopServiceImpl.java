package org.thingsboard.server.dao.workshop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class WorkshopServiceImpl extends AbstractEntityService implements WorkshopService {

    private final WorkshopDao workshopDao;

    public WorkshopServiceImpl(WorkshopDao workshopDao){
        this.workshopDao = workshopDao;
    }

    /**
     * 保存后刷新值
     * @param workshop
     * @return
     */
    @Override
    public Workshop saveWorkshop(Workshop workshop) throws ThingsboardException {
        log.trace("Executing saveWorkshop [{}]", workshop);
        workshop.setCode(String.valueOf(System.currentTimeMillis()));
        return workshopDao.saveWorkshop(workshop);
    }

    /**
     * 修改后刷新值
     * @param workshop
     * @return
     */
    @Override
    public Workshop updWorkshop(Workshop workshop) throws ThingsboardException{
        log.trace("Executing updWorkshop [{}]", workshop);
        return workshopDao.saveWorkshop(workshop);
    }

    /**
     * 删除后刷新值
     * @param id
     * @param id
     * @return
     */
    @Override
    public void delWorkshop(UUID id){
        log.trace("Executing delWorkshop [{}]", id);
        workshopDao.delWorkshop(id);
    }


    /**
     * 查询租户下所有车间列表
     * @param tenantId
     * @return
     */
    @Override
    public List<Workshop> findWorkshopListByTenant(UUID tenantId,UUID factoryId){
        log.trace("Executing findWorkshopListByTenant [{}]", tenantId,factoryId);
        return workshopDao.findWorkshopListByTenant(tenantId,factoryId);
    }

    /**
     * 查询详情
     * @param id
     * @return
     */
    @Override
    public Workshop findById(UUID id){
        log.trace("Executing findById [{}]", id);
        return workshopDao.findById(null, id);
    }
}
