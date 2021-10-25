package org.thingsboard.server.dao.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FactoryServiceImpl extends AbstractEntityService implements FactoryService {

    private final FactoryDao factoryDao;

    public FactoryServiceImpl(FactoryDao factoryDao){
        this.factoryDao = factoryDao;
    }


    /**
     * 保存后刷新值
     * @param factory
     * @return
     */
    @Override
    public Factory saveFactory(Factory factory){
        log.trace("Executing saveFactory [{}]", factory);
        factory.setCode(String.valueOf(System.currentTimeMillis()));
        return factoryDao.save(new TenantId(factory.getTenantId()), factory);
    }

    /**
     * 修改后刷新值
     * @param factory
     * @return
     */
    @Override
    public Factory updFactory(Factory factory){
        log.trace("Executing updFactory [{}]", factory);
        return factoryDao.save(new TenantId(factory.getTenantId()), factory);
    }

    /**
     * 删除后刷新值
     * @param id
     * @param id
     * @return
     */
    @Override
    public void delFactory(UUID id){
        log.trace("Executing delFactory [{}]", id);
        factoryDao.removeById(null, id);
    }


    /**
     * 查询工厂列表
     * @param tenantId
     * @return
     */
    @Override
    public List<Factory> findFactoryList(UUID tenantId){
        log.trace("Executing findFactoryList [{}]", tenantId);
        return factoryDao.find(new TenantId(tenantId));
    }

    /**
     * 查询详情
     * @param id
     * @return
     */
    @Override
    public Factory findById(UUID id){
        log.trace("Executing findById [{}]", id);
        return factoryDao.findById(null, id);
    }
}
