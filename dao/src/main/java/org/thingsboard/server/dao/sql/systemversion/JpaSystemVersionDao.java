package org.thingsboard.server.dao.sql.systemversion;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.systemversion.SystemVersion;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.SystemVersionEntity;
import org.thingsboard.server.dao.systemversion.SystemVersionDao;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaSystemVersionDao implements SystemVersionDao {

    @Autowired
    private SystemVersionRepository systemVersionRepository;

    @Override
    public SystemVersion saveSystemVersion(SystemVersion systemVersion) throws ThingsboardException {
        if(systemVersion != null && StringUtils.isNotEmpty(systemVersion.getVersion())){
            SystemVersion checkQry = new SystemVersion();
            checkQry.setVersion(systemVersion.getVersion());
            checkQry.setTenantId(systemVersion.getTenantId());
            List<SystemVersion> systemVersionList = this.findSystemVersionList(checkQry);
            if(CollectionUtils.isNotEmpty(systemVersionList)){
                throw new ThingsboardException("版本号重复！",ThingsboardErrorCode.GENERAL);
            }
            UUID uuid = Uuids.timeBased();
            systemVersion.setId(uuid);
            systemVersion.setCreatedTime(Uuids.unixTimestamp(uuid));
            if(systemVersion.getPublishTime() == null || systemVersion.getPublishTime() == 0){
                systemVersion.setPublishTime(systemVersion.getCreatedTime());
            }
        }
        return this.saveOrUpdate(systemVersion);
    }

    @Override
    public SystemVersion updSystemVersion(SystemVersion systemVersion) throws ThingsboardException {
        if(systemVersion != null && StringUtils.isNotEmpty(systemVersion.getVersion())){
            SystemVersion checkQry = new SystemVersion();
            checkQry.setVersion(systemVersion.getVersion());
            checkQry.setTenantId(systemVersion.getTenantId());
            List<SystemVersion> systemVersionList = this.findSystemVersionList(checkQry);
            if(CollectionUtils.isNotEmpty(systemVersionList)){
                if(!systemVersion.getId().toString().equals(systemVersionList.get(0).getId().toString())){
                    throw new ThingsboardException("版本号重复！",ThingsboardErrorCode.GENERAL);
                }
            }
            systemVersion.setUpdatedTime(Uuids.unixTimestamp(Uuids.timeBased()));
        }
        return this.saveOrUpdate(systemVersion);
    }

    /**
     * 保存或修改
     * @param systemVersion
     * @return
     */
    private SystemVersion saveOrUpdate(SystemVersion systemVersion){
        SystemVersion version = new SystemVersion();
        SystemVersionEntity versionEntity = new SystemVersionEntity(systemVersion);
        if (versionEntity.getUuid() == null) {
            UUID uuid = Uuids.timeBased();
            versionEntity.setUuid(uuid);
            versionEntity.setCreatedTime(Uuids.unixTimestamp(uuid));
        }
        SystemVersionEntity entity = systemVersionRepository.save(versionEntity);
        if(entity != null){
            version = entity.toData();
        }
        return version;
    }

    @Override
    public void delSystemVersion(UUID id) throws ThingsboardException {
        systemVersionRepository.deleteById(id);
    }

    /**
     * 分页查询
     * @param pageLink
     * @return
     */
    @Override
    public PageData<SystemVersion> findSystemVersionPage(SystemVersion systemVersion ,PageLink pageLink) {
        List<SystemVersion> result = new ArrayList<>();
        // 动态条件查询
        Specification<SystemVersionEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(systemVersion != null){
                if(systemVersion.getTenantId() != null){
                    predicates.add(cb.equal(root.get("tenantId"),systemVersion.getTenantId()));
                }
                if(StringUtils.isNotEmpty(systemVersion.getVersion())){
                    predicates.add(cb.equal(root.get("version"),systemVersion.getVersion()));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = DaoUtil.toPageable(pageLink);
        Page<SystemVersionEntity> systemVersionEntities = systemVersionRepository.findAll(specification, pageable);
        //转换数据
        List<SystemVersionEntity> content = systemVersionEntities.getContent();

        if(CollectionUtils.isNotEmpty(content)){
            content.forEach(i->{
                result.add(i.toData());
            });
        }
        PageData<SystemVersion> resultPage = new PageData<>();
        resultPage = new PageData<SystemVersion>(result,systemVersionEntities.getTotalPages(),systemVersionEntities.getTotalElements(),systemVersionEntities.hasNext());
        return resultPage;
    }

    /**
     * 查询历史版本
     * @param systemVersion
     * @return
     */
    @Override
    public List<SystemVersion> findSystemVersionList(SystemVersion systemVersion) {
        List<SystemVersion> result = new ArrayList<>();
        Specification<SystemVersionEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(systemVersion != null){
                if(systemVersion.getTenantId() != null){
                    predicates.add(cb.equal(root.get("tenantId"),systemVersion.getTenantId()));
                }
                if(StringUtils.isNotEmpty(systemVersion.getVersion())){
                    predicates.add(cb.equal(root.get("version"),systemVersion.getVersion()));
                }
            }
            /**
             * order By
             */
            Order publishTimeOrder = cb.desc(root.get("publishTime"));
            return  query.orderBy(publishTimeOrder).where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        };
        List<SystemVersionEntity> entityList = systemVersionRepository.findAll(specification);
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.forEach(s->{
                result.add(s.toData());
            });
        }
        return result;
    }

    @Override
    public SystemVersion findById(UUID id) {
        Optional<SystemVersionEntity> byId = systemVersionRepository.findById(id);
        if(byId != null && byId.get() != null){
            return byId.get().toData();
        }
        return null;
    }

    @Override
    public SystemVersion getSystemVersionMax(TenantId tenantId) throws ThingsboardException {
        List<SystemVersion> systemVersionList = this.findSystemVersionList(new SystemVersion(tenantId));
        if(CollectionUtils.isNotEmpty(systemVersionList)){
            return systemVersionList.get(0);
        }
        return null;
    }
}
