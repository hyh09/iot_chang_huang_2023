package org.thingsboard.server.dao.productioncalender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.hs.service.OrderService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ProductionCalenderServiceImpl implements ProductionCalenderService {

    private final ProductionCalenderDao productionCalenderDao;

    private final DeviceDao deviceDao;

    private final OrderService orderService;


    public ProductionCalenderServiceImpl(ProductionCalenderDao productionCalenderDao,DeviceDao deviceDao,OrderService orderService) {
        this.productionCalenderDao = productionCalenderDao;
        this.deviceDao = deviceDao;
        this.orderService = orderService;
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

    /**
     * 查询看板设备监控统计
     * @return
     */
    @Override
    public List<ProductionCalender> getProductionMonitorList(ProductionCalender productionCalender){
        //查询所有设备
        List<Device> deviceListByCdn = deviceDao.findDeviceListByCdn(new Device(productionCalender.getTenantId(), productionCalender.getFactoryId(), productionCalender.getWorkshopId(), true));
        if(!CollectionUtils.isEmpty(deviceListByCdn)){
            List<UUID> deviceIds = deviceListByCdn.stream().map(m -> m.getId().getId()).collect(Collectors.toList());
            //查询设备完成量/计划量
            orderService.getDeviceAchieveOrPlanList(deviceIds,productionCalender.getStartTime(),productionCalender.getEndTime());
        }

        return null;
    }
}
