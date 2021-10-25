package org.thingsboard.server.dao.util.sql.jpa;


import org.thingsboard.server.dao.util.sql.entity.BaseEntity;

import java.io.Serializable;

public interface BaseService<T extends BaseEntity,ID extends Serializable, D extends BaseDao<T, ID>> {
	
}
