package org.thingsboard.server.dao.util.sql.jpa;



import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.util.GenericsUtils;
import org.thingsboard.server.dao.util.sql.BaseSqlDao;
import org.thingsboard.server.dao.util.sql.JpaQueryHelper;
import org.thingsboard.server.dao.util.sql.entity.TenantBaseEntity;
import org.thingsboard.server.dao.util.sql.jpa.repository.SortRowName;
import org.thingsboard.server.dao.util.sql.jpa.transform.NameTransform;


import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public abstract class BaseSQLServiceImpl<T extends TenantBaseEntity, ID extends Serializable, D extends BaseSqlDao<T, ID>> implements BaseSQLService<T, ID, D>  {
	private static Logger logger = LoggerFactory.getLogger(BaseSQLServiceImpl.class.getName());

	private Class<T> entityClass;
	private Class<D> daoClass;
	protected D dao;

	@Autowired
	private ApplicationContext applicationContext;

	@SuppressWarnings("unchecked")
	public BaseSQLServiceImpl(){
		entityClass = GenericsUtils.getSuperClassGenricType(this.getClass(),0);
		daoClass = GenericsUtils.getSuperClassGenricType(this.getClass(),2);
	}

	public D getDao() {
		return dao;
	}

	@PostConstruct
	private void initDao(){
		if(dao == null){
			dao = applicationContext.getBean(daoClass);
		}
	}



	@Override
	public T findById(ID id) {
		Optional<T> opt = dao.findById(id);
		return opt.isPresent() ? opt.get(): null;
	}

	@Override
	public List<T> findAllById(Iterable<ID> var1)
	{
		return  dao.findAllById(var1);
	}




	@Override
	@Transactional
	public T updateNonNull(ID id, T entity){
		UUID uuid = Uuids.timeBased();
		entity.setUpdatedTime(Uuids.unixTimestamp(uuid));
		return dao.updateNonNull(id, entity);
	}


	/**
	 * 自测有问题
	 * @param entity
	 */
	@Override
	@Transactional
	public void deleteByEntity( T entity){
		 dao.deleteByEntity( entity);
	}



	@Override
	public T save(T entity) {
		if (entity.getId() == null) {
			UUID uuid = Uuids.timeBased();
			entity.setId(uuid);
			entity.setCreatedTime(Uuids.unixTimestamp(uuid));
		}else {
			UUID uuid = Uuids.timeBased();
			entity.setUpdatedTime(Uuids.unixTimestamp(uuid));
		}
		return dao.save(entity);
	}

	@Override
	public void deleteById(ID id) {
		dao.deleteById(id);
	}



	@Override
	public List<T> findAll(Map<String, Object> queryParam) {
		return findAll(queryParam, Sort.unsorted());
	}


	@Override
	public List<T> findAll(Map<String, Object> queryParam, Sort sort) {
		if (sort == null) {
			return this.dao.findAll(JpaQueryHelper.createQueryByMap(queryParam, entityClass));
		} else {
			System.out.println("findAll:queryParam-->"+queryParam);
			return this.dao.findAll(JpaQueryHelper.createQueryByMap(queryParam, entityClass), sort);
		}
	}


	@Override
	public Page<T> findAll(Map<String, Object> queryParam, PageLink pageLink) {
		Page<T> list = this.dao.findAll(JpaQueryHelper.createQueryByMap(queryParam, entityClass),  DaoUtil.toPageable(pageLink));// jpa的分页查询
		return list;
	}

	@Override
	public <T> Page<T> querySql(String sql, Map<String, Object> param, Class<T> cls, Pageable pageable, NameTransform trans, boolean isNativeSql){
		return  this.dao.querySql(sql,param,cls,pageable,trans,isNativeSql);
	}

	public  <T> Page<T> querySql(String sql, Map<String, Object> param, Class<T> cls, Pageable pageable, SortRowName sortRowName){
		return  this.dao.querySql(sql,param,cls,pageable,NameTransform.UN_CHANGE,true,sortRowName);
	}


	@Override
	public <T> List<T> queryAllListSqlLocal(String sql,Map<String, Object> param, Class<T> cls)
	{
		return  this.dao.queryAllListSql(sql,param,cls,NameTransform.UN_CHANGE,true);
	}

	/**
	 *
	 * @param sql
	 * @param param
	 * @return
	 */
	@Override
	public Long queryContListSqlLocal(String sql,Map<String, Object> param)
	{
		return  this.dao.queryContListSqlLocal(sql,param,true);
	}























}
