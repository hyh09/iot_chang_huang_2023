package org.thingsboard.server.dao.util.sql.jpa;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.util.sql.BaseSqlDao;
import org.thingsboard.server.dao.util.sql.entity.TenantBaseEntity;
import org.thingsboard.server.dao.util.sql.jpa.transform.NameTransform;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 通用jpa的常用方法
 * @param <T>
 * @param <ID>
 * @param <D>
 */
public interface BaseSQLService<T extends TenantBaseEntity, ID extends Serializable, D extends BaseSqlDao<T, ID>> extends BaseService<T, ID, D>{

	T save(T record);

	void deleteById(ID id);


	/**  主键的查询 */
	T findById(ID id);

	T updateNonNull(ID id, T entity);

	List<T> findAll(Map<String, Object>  queryParam);

   //带排序的查询
	 List<T> findAll(Map<String, Object> queryParam, Sort sort);

	Page<T> findAll(Map<String, Object> queryParam, PageLink pageLink);

	/**
	 *
	 * @param sql
	 * @param param
	 * @param cls
	 * @param pageable
	 * @param trans
	 * @param isNativeSql
	 * @param <T>
	 * @return
	 */
	 <T> Page<T> querySql(String sql, Map<String, Object> param, Class<T> cls, Pageable pageable, NameTransform trans, boolean isNativeSql);


	 //

}
