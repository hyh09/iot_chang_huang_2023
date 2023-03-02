package org.thingsboard.server.dao.util.sql.jpa;


import org.thingsboard.server.dao.util.sql.entity.TenantBaseEntity;

import java.io.Serializable;

public interface BaseService<T extends TenantBaseEntity,ID extends Serializable, D extends BaseDao<T, ID>> {
	
}
