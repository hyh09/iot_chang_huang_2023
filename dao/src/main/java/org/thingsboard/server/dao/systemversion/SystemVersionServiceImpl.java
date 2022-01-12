package org.thingsboard.server.dao.systemversion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.systemversion.SystemVersion;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class SystemVersionServiceImpl implements SystemVersionService{

//    @Autowired
//    private SystemVersionDao systemVersionDao;

    private final SystemVersionDao systemVersionDao;

    public SystemVersionServiceImpl(SystemVersionDao systemVersionDao) {
        this.systemVersionDao = systemVersionDao;
    }

    @Override
    public SystemVersion saveSystemVersion(SystemVersion systemVersion) throws ThingsboardException {
        return systemVersionDao.saveSystemVersion(systemVersion);
    }

    @Override
    public SystemVersion updSystemVersion(SystemVersion systemVersion) throws ThingsboardException {
        return systemVersionDao.updSystemVersion(systemVersion);
    }

    @Override
    public void delSystemVersion(UUID id) throws ThingsboardException {
        systemVersionDao.delSystemVersion(id);
    }

    @Override
    public PageData<SystemVersion> findSystemVersionPage(SystemVersion systemVersion,PageLink pageLink) {
        return systemVersionDao.findSystemVersionPage(systemVersion,pageLink);
    }

    @Override
    public List<SystemVersion> findSystemVersionList(SystemVersion systemVersion) {
        return systemVersionDao.findSystemVersionList(systemVersion);
    }

    @Override
    public SystemVersion findById(UUID id) {
        return systemVersionDao.findById(id);
    }

    @Override
    public SystemVersion getSystemVersionMax(TenantId tenantId) throws ThingsboardException {
        return systemVersionDao.getSystemVersionMax(tenantId);
    }
}
