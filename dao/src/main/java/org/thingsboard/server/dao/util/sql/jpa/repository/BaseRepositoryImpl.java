package org.thingsboard.server.dao.util.sql.jpa.repository;


import org.hibernate.SQLQuery;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.dao.util.CommonUtils;
import org.thingsboard.server.dao.util.sql.jpa.BaseRepository;
import org.thingsboard.server.dao.util.sql.jpa.transform.CustomResultToBean;
import org.thingsboard.server.dao.util.sql.jpa.transform.CustomResultToMap;
import org.thingsboard.server.dao.util.sql.jpa.transform.NameTransform;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 有空再看把， EntityManager 这个这次使用报错
 * @param <T>
 * @param <ID>
 */
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private RepositoryInformation information;
	
	private Class<T> domainClass;

	@PersistenceContext
	private EntityManager entityManager;
	
	public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
		this.domainClass = entityInformation.getJavaType();
	}

	@Override
	public Class<T> getDomainClass() {
		return domainClass;
	}

	public BaseRepositoryImpl(RepositoryInformation infomation, Class<T> domainClass, EntityManager entityManager) {
		super(domainClass, entityManager);
		this.domainClass = domainClass;
		this.entityManager = entityManager;
		this.information = information;
	}

	@Override
	public boolean support(String modelType) {
		return domainClass.getName().equals(modelType);
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}


	@Override
	public RepositoryInformation getInformation() {
		return this.information;
	}



	@Override
	public T updateNonNull(ID id, T entity) {
		Optional<T> opt = findById(id);
		T e = opt.isPresent() ? opt.get(): null;
		if(e == null){
			return null;
		}
		CommonUtils.copyNonNullProperties(entity, e);
		logger.info("打印当前的=====："+e);
		entityManager.flush();
     	entityManager.setFlushMode(FlushModeType.COMMIT);
		return e;
	}

	/**
	 * 有问题;
	 * @param entity
	 */
	@Transactional
	public void deleteByEntity(T entity) {
		entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
	}





	public <T> Page<T> querySql(String sql, Map<String, Object> param, Class<T> cls, Pageable pageable, NameTransform trans, boolean isNativeSql) {
		boolean enablePage = false;
		if(pageable != null ){
			if(pageable.getPageSize() != Integer.MAX_VALUE){
				enablePage = true;
			}
		}
		String sqlCount = "select count(*) from (" + sql + ") t_count_0";
		Query countQuery = null;
		Query query = null;
		if(isNativeSql){
			countQuery = entityManager.createNativeQuery(sqlCount).unwrap(NativeQuery.class);

			query = entityManager.createNativeQuery(sql).unwrap(NativeQuery.class);
			if(Map.class.isAssignableFrom(cls)){
				query.unwrap(NativeQueryImpl.class).setResultTransformer(new CustomResultToMap(trans));
			} else {
				query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
//				query.unwrap(NativeQueryImpl.class).setResultTransformer(new CustomResultToBean(cls, trans));
			}
		} else {
			countQuery = entityManager.createQuery(sqlCount).unwrap(Query.class);
			query = entityManager.createQuery(sql).unwrap(Query.class);
		}

		if(param!= null){
			for(Map.Entry<String, ?> entry : param.entrySet()){
				if(sql.indexOf(":" + entry.getKey()) > -1 ){
					countQuery.setParameter(entry.getKey(), entry.getValue());
					query.setParameter(entry.getKey(), entry.getValue());
				}
			}
		}

		Long totalObj = 0l;
		if(enablePage){
			totalObj = Long.parseLong(countQuery.getSingleResult().toString());
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize() ).setMaxResults(pageable.getPageSize());
		}

//		List<T> list = query.getResultList();
		List<T> list = query.getResultList();

		Page<T> page = new PageImpl<>(list, pageable, totalObj);
		//entityManager由entityManagerFactory统一管理，无需显示关闭
		//entityManager.clear();
		//entityManager.close();
		return page;
	}


    public <T> List<T> queryAllListSql(String sql,Map<String, Object> param, Class<T> cls,NameTransform trans, boolean isNativeSql)
	{
		Query query = null;
		if(isNativeSql){

			query = entityManager.createNativeQuery(sql).unwrap(NativeQuery.class);
			if(Map.class.isAssignableFrom(cls)){
				query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

//				query.unwrap(NativeQueryImpl.class).setResultTransformer(new CustomResultToMap(trans));
			} else {
				query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
//				query.unwrap(NativeQueryImpl.class).setResultTransformer(new CustomResultToBean(cls, trans));
			}
		} else {
			query = entityManager.createQuery(sql).unwrap(Query.class);
		}
		if(param!= null){
			for(Map.Entry<String, ?> entry : param.entrySet()){
				if(sql.indexOf(":" + entry.getKey()) > -1 ){
					query.setParameter(entry.getKey(), entry.getValue());
				}
			}
		}
		List<T> list = query.getResultList();
		return  list;


	}



	public Long queryContListSqlLocal(String sqlCount, Map<String, Object> param, boolean isNativeSql) {

		Query countQuery = null;
		if(isNativeSql){
			countQuery = entityManager.createNativeQuery(sqlCount).unwrap(NativeQuery.class);

		} else {
			countQuery = entityManager.createQuery(sqlCount).unwrap(Query.class);
		}

		if(param!= null){
			for(Map.Entry<String, ?> entry : param.entrySet()){
				if(sqlCount.indexOf(":" + entry.getKey()) > -1 ){
					countQuery.setParameter(entry.getKey(), entry.getValue());
				}
			}
		}

		Long	totalObj = Long.parseLong(countQuery.getSingleResult().toString());
		return  totalObj;



	}






}