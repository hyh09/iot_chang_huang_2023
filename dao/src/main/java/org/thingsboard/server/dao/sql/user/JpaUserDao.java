/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.sql.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.vo.enums.RoleEnums;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.UserEntity;
import org.thingsboard.server.dao.sql.JpaAbstractSearchTextDao;
import org.thingsboard.server.dao.user.UserDao;
import org.thingsboard.server.dao.util.sql.JpaQueryHelper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;

/**
 * @author Valerii Sosliuk
 */
@Component
public class JpaUserDao extends JpaAbstractSearchTextDao<UserEntity, User> implements UserDao {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected Class<UserEntity> getEntityClass() {
        return UserEntity.class;
    }

    @Override
    protected CrudRepository<UserEntity, UUID> getCrudRepository() {
        return userRepository;
    }




    @Override
    public int update(User user) {
         return  userRepository.update(user);
    }

    @Override
    public User findByEmail(TenantId tenantId, String email) {
        return DaoUtil.getData(userRepository.findByEmail(email));
    }


    /**
     *
     * @param phoneNumber
     * @return
     */
    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return DaoUtil.getData(userRepository.findByPhoneNumber(phoneNumber));
    }

    @Override
    public PageData<User> findByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                userRepository
                        .findByTenantId(
                                tenantId,
                                Objects.toString(pageLink.getTextSearch(), ""),
                                DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<User> findTenantAdmins(UUID tenantId, PageLink pageLink) {
        Page<UserEntity>  userEntities =  userRepository.findTenantAdmins(tenantId,Objects.toString(pageLink.getTextSearch(), ""),
                RoleEnums.TENANT_ADMIN.getRoleCode(),DaoUtil.toPageable(pageLink));
        return  DaoUtil.toPageData(userEntities);
//        return DaoUtil.toPageData(
//                userRepository
//                        .findUsersByAuthority(
//                                tenantId,
//                                NULL_UUID,
//                                Objects.toString(pageLink.getTextSearch(), ""),
//                                Authority.TENANT_ADMIN,
//                                DaoUtil.toPageable(pageLink)));
    }


    @Override
    public PageData<User> findFactoryAdmins(UUID tenantId, UUID factoryId, String userCode, String userName, PageLink pageLink) {
        Page<UserEntity>  userEntities =  userRepository.findFactoryAdmins(tenantId,factoryId,userCode,userName,
                RoleEnums.FACTORY_ADMINISTRATOR.getRoleCode(),DaoUtil.toPageable(pageLink));
        return  DaoUtil.toPageData(userEntities);
    }

    @Override
    public PageData<User> findCustomerUsers(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(
                userRepository
                        .findUsersByAuthority(
                                tenantId,
                                customerId,
                                Objects.toString(pageLink.getTextSearch(), ""),
                                Authority.CUSTOMER_USER,
                                DaoUtil.toPageable(pageLink)));

    }

    @Override
    public Long countByTenantId(TenantId tenantId) {
        return userRepository.countByTenantId(tenantId.getId());
    }

    @Override
    public PageData<User> findAll(Map<String, Object> queryParam, PageLink pageLink) {
        Page<UserEntity> list = this.userRepository.findAll(JpaQueryHelper.createQueryByMap(queryParam,UserEntity.class ),  DaoUtil.toPageable(pageLink));
        System.out.println("打印当前的数据:"+list);
        return  DaoUtil.toPageData(list);
    }

    @Override
    public List<User> findAll(Map<String, Object> queryParam) {
        List<UserEntity> list = this.userRepository.findAll(JpaQueryHelper.createQueryByMap(queryParam,UserEntity.class ));
        System.out.println("打印当前的数据:"+list);
        return list.stream().map(DaoUtil::getData).collect(Collectors.toList());
    }


    public  List<String> findAllCodesByTenantId( UUID tenantId)
    {
        return this.userRepository.findAllCodesByTenantId(tenantId);
    }


}
