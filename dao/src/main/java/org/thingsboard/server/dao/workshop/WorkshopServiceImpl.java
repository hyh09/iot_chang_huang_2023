package org.thingsboard.server.dao.workshop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
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
    public Workshop saveWorkshop(Workshop workshop){
        log.trace("Executing saveFactory [{}]", workshop);
        workshop.setCode(String.valueOf(System.currentTimeMillis()));
        return workshopDao.save(new TenantId(workshop.getTenantId()), workshop);
    }

    /**
     * 修改后刷新值
     * @param workshop
     * @return
     */
    @Override
    public Workshop updWorkshop(Workshop workshop){
        log.trace("Executing updFactory [{}]", workshop);
        return workshopDao.save(new TenantId(workshop.getTenantId()), workshop);
    }

    /**
     * 删除后刷新值
     * @param id
     * @param id
     * @return
     */
    @Override
    public void delWorkshop(UUID id){
        log.trace("Executing delFactory [{}]", id);
        workshopDao.removeById(null, id);
    }


    /**
     * 查询工厂列表
     * @param tenantId
     * @return
     */
    @Override
    public List<Workshop> findWorkshopList(UUID tenantId){
        log.trace("Executing findFactoryList [{}]", tenantId);
        return workshopDao.find(new TenantId(tenantId));
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
