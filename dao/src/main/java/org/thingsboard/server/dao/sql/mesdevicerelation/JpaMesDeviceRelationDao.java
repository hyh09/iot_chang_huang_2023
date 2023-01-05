package org.thingsboard.server.dao.sql.mesdevicerelation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.dao.mesdevicerelation.MesDeviceRelationDao;

import javax.transaction.Transactional;

@Component
@Transactional
public class JpaMesDeviceRelationDao implements MesDeviceRelationDao {

    @Autowired
    private MesDeviceRelationRepository mesDeviceRelationRepository;


}
