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

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.thingsboard.server.dao.model.sql.UserCredentialsEntity;

import javax.transaction.Transactional;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 4/22/2017.
 */
public interface UserCredentialsRepository extends CrudRepository<UserCredentialsEntity, UUID> {

    UserCredentialsEntity findByUserId(UUID userId);

    UserCredentialsEntity findByActivateToken(String activateToken);

    UserCredentialsEntity findByResetToken(String resetToken);

    @Transactional
    @Modifying(clearAutomatically = true)
   @Query("update UserCredentialsEntity m set m.password=?2 where  m.userId=?1")
    int  updatePassword(UUID userId, String password);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update UserCredentialsEntity m set m.enabled=?2 where  m.userId=?1")
    int  updateEnableByUserId(UUID userId,boolean enabled);



}
