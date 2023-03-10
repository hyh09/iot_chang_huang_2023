package org.thingsboard.server.dao.factory;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.factory.FactoryListVo;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.id.factory.FactoryId;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.vo.JudgeUserVo;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.relation.RelationDao;
import org.thingsboard.server.dao.sql.role.service.UserRoleMenuSvc;

import javax.persistence.Column;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.service.Validator.validateId;

@Service
@Slf4j
@Transactional
public class FactoryServiceImpl extends AbstractEntityService implements FactoryService {

    public static final String INCORRECT_FACTORY_ID = "不正确的 factoryId ";
    private static final String PREFIX_ENCODING_GC = "GC";


    private final FactoryDao factoryDao;
    private final UserRoleMenuSvc userRoleMenuSvc;
    private final DeviceService deviceService;
    private final RelationDao relationDao;

    public FactoryServiceImpl(FactoryDao factoryDao, UserRoleMenuSvc userRoleMenuSvc, DeviceService deviceService, RelationDao relationDao) {
        this.factoryDao = factoryDao;
        this.userRoleMenuSvc = userRoleMenuSvc;
        this.deviceService = deviceService;
        this.relationDao = relationDao;
    }


    /**
     * 保存后刷新值
     *
     * @param factory
     * @return
     */
    @Override
    public Factory saveFactory(Factory factory) throws ThingsboardException {
        log.trace("Executing saveFactory [{}]", factory);
        factory.setCode(PREFIX_ENCODING_GC + String.valueOf(System.currentTimeMillis()));
        Factory factorySave = factoryDao.saveFactory(factory);
        //创建工厂管理员角色
        userRoleMenuSvc.saveRole(factory.getTenantId(), factory.getCreatedUser(), factorySave.getId());
        //建立实体关系
        EntityRelation relation = new EntityRelation(
                new TenantId(factorySave.getTenantId()), new FactoryId(factorySave.getId()), EntityRelation.CONTAINS_TYPE
        );
        relationService.saveRelation(new TenantId(factorySave.getTenantId()), relation);
        return factorySave;
    }

    /**
     * 修改后刷新值
     *
     * @param factory
     * @return
     */
    @Override
    public Factory updFactory(Factory factory) throws ThingsboardException {
        log.trace("Executing updFactory [{}]", factory);
        return factoryDao.saveFactory(factory);
    }

    /**
     * 删除后刷新值(逻辑删除)
     *
     * @param id
     * @param id
     * @return
     */
    @Override
    public void delFactory(UUID id) throws ThingsboardException {
        log.trace("Executing delFactory [{}]", id);
        Factory byId = factoryDao.findById(id);
        factoryDao.delFactory(id);
        //清除实体关系
        deleteEntityRelations(new TenantId(byId.getTenantId()), new FactoryId(id));
        /*if(byId != null && byId.getTenantId() != null){
            EntityRelation relation = new EntityRelation(
                    new TenantId(byId.getTenantId()),new FactoryId(id), EntityRelation.CONTAINS_TYPE
            );
            relationDao.deleteRelation(new TenantId(byId.getTenantId()), relation);
        }*/
    }


    /**
     * 查询工厂列表
     *
     * @param tenantId
     * @return
     */
    @Override
    public List<Factory> findFactoryList(UUID tenantId) {
        log.trace("Executing findFactoryList [{}]", tenantId);
        return factoryDao.findFactoryByTenantId(tenantId);
    }

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    @Override
    public Factory findById(UUID id) {
        log.trace("Executing findById [{}]", id);
        return factoryDao.findById(id);
    }

    /**
     * 条件查询工厂列表
     *
     * @param factory
     * @return
     */
    @Override
    public FactoryListVo findFactoryListByCdn(Factory factory) {
        log.trace("Executing findFactoryListBuyCdn [{}]", factory);
        JudgeUserVo judgeUserVo = userRoleMenuSvc.decideUser(new UserId(factory.getLoginUserId()));
        FactoryListVo factoryListByCdn = factoryDao.findFactoryListByCdn(factory, judgeUserVo);
        //查询未分配的设备
        if (judgeUserVo != null && judgeUserVo.getTenantFlag() && factory.getTenantId() != null) {
            List<Device> notDistributionDevice = deviceService.getNotDistributionDevice(new TenantId(factory.getTenantId()));
            if (CollectionUtils.isNotEmpty(notDistributionDevice)) {
                factoryListByCdn.renameByNotDistributionList(notDistributionDevice);
            }
        }
        return factoryListByCdn;
    }

    /**
     * 查询工厂最新版本
     *
     * @param factory
     * @return
     */
    @Override
    public List<Factory> findFactoryVersion(Factory factory) throws ThingsboardException {
        //查询当前登录人能查看的所有工厂
        List<Factory> resultFactory = this.findFactoryListByLoginRole(factory.getLoginUserId(), factory.getTenantId());
        //查询工厂关联的最新版本的网关设备版本信息
        if (CollectionUtils.isNotEmpty(resultFactory)) {
            List<UUID> factoryIdList = resultFactory.stream().map(Factory::getId).collect(Collectors.toList());
            //查询工厂最大版本
            List<Device> gatewayNewVersionByFactory = deviceService.findGatewayNewVersionByFactory(factoryIdList);
            if (CollectionUtils.isNotEmpty(gatewayNewVersionByFactory)) {
                resultFactory.forEach(i -> {
                    for (Device j : gatewayNewVersionByFactory) {
                        //筛选网关设备名称
                        if (StringUtils.isNotEmpty(factory.getGatewayName()) && !factory.getGatewayName().equals(j.getFactoryName())) {
                            continue;
                        }
                        if (i.getId().toString().equals(j.getFactoryId().toString())) {
                            i.setFactoryVersion(j.getGatewayVersion());
                            if (j.getGatewayUpdateTs() != null) {
                                i.setPublishTime(j.getGatewayUpdateTs());
                            }
                            i.setGatewayName(j.getName());
                            i.setActive(j.getActive());
                        }
                    }
                });
            }
        }
        return resultFactory;
    }

    /**
     * 查询工厂所有版本列表
     *
     * @param factory
     * @return
     * @throws Exception
     */
    @Override
    public List<Factory> findFactoryVersionList(Factory factory) throws Exception {
        List<Factory> resultFactory = new ArrayList<>();
        //查询当前登录人能查看的所有工厂
        List<Factory> factoryByRole = this.findFactoryListByLoginRole(factory.getLoginUserId(), factory.getTenantId());
        //查询工厂关联的网关设备版本信息
        if (CollectionUtils.isNotEmpty(factoryByRole)) {
            List<UUID> factoryIdList = factoryByRole.stream().map(Factory::getId).collect(Collectors.toList());
            //查询工厂网关设备
            List<Device> gatewayNewVersionByFactory = deviceService.findGatewayListVersionByFactory(factoryIdList);
            //返回网关设备信息
            if (CollectionUtils.isNotEmpty(gatewayNewVersionByFactory)) {
                for (Device m : gatewayNewVersionByFactory) {
                    //筛选网关设备名称
                    if (StringUtils.isNotEmpty(m.getFactoryName()) && StringUtils.isNotEmpty(factory.getGatewayName())) {
                        int i = m.getFactoryName().indexOf(factory.getGatewayName());
                        if (i == -1) {
                            continue;
                        }
                    }
                    Factory rstFactory = new Factory();
                    Factory factoryName = factoryByRole.stream().filter(f -> f.getId().toString().equals(m.getFactoryId().toString())).collect(Collectors.toList()).stream().findFirst().get();
                    if (factoryName != null) {
                        rstFactory.setName(factoryName.getName());
                        rstFactory.setLogoImages(factoryName.getLogoImages());
                    }
                    rstFactory.setFactoryVersion(m.getGatewayVersion());
                    if (m.getGatewayUpdateTs() != null) {
                        rstFactory.setPublishTime(m.getGatewayUpdateTs());
                    }
                    String rename = m.getRename();
                    rstFactory.setGatewayName(rename);
                    rstFactory.setActive(m.getActive());
                    resultFactory.add(rstFactory);
                }
            }
        }
        return resultFactory;

    }

    /**
     * 根据登录人角色查询工厂列表
     *
     * @param userId
     * @param tenantId
     * @return
     */
    @Override
    public List<Factory> findFactoryListByLoginRole(UUID userId, UUID tenantId) {
        List<Factory> resultFactory = new ArrayList<>();
        //查询登录人角色
        //查询登录人角色及所属工厂
        JudgeUserVo judgeUserVo = userRoleMenuSvc.decideUser(new UserId(userId));
        if (judgeUserVo != null && judgeUserVo.getTenantFlag() != null && judgeUserVo.getTenantFlag()) {
            //租户管理员/租户有菜单权限的用户，拥有全部数据权限
            resultFactory = factoryDao.findFactoryByTenantIdToBoard(tenantId);
        } else if (judgeUserVo != null && judgeUserVo.getFactoryManagementFlag() != null && judgeUserVo.getFactoryManagementFlag()) {
            //工厂管理员/工厂用户，拥有所属工厂数据权限
            //查询工厂信息
            if (judgeUserVo.getUser() != null && judgeUserVo.getUser().getFactoryId() != null) {
                Factory queryFactory = factoryDao.findByIdToBoard(judgeUserVo.getUser().getFactoryId());
                if (queryFactory != null) {
                    resultFactory.add(queryFactory);
                }
            }
        }
        return resultFactory;
    }

    /**
     * 根据登录人角色查询工厂状态
     *
     * @param userId
     * @param tenantId
     * @return
     */
    public List<Factory> findFactoryStatusByLoginRole(UUID userId, UUID tenantId, UUID factoryId) throws ThingsboardException {
        List<Factory> resultFactory = new ArrayList<>();
        if (factoryId != null && StringUtils.isNotEmpty(factoryId.toString())) {
            Factory queryFactory = factoryDao.findByIdToBoard(factoryId);
            if (queryFactory != null) {
                resultFactory.add(queryFactory);
            }
        } else {
            //查询登录人角色
            //查询登录人角色及所属工厂
            JudgeUserVo judgeUserVo = userRoleMenuSvc.decideUser(new UserId(userId));
            if (judgeUserVo != null && judgeUserVo.getTenantFlag() != null && judgeUserVo.getTenantFlag()) {
                //租户管理员/租户有菜单权限的用户，拥有全部数据权限
                resultFactory = factoryDao.findFactoryByTenantIdToBoard(tenantId);
            } else if (judgeUserVo != null && judgeUserVo.getFactoryManagementFlag() != null && judgeUserVo.getFactoryManagementFlag()) {
                //工厂管理员/工厂用户，拥有所属工厂数据权限
                //查询工厂信息
                if (judgeUserVo.getUser() != null && judgeUserVo.getUser().getFactoryId() != null) {
                    Factory queryFactory = factoryDao.findByIdToBoard(judgeUserVo.getUser().getFactoryId());
                    if (queryFactory != null) {
                        resultFactory.add(queryFactory);
                    }
                }
            }
        }
        //查询网关状态
        if (CollectionUtils.isNotEmpty(resultFactory)) {
            resultFactory.forEach(i -> {
                //工厂下网关的在线、离线状态。有一个在线视为正常，全部离线视为异常
                try {
                    i.setFactoryStatus(factoryDao.checkoutFactoryStatus(i.getId()));
                } catch (ThingsboardException e) {
                    new ThingsboardException("查询工厂在线报错", ThingsboardErrorCode.FAIL_VIOLATION);
                }
            });
        }

        return resultFactory;
    }

    /**
     * 根据名称查询
     *
     * @return
     */
    @Override
    public List<Factory> findByName(String name, UUID tenantId) {
        Factory queryFactory = new Factory();
        queryFactory.setTenantId(tenantId);
        queryFactory.setName(name);
        return factoryDao.findAllByCdn(queryFactory);
    }

    /**
     * 获取实体属性
     *
     * @param o
     */
    @Override
    public String[] getEntityAttributeList(Object o) {
        Map<String, String> fieldMap = new HashMap<>();
        Map<String, String> map = new HashMap();
        Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        boolean b = false;
        for (int i = 0; i < fields.length; i++) {
            // 除过fieldMap中的属性，其他属性都获取
            if (!fieldMap.containsValue(fields[i].getName())) {
//                Field field=clazz.getDeclaredField(fields[i].getName());
                boolean annotationPresent = fields[i].isAnnotationPresent(Column.class);
                if (annotationPresent) {
                    // 获取注解值
                    String name = fields[i].getAnnotation(Column.class).name();
                    map.put(name, fields[i].getName());
                }
            }
        }
        System.out.println(map);
        return null;
    }

    @Override
    public ListenableFuture<Factory> findFactoryByIdAsync(TenantId callerId, FactoryId factoryId) {
        log.trace("执行 findFactoryByIdAsync [{}]", factoryId);
        validateId(factoryId, INCORRECT_FACTORY_ID + factoryId);
        return factoryDao.findByIdAsync(callerId, factoryId.getId());
    }

    /**
     * 校验工厂下是否有网关（true-有，false-无）
     *
     * @param factoryId
     * @return
     * @throws ThingsboardException
     */
    @Override
    public Boolean checkFactoryHaveGateway(String factoryId) throws ThingsboardException {
        Device device = new Device();
        device.setFactoryId(UUID.fromString(factoryId));
        device.setOnlyGatewayFlag(true);
        if (CollectionUtils.isNotEmpty(deviceService.findDeviceListByCdn(device, null, null))) {
            return true;
        }
        return false;
    }

}
