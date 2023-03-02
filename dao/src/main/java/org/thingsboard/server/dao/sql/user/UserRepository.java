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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.model.sql.UserEntity;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * @author Valerii Sosliuk
 */
public interface UserRepository extends PagingAndSortingRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    UserEntity findByEmail(String email);

    @Query(value ="SELECT u FROM UserEntity u WHERE u.email = :email " )
    List<UserEntity> findByEmailList(@Param("email") String email);

    UserEntity  findByPhoneNumber(String phoneNumber);

    @Query(value ="SELECT u FROM UserEntity u WHERE u.phoneNumber = :phoneNumber " )
    List<UserEntity> findByPhoneNumberList(@Param("phoneNumber") String phoneNumber);


    @Query("SELECT u FROM UserEntity u WHERE u.tenantId = :tenantId " +
            "AND u.customerId = :customerId AND u.authority = :authority " +
            "AND LOWER(u.searchText) LIKE LOWER(CONCAT(:searchText, '%'))")
    Page<UserEntity> findUsersByAuthority(@Param("tenantId") UUID tenantId,
                                          @Param("customerId") UUID customerId,
                                          @Param("searchText") String searchText,
                                          @Param("authority") Authority authority,
                                          Pageable pageable);

    @Query(value = "select t.*  from  tb_user t  where    t.id  in (select  user_id  from  tb_user_menu_role rr  ,tb_tenant_sys_role re\n" +
            "                                              where  rr.tenant_sys_role_id  = re.id  and  re.tenant_id= :tenantId " +
            "                                                and re.role_code=:roleCode " +
            ") and  t.tenant_id= :tenantId  and (lower(t.search_text) like lower(( :searchText ||'%')) )",nativeQuery = true)
    Page<UserEntity> findTenantAdmins(@Param("tenantId") UUID tenantId,@Param("searchText") String searchText,@Param("roleCode") String roleCode,Pageable pageable);


    @Query(value = "select t.*  from  tb_user t  where    t.id  in (select  user_id  from  tb_user_menu_role rr  ,tb_tenant_sys_role re\n" +
            "                                              where  rr.tenant_sys_role_id  = re.id  and  re.tenant_id= :tenantId " +
            "                                                and re.role_code=:roleCode  and re.factory_id= :factoryId" +
            ") and  t.tenant_id= :tenantId  and t.factory_id = :factoryId  and ((t.user_code) like (( :userCode ||'%')) )  and ((t.user_name) like (( :userName ||'%')) )",nativeQuery = true)
    Page<UserEntity> findFactoryAdmins(@Param("tenantId") UUID tenantId,@Param("factoryId") UUID factoryId,@Param("userCode") String userCode,@Param("userName") String userName,@Param("roleCode") String roleCode,Pageable pageable);


    @Query("SELECT u FROM UserEntity u WHERE u.tenantId = :tenantId " +
            "AND LOWER(u.searchText) LIKE LOWER(CONCAT(:searchText, '%'))")
    Page<UserEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                    @Param("searchText") String searchText,
                                    Pageable pageable);

    Long countByTenantId(UUID tenantId);


    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE tb_user  set " +
            "phone_number = :#{#user.phoneNumber} ," +
            "active_status = :#{#user.activeStatus} ," +
            "user_name =  :#{#user.userName} ," +
            "user_creator =  :#{#user.userCreator} ," +
            "email =  :#{#user.email}   " +
            "  where id = :#{#user.uuidId} " , nativeQuery = true)
    int update(@Param("user") User user);



    @Query("select d.userCode  from UserEntity d where d.tenantId = :tenantId")
    List<String> findAllCodesByTenantId(@Param("tenantId") UUID tenantId);



    @Query("select d  from UserEntity d where d.tenantId = :tenantId and d.userLevel=3")
    List<UserEntity> queyrTenantManagement(@Param("tenantId") UUID tenantId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update UserEntity  d set d.operationType= :operationType where d.id= :userId")
    int updateOperationType(@Param("userId") UUID  userId,@Param("operationType") Integer  operationType);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update UserEntity  d set d.userLevel = :userLevel where d.id= :userId")
    int updateLevel(@Param("userId") UUID  userId,@Param("userLevel") Integer  userLevel);

}
