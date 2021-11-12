package org.thingsboard.server.dao.productionline;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductionLineServiceImpl extends AbstractEntityService implements ProductionLineService {

    private static final String DEFAULT_TENANT_REGION = "Global";
    public static final String INCORRECT_MENU_ID = "Incorrect menuId ";
    public static final int ONE = 1;

    private final ProductionLineDao productionLineDao;

    public ProductionLineServiceImpl(ProductionLineDao productionLineDao){
        this.productionLineDao = productionLineDao;
    }

    /**
     * 保存后刷新值
     * @param factory
     * @return
     */
    @Override
    public ProductionLine saveProductionLine(ProductionLine factory){
        log.trace("Executing saveProductionLine [{}]", factory);
        factory.setCode(String.valueOf(System.currentTimeMillis()));
        return productionLineDao.saveProductionLine(factory);
    }

    /**
     * 修改后刷新值
     * @param factory
     * @return
     */
    @Override
    public ProductionLine updProductionLine(ProductionLine factory){
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
    public void delProductionLine(UUID id){
        log.trace("Executing delProductionLine [{}]", id);
        productionLineDao.delProductionLine( id);
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
        return productionLineDao.findById(null, id);
    }
}
