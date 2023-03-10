package org.thingsboard.server.dao.productionline;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.factory.FactoryId;
import org.thingsboard.server.common.data.id.productionline.ProductionLineId;
import org.thingsboard.server.common.data.id.workshop.WorkshopId;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.relation.RelationDao;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static org.thingsboard.server.dao.service.Validator.validateId;

@Service
@Slf4j
@Transactional
public class ProductionLineServiceImpl extends AbstractEntityService implements ProductionLineService {

    private static final String DEFAULT_TENANT_REGION = "Global";
    public static final String INCORRECT_MENU_ID = "Incorrect menuId ";
    public static final String INCORRECT_PRODUCTION_LINE_ID = "不正确的 productionLineId ";
    public static final int ONE = 1;
    private final RelationDao relationDao;
    private final ProductionLineDao productionLineDao;

    public ProductionLineServiceImpl(ProductionLineDao productionLineDao, RelationDao relationDao){
        this.productionLineDao = productionLineDao;
        this.relationDao = relationDao;
    }

    /**
     * 保存后刷新值
     * @param productionLine
     * @return
     */
    @Override
    public ProductionLine saveProductionLine(ProductionLine productionLine) throws ThingsboardException {
        log.trace("Executing saveProductionLine [{}]", productionLine);
        productionLine.setCode(String.valueOf(System.currentTimeMillis()));
        ProductionLine productionLineResult = productionLineDao.saveProductionLine(productionLine);
        //建立实体关系
        EntityRelation relation = new EntityRelation(
                new WorkshopId(productionLineResult.getWorkshopId()), new ProductionLineId(productionLineResult.getId()), EntityRelation.CONTAINS_TYPE
        );
        relationService.saveRelation(new TenantId(productionLineResult.getTenantId()), relation);
        return productionLineResult;
    }

    /**
     * 修改后刷新值
     * @param factory
     * @return
     */
    @Override
    public ProductionLine updProductionLine(ProductionLine factory) throws ThingsboardException{
        log.trace("Executing updProductionLine [{}]", factory);
        return productionLineDao.saveProductionLine(factory);
    }

    /**
     * 删除后刷新值
     * @param id
     * @param id
     * @return
     */
    @Override
    public void delProductionLine(UUID id) throws ThingsboardException{
        log.trace("Executing delProductionLine [{}]", id);
        ProductionLine byId = productionLineDao.findById(id);
        productionLineDao.delProductionLine( id);
        //清除实体关系
        deleteEntityRelations(new TenantId(byId.getTenantId()), new ProductionLineId(id));
//        if(byId != null && byId.getWorkshopId() != null){
//            EntityRelation relation = new EntityRelation(
//                    new WorkshopId(byId.getWorkshopId()),new ProductionLineId(id), EntityRelation.CONTAINS_TYPE
//            );
//            relationDao.deleteRelation(new TenantId(byId.getTenantId()), relation);
//        }
    }


    /**
     * 询租户/工厂/车间下所有生产线列表
     * @param tenantId
     * @param workshopId
     * @param factoryId
     * @return
     */
    @Override
    public List<ProductionLine> findProductionLineList(UUID tenantId,UUID workshopId,UUID factoryId){
        log.trace("Executing findProductionLineList [{}]", tenantId);
        return productionLineDao.findProductionLineList(tenantId,workshopId,factoryId);
    }

    /**
     * 查询详情
     * @param id
     * @return
     */
    @Override
    public ProductionLine findById(UUID id){
        log.trace("Executing findById [{}]", id);
        return productionLineDao.findById(id);
    }

    @Override
    public ListenableFuture<ProductionLine> findProductionLineByIdAsync(TenantId callerId, ProductionLineId productionLineId) {
        log.trace("执行 findProductionLineByIdAsync [{}]", productionLineId);
        validateId(productionLineId, INCORRECT_PRODUCTION_LINE_ID + productionLineId);
        return productionLineDao.findProductionLineByIdAsync(callerId, productionLineId.getId());
    }
}
