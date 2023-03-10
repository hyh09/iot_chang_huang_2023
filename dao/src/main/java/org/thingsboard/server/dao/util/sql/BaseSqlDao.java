package org.thingsboard.server.dao.util.sql;


import org.thingsboard.server.dao.util.sql.entity.TenantBaseEntity;
import org.thingsboard.server.dao.util.sql.jpa.BaseDao;
import org.thingsboard.server.dao.util.sql.jpa.BaseRepository;

import java.io.Serializable;

/**
 * 自定义的JPA的基础dao
 * @param <T>
 * @param <ID>
 */
public interface BaseSqlDao<T extends TenantBaseEntity, ID extends Serializable> extends BaseRepository<T, ID>, BaseDao<T,ID> {

}
