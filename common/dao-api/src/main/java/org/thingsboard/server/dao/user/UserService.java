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
package org.thingsboard.server.dao.user;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserCredentialsId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.common.data.vo.PasswordVo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserService {
	
	User findUserById(TenantId tenantId, UserId userId);

	ListenableFuture<User> findUserByIdAsync(TenantId tenantId, UserId userId);

	User findUserByEmail(TenantId tenantId, String email);

	User findByPhoneNumber(String phoneNumber);

	User saveUser(User user);

	User save(User user,String  encodePassword );

	int  update(User user);



	UserCredentials findUserCredentialsByUserId(TenantId tenantId, UserId userId);
	
	UserCredentials findUserCredentialsByActivateToken(TenantId tenantId, String activateToken);

	UserCredentials findUserCredentialsByResetToken(TenantId tenantId, String resetToken);

	UserCredentials saveUserCredentials(TenantId tenantId, UserCredentials userCredentials);
	
	UserCredentials activateUserCredentials(TenantId tenantId, String activateToken, String password);
	
	UserCredentials requestPasswordReset(TenantId tenantId, String email);

    UserCredentials requestExpiredPasswordReset(TenantId tenantId, UserCredentialsId userCredentialsId);

    UserCredentials replaceUserCredentials(TenantId tenantId, UserCredentials userCredentials);

	void deleteUser(TenantId tenantId, UserId userId);

    PageData<User> findUsersByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<User> findTenantAdmins(TenantId tenantId, PageLink pageLink);

	/**
	 * 查询工厂管理员
	 * @param tenantId
	 * @param factoryId
	 * @param userCode
	 * @param userName
	 * @return
	 */
	PageData<User> findFactoryAdmins(TenantId tenantId, UUID factoryId, String userCode, String userName,PageLink pageLink);
	
	void deleteTenantAdmins(TenantId tenantId);

    PageData<User> findCustomerUsers(TenantId tenantId, CustomerId customerId, PageLink pageLink);
	    
	void deleteCustomerUsers(TenantId tenantId, CustomerId customerId);

	void setUserCredentialsEnabled(TenantId tenantId, UserId userId, boolean enabled);

	void onUserLoginSuccessful(TenantId tenantId, UserId userId);

	int onUserLoginIncorrectCredentials(TenantId tenantId, UserId userId);

	PageData<User>   findAll(Map<String, Object> queryParam,PageLink pageLink);

	List<User> findAll(Map<String, Object> queryParam);

	public List<String> findAllCodesByTenantId(UUID tenantId);

	Object  changeOthersPassword(PasswordVo vo);


	 int updateEnableByUserId(UUID userId, boolean enabled);
}
