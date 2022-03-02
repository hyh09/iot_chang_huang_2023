package org.thingsboard.server.dao.productioncalender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class ProductionCalenderServiceImpl implements ProductionCalenderService {

    private final ProductionCalenderDao productionCalenderDao;

    public ProductionCalenderServiceImpl(ProductionCalenderDao productionCalenderDao) {
        this.productionCalenderDao = productionCalenderDao;
    }

    @Override
    public void saveProductionCalender(ProductionCalender productionCalender) throws ThingsboardException {
        productionCalenderDao.saveProductionCalender(productionCalender);
    }

    @Override
    public void delProductionCalender(UUID id) throws ThingsboardException {
        productionCalenderDao.delProductionCalender(id);
    }

    @Override
    public ProductionCalender findById(UUID id) {
        return productionCalenderDao.findById(id);
    }

    @Override
    public PageData<ProductionCalender> findProductionCalenderPage(ProductionCalender productionCalender, PageLink pageLink) {
        return productionCalenderDao.findProductionCalenderPage(productionCalender,pageLink);
    }

    @Override
    public List<ProductionCalender> getHistoryById(UUID deviceId) {
        return productionCalenderDao.getHistoryById(deviceId);
    }
}
