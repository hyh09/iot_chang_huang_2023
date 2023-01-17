package org.thingsboard.server.dao.sql.mesdevicerelation;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.dao.mesdevicerelation.MesDeviceRelationDao;
import org.thingsboard.server.dao.model.sql.MesDeviceRelationEntity;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Transactional
public class JpaMesDeviceRelationDao implements MesDeviceRelationDao {

    @Autowired
    private MesDeviceRelationRepository mesDeviceRelationRepository;

    /**
     * 返回DeviceI对应的mes设备id
     *
     * @param deviceIds
     * @return
     */
    public List<UUID> getMesIdsByDeviceIds(List<UUID> deviceIds) {
        List<MesDeviceRelationEntity> deviceRelationEntityList = mesDeviceRelationRepository.findAllByDeviceIdIn(deviceIds);
        return deviceRelationEntityList.stream().map(MesDeviceRelationEntity::getMesDeviceId).collect(Collectors.toList());
    }


    /**
     * 返回mes对应的DeviceId设备id
     *
     * @param mesIds
     * @return
     */
    public List<UUID> getDeviceIdsByMesIds(List<UUID> mesIds) {
        List<MesDeviceRelationEntity> deviceRelationEntityList = mesDeviceRelationRepository.findAllByMesDeviceIdIn(mesIds);
        return deviceRelationEntityList.stream().map(MesDeviceRelationEntity::getDeviceId).collect(Collectors.toList());
    }

    /**
     * 返回DeviceI对应的mes设备id
     *
     * @param deviceId
     * @return
     */
    public UUID getMesIdByDeviceId(UUID deviceId) {
        List<UUID> deviceRelationEntityList = this.getMesIdsByDeviceIds(Arrays.asList(deviceId));
        if (CollectionUtils.isEmpty(deviceRelationEntityList)) {
            return null;
        }
        return deviceRelationEntityList.get(0);
    }

    /**
     * 返回mes对应的DeviceId设备id
     *
     * @param mesId
     * @return
     */
    public UUID getDeviceIdByMesId(UUID mesId) {
        List<UUID> deviceRelationEntityList = this.getDeviceIdsByMesIds(Arrays.asList(mesId));
        if (CollectionUtils.isEmpty(deviceRelationEntityList)) {
            return null;
        }
        return deviceRelationEntityList.get(0);
    }
}
