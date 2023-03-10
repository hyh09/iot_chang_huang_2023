/**
 * Copyright © 2016-2021 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.service.install;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thingsboard.server.dao.util.PsqlDao;

@Service
@PsqlDao
@Profile("install")
@Slf4j
public class PsqlEntityDatabaseSchemaService extends SqlAbstractDatabaseSchemaService
        implements EntityDatabaseSchemaService {
    public static final String SCHEMA_ENTITIES_SQL = "schema-entities.sql";
    public static final String SCHEMA_ENTITIES_IDX_SQL = "schema-entities-idx.sql";
    public static final String SCHEMA_ENTITIES_IDX_PSQL_ADDON_SQL = "schema-entities-idx-psql-addon.sql";
    public static final String Test001 = "1.0.0.sql";

    public PsqlEntityDatabaseSchemaService() {
        super(SCHEMA_ENTITIES_SQL, SCHEMA_ENTITIES_IDX_SQL);
    }

    @Override
    public void createDatabaseIndexes() throws Exception {
        super.createDatabaseIndexes();
        log.info("Installing SQL DataBase schema PostgreSQL specific indexes part: " + SCHEMA_ENTITIES_IDX_PSQL_ADDON_SQL);
        executeQueryFromFile(SCHEMA_ENTITIES_IDX_PSQL_ADDON_SQL);
        //   log.info("Installing SQL DataBase schema PostgreSQL specific Test001 part: " + Test001);
        //  executeQueryFromFile(Test001);


    }

}
