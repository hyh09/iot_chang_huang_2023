package org.thingsboard.server.dao.hs.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.vo.device.DictDeviceDataVo;
import org.thingsboard.server.dao.hs.dao.DictDeviceGroupPropertyRepository;
import org.thingsboard.server.dao.hs.dao.DictDeviceRepository;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupPropertyVO;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupVO;
import org.thingsboard.server.dao.hs.service.DeviceDictPropertiesSvc;
import org.thingsboard.server.dao.hs.service.DictDeviceService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2021-12-02 11:32
 **/
@Slf4j
@Service
public class DeviceDictPropertiesImpl implements DeviceDictPropertiesSvc {

    @Autowired private   DictDeviceRepository dictDeviceRepository;
    @Autowired private DictDeviceGroupPropertyRepository groupPropertyRepository;
    @Autowired
    DictDeviceService dictDeviceService;

    /**
     * 获取当前产能 能耗的数据 由 分组表 改为  hs_init  表中读取  ##hs_init 为程序初始化的数据
     *
     * @param dictDeviceId
     * @param name
     * @return
     */
    @Override
    public List<String> findAllByName(UUID dictDeviceId, String name) {
        List<String> nameList  = new ArrayList<>();
        List<DictDeviceGroupVO>  dictDeviceGroupVOS  = dictDeviceService.getDictDeviceGroupInitData();
        for(DictDeviceGroupVO  vos :dictDeviceGroupVOS)
        {
            if(vos.getName().equals(name))
            {
                nameList =  vos.getGroupPropertyList().stream().map(DictDeviceGroupPropertyVO::getName).collect(Collectors.toList());
            }
        }

        return  nameList;
//        List<DictDeviceGroupPropertyEntity> entities = this.groupPropertyRepository.findAllByName(name);
//        List<String> nameList = entities.stream().map(DictDeviceGroupPropertyEntity::getName).collect(Collectors.toList());
//        return nameList;
    }

    @Override
    public Map<String, DictDeviceGroupPropertyVO> getMapPropertyVoByTitle() {
        Map<String, DictDeviceGroupPropertyVO>  nameMap = new HashMap<>();
        List<DictDeviceGroupVO>  dictDeviceGroupVOS  = dictDeviceService.getDictDeviceGroupInitData();
        for(DictDeviceGroupVO  vo:dictDeviceGroupVOS)
        {
            List<DictDeviceGroupPropertyVO>  voList=  vo.getGroupPropertyList();
            voList.stream().forEach(vo1->{
                nameMap.put(vo1.getTitle(),vo1);
            });
        }
        return  nameMap;
    }


    @Override
    public Map<String, DictDeviceGroupPropertyVO> getMapPropertyVo() {
        Map<String, DictDeviceGroupPropertyVO>  nameMap = new HashMap<>();
        List<DictDeviceGroupVO>  dictDeviceGroupVOS  = dictDeviceService.getDictDeviceGroupInitData();
        for(DictDeviceGroupVO  vo:dictDeviceGroupVOS)
        {
            List<DictDeviceGroupPropertyVO>  voList=  vo.getGroupPropertyList();
            voList.stream().forEach(vo1->{
                nameMap.put(vo1.getName(),vo1);
            });
        }
        return  nameMap;
    }


    /**
     *
     * @return
     */
    @Override
    public  Map<String,String> getUnit()
    {
        Map<String, String> map = new HashMap<>();

        List<DictDeviceGroupVO>  dictDeviceGroupVOS  = dictDeviceService.getDictDeviceGroupInitData();
        log.info("打印当前的数据:{}",dictDeviceGroupVOS);
        for(DictDeviceGroupVO  vo:dictDeviceGroupVOS)
        {
            Map map1=   vo.getGroupPropertyList().stream().collect(Collectors.toMap(DictDeviceGroupPropertyVO::getName,DictDeviceGroupPropertyVO::getTitle));
            map.putAll(map1);
        }
        return map;

    }

    @Override
    public List<DictDeviceGroupPropertyVO>  findAllDictDeviceGroupVO(String name) {
        List<DictDeviceGroupPropertyVO>  voList  = new ArrayList<>();
        List<DictDeviceGroupVO>  dictDeviceGroupVOS  = dictDeviceService.getDictDeviceGroupInitData();
        for(DictDeviceGroupVO  vos :dictDeviceGroupVOS)
        {
            if(vos.getName().equals(name))
            {
                voList.addAll(vos.getGroupPropertyList());
            }
        }

        return  voList;
    }

    @Override
    public List<DictDeviceDataVo> findGroupNameAndName(UUID dictDeviceId) {
        return this.groupPropertyRepository.findGroupNameAndName(dictDeviceId);
    }

}
