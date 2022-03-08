package org.thingsboard.server.dao.board;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.vo.BoardV3DeviceDictionaryVo;
import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.common.data.vo.bodrd.DashboardV3Vo;
import org.thingsboard.server.common.data.vo.enums.KeyTitleEnums;
import org.thingsboard.server.dao.board.repository.BoardV3DeviceDictionaryReposutory;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.hs.dao.DictDeviceStandardPropertyEntity;
import org.thingsboard.server.dao.hs.dao.DictDeviceStandardPropertyRepository;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.sql.role.entity.BoardV3DeviceDitEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2022-03-07 11:19
 **/
@Slf4j
@Service
public class BulletinV3BoardImpl implements  BulletinV3BoardVsSvc{


    @Autowired  private BoardV3DeviceDictionaryReposutory boardV3DeviceDictionaryReposutory;
    @Autowired  private DeviceService deviceService;
    @Autowired  private DictDeviceStandardPropertyRepository dictDeviceStandardPropertyRepository;
    @Autowired private DeviceDictPropertiesSvc deviceDictPropertiesSvc;


    /**
     *
     * @param tsSqlDayVo
     * @return
     */
    @Override
    public List<BoardV3DeviceDitEntity> queryDeviceDictionaryByEntityVo(TsSqlDayVo tsSqlDayVo) {
        try {
            return boardV3DeviceDictionaryReposutory.queryDeviceDictionaryByEntityVo(tsSqlDayVo);
        }catch (Exception e)
        {
             log.info("queryDeviceDictionaryByEntityVo.异常:{}",e);
            return  null;
        }
    }

    @Override
    public List<DashboardV3Vo> queryDashboardValue(BoardV3DeviceDictionaryVo vo) {
        List<DashboardV3Vo>  dashboardV3Vos  = new ArrayList<>();

        log.info("方法:BulletinV3BoardImpl.queryDashboardValue的入参:{}",vo);
        Device  device = new  Device();
        device.setDictDeviceId(vo.getId());
        Map<String, DictDeviceGroupPropertyVO>  map = deviceDictPropertiesSvc.getMapPropertyVoByTitle();
        List<String> keyName= map.entrySet().stream().map(e->e.getValue().getName()).collect(Collectors.toList());
        List<DictDeviceStandardPropertyEntity>   dictDeviceStandardPropertyEntities= dictDeviceStandardPropertyRepository.findAllByInContentAndDictDataId(vo.getId(),keyName);
        Map<String,String> stringStringMap= dictDeviceStandardPropertyEntities.stream().collect(Collectors.toMap(DictDeviceStandardPropertyEntity::getName,DictDeviceStandardPropertyEntity::getContent));
        log.info("方法:BulletinV3BoardImpl.queryDashboardValue的查询到的stringStringMap出参:{}",dictDeviceStandardPropertyRepository);
        List<Device> deviceListByCdn = deviceService.findDeviceIssueListByCdn(device);
        log.info("方法:BulletinV3BoardImpl.queryDashboardValue的查询到的deviceListByCdn出参:{}",deviceListByCdn);
        dashboardV3Vos.add(getResultList(stringStringMap,KeyTitleEnums.key_water,map));
        dashboardV3Vos.add(getResultList(stringStringMap,KeyTitleEnums.key_cable,map));
        dashboardV3Vos.add(getResultList(stringStringMap,KeyTitleEnums.key_gas,map));
        return dashboardV3Vos;
    }


    private DashboardV3Vo getResultList(Map<String,String> stringStringMap,KeyTitleEnums  enums, Map<String, DictDeviceGroupPropertyVO> map)
    {
        DashboardV3Vo  vo1 = new DashboardV3Vo();
        DictDeviceGroupPropertyVO  dictVo =  map.get(enums.getgName());
        vo1.setName(enums.getgName());
        vo1.setStandardValue(stringStringMap.get(dictVo.getName()));
       return  vo1;

    }
}
