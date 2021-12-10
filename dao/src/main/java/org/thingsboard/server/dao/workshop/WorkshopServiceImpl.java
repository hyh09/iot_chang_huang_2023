package org.thingsboard.server.dao.workshop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.factory.FactoryId;
import org.thingsboard.server.common.data.id.workshop.WorkshopId;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.relation.RelationDao;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class WorkshopServiceImpl extends AbstractEntityService implements WorkshopService {

    private final WorkshopDao workshopDao;
    private final RelationDao relationDao;

    public WorkshopServiceImpl(WorkshopDao workshopDao,RelationDao relationDao){
        this.workshopDao = workshopDao;
        this.relationDao = relationDao;
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
        Workshop workshopResult = workshopDao.saveWorkshop(workshop);
        //建立实体关系
        EntityRelation relation = new EntityRelation(
                new WorkshopId(workshopResult.getId()), new FactoryId(workshopResult.getFactoryId()), EntityRelation.CONTAINS_TYPE
        );
        relationService.saveRelation(new TenantId(workshopResult.getTenantId()), relation);
        return workshopResult;
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
        Workshop byId = workshopDao.findById(id);
        //清除实体关系
        if(byId != null && byId.getFactoryId() != null){
            EntityRelation relation = new EntityRelation(
                    new WorkshopId(id),new FactoryId(byId.getFactoryId()), EntityRelation.CONTAINS_TYPE
            );
            relationDao.deleteRelation(new TenantId(byId.getTenantId()), relation);
        }
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
        return workshopDao.findById(id);
    }
}
