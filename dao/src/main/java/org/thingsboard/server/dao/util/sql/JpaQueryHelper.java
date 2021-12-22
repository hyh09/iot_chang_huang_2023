package org.thingsboard.server.dao.util.sql;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.dao.util.ReflectionUtils;
import org.thingsboard.server.dao.util.anno.JpaOperatorsType;

import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
public class JpaQueryHelper {
	
	@SuppressWarnings({"rawtypes","unchecked"})
	public static enum Operators {
		// 等于
		eq{
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.equal(root.get(fn), valueObj);
			}
		},
		// 不等
		ne{
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.notEqual(root.get(fn), valueObj);
			}
		},
		// 开始于 以xxx开头
		bw{
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.like(root.get(fn), (String) valueObj + "%");
			}
		}, 
		 // 不开始于 不以xxx开头
		bn{
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.notLike(root.get(fn), (String) valueObj + "%");
			}
		},
		// 结束于 以xxx结尾
		ew{
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.like(root.get(fn), "%" + (String) valueObj);
			}
		}, 
		// 不结束于 不以xxx结尾
		en{
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.notLike(root.get(fn), "%" + (String) valueObj);
			}
		}, 
		// 包含
		cn{
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.like(root.get(fn), "%" + (String) valueObj + "%");
			}
		}, 
		// 不包含
		nc{
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.notLike(root.get(fn), "%" + (String) valueObj + "%");
			}
		}, 
		// like 等同于 cn 包含
		lk{
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.like(root.get(fn), "%" + (String) valueObj + "%");
			}
		},
		// not like 等同于 nc 不包含
		nlk {
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.notLike(root.get(fn), "%" + (String) valueObj + "%");
			}
		},
		 // 空值于
		nu {
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.isNull(root.get(fn));
			}
		},
		// 非空值
		nn {
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.isNotNull(root.get(fn));
			}
		},
		// 属于 支持String与List 
		in {
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				List<Object> values = new ArrayList<Object>();
				if(valueObj instanceof String){
					values = Arrays.asList(valueObj.toString().split(","));
				} else if( valueObj instanceof List){
					values = (List<Object>) valueObj;
				}
				if(!values.isEmpty()){
					return root.get(fn).in(values);
				} else {
					return null;
				}
			}
		}, 
		// 不属于
		ni {
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				List<Object> values = new ArrayList<Object>();
				if(valueObj instanceof String){
					values = Arrays.asList(valueObj.toString().split(","));
				} else if( valueObj instanceof List){
					values = (List<Object>) valueObj;
				}
				if(!values.isEmpty()){
					return root.get(fn).in(values).not();
				} else {
					return null;
				}
			}
		}, 
		// 小于
		lt {
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.lessThan(root.get(fn), (Comparable) valueObj);
			}
		}, 
		// 小于等于
		lte {
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.lessThanOrEqualTo(root.get(fn), (Comparable) valueObj);
			}
		}, 
		// 大于
		gt {
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.greaterThan(root.get(fn), (Comparable) valueObj);
			}
		}, 
		// 大于等于
		gte {
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.greaterThanOrEqualTo(root.get(fn), (Comparable) valueObj);
			}
		}, 
		// 为空
		ep {
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.isEmpty(root.get(fn));
			}
		}, 
		// 不为空
		nep {
			@Override
			public Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj) {
				return cb.isNotEmpty(root.get(fn));
			}
		}; 
		
		public abstract Predicate buildPredicate(CriteriaBuilder cb, Root root, String fn, Object valueObj );
	}




	public static <T> Specification<T> createQueryByMap(Map<String, Object> queryParam, Class<T> cls){
		Specification<T> spec = new Specification<T>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Class<T> entityCls =  (Class<T>) root.getJavaType();
				Field[] entityFields = ReflectionUtils.getAccessibleField(entityCls);
				List<Predicate> pList = new ArrayList<Predicate>();

				Predicate p = null;
				Field idField = null;
				for(Field f : entityFields){
					log.info("==打印的===>:{},{},{},{}",f.getType(),f.getName(), queryParam.get(f.getName()),( queryParam.get(f.getName()) instanceof  UUID));
					if(f.getAnnotation(Id.class) != null){
						idField = f;
					}
					if(queryParam.containsKey(f.getName())){  //如果入参的 key中存在 当前类的属性
						Object value = queryParam.get(f.getName()); //当前入参key对应的value (key 要在类的属性中存在)
						if(value != null) {
							if (value.getClass().isArray()) {  //判断当前的value 的类型是不是集合
								CriteriaBuilder.In in = cb.in(root.get(f.getName()));
								Object[] vs = (Object[]) value;
								for(Object o : vs){
									in.value(o);
								}
								pList.add(in);
							} else if(value instanceof Collection){
								CriteriaBuilder.In in = cb.in(root.get(f.getName()));
								List<?> vs = (List<?>) value;
								for(Object o : vs){
									in.value(o);
								}
								pList.add(in);
							}else if(f.getType().isAssignableFrom(UUID.class) ){
								if(value instanceof  UUID ){
									pList.add(cb.equal(root.get(f.getName()).as(UUID.class), value));

								}
								if(value instanceof  String ){

									pList.add(cb.equal(root.get(f.getName()).as(String.class), value));
								}
							}
							else  if(f.getAnnotation(JpaOperatorsType.class) != null)
							{
						  		  log.info("==================valueJpaOperatorsType======================={}", value);
						  		  JpaOperatorsType jpaOperatorsType = f.getAnnotation(JpaOperatorsType.class);
						  		  pList.add(jpaOperatorsType.value().buildPredicate(cb, root, f.getName(), value));
							}

							else if (f.getType().isAssignableFrom(String.class) && f.getAnnotation(Id.class) == null) {
								if(StringUtils.isNotEmpty((String) value) && !value.equals("0")) {  //
									pList.add(cb.like(root.get(f.getName()).as(String.class), "%" + value + "%"));
								}
							}else  if(f.getType().isAssignableFrom(long.class) )
							{
                                if(value instanceof  Long){
									long l = ((Long) value).longValue();
									if(l>0){
										pList.add(cb.equal(root.get(f.getName()), value));
									}
								}

							}else  if(f.getType().isAssignableFrom(int.class))
							{
								log.info("打印当前得数据:{},====xingjiade==={}",f.getType(),value);
								pList.add(cb.equal(root.get(f.getName()), value));
							}
							else {
								pList.add(cb.equal(root.get(f.getName()), value));
							}
						}
					}
				}
				if(idField != null && queryParam.containsKey("notId") ){
					Object idObjs = queryParam.get("notId");
					List<Object> ids = new ArrayList<Object>();
					if(idObjs instanceof String){
						ids = Arrays.asList(idObjs.toString().split(","));
					} else if( idObjs instanceof List){
						ids = (List<Object>) idObjs;
					}
					if(!ids.isEmpty()){
						pList.add(root.get(idField.getName()).in(ids).not());
					}
				}


				if(idField != null && queryParam.containsKey("idlist") ){
					Object idObjs = queryParam.get("idlist");
					List<Object> ids = new ArrayList<Object>();
					if(idObjs instanceof String){
						ids = Arrays.asList(idObjs.toString().split(","));
					} else if( idObjs instanceof List){
						ids = (List<Object>) idObjs;
					}
					if(!ids.isEmpty()){
						pList.add(root.get(idField.getName()).in(ids));
					}
				}

				Predicate[] pArr = new Predicate[pList.size()];
				p = cb.and(pList.toArray(pArr));

				return p;
			}
		};
		return spec;
	}



	public static <T> Specification<T> createQueryDeviceByMap(Map<String, Object> queryParam, Class<T> cls){
		Specification<T> spec = new Specification<T>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Class<T> entityCls =  (Class<T>) root.getJavaType();
				Field[] entityFields = ReflectionUtils.getAccessibleField(entityCls);
				List<Predicate> pList = new ArrayList<Predicate>();
				pList.add(cb.or(cb.isNull(root.<String>get("additionalInfo")), cb.equal(cb.locate(root.<String>get("additionalInfo"), "\"gateway\":true"), 0)));
				Predicate p = null;
				Field idField = null;
				for(Field f : entityFields){
					log.info("==打印的===>:{},{},{},{}",f.getType(),f.getName(), queryParam.get(f.getName()),( queryParam.get(f.getName()) instanceof  UUID));
					if(f.getAnnotation(Id.class) != null){
						idField = f;
					}
					if(queryParam.containsKey(f.getName())){  //如果入参的 key中存在 当前类的属性
						Object value = queryParam.get(f.getName()); //当前入参key对应的value (key 要在类的属性中存在)
						if(value != null) {
							if (value.getClass().isArray()) {  //判断当前的value 的类型是不是集合
								CriteriaBuilder.In in = cb.in(root.get(f.getName()));
								Object[] vs = (Object[]) value;
								for(Object o : vs){
									in.value(o);
								}
								pList.add(in);
							} else if(value instanceof Collection){
								CriteriaBuilder.In in = cb.in(root.get(f.getName()));
								List<?> vs = (List<?>) value;
								for(Object o : vs){
									in.value(o);
								}
								pList.add(in);
							}else if(f.getType().isAssignableFrom(UUID.class) ){
								if(value instanceof  UUID ){
									pList.add(cb.equal(root.get(f.getName()).as(UUID.class), value));

								}
								if(value instanceof  String ){

									pList.add(cb.equal(root.get(f.getName()).as(String.class), value));
								}
							}
							else  if(f.getAnnotation(JpaOperatorsType.class) != null)
							{
								log.info("==================valueJpaOperatorsType======================={}", value);
								JpaOperatorsType jpaOperatorsType = f.getAnnotation(JpaOperatorsType.class);
								pList.add(jpaOperatorsType.value().buildPredicate(cb, root, f.getName(), value));
							}

							else if (f.getType().isAssignableFrom(String.class) && f.getAnnotation(Id.class) == null) {
								if(StringUtils.isNotEmpty((String) value) && !value.equals("0")) {  //
									pList.add(cb.like(root.get(f.getName()).as(String.class), "%" + value + "%"));
								}
							}else  if(f.getType().isAssignableFrom(long.class) )
							{
								if(value instanceof  Long){
									long l = ((Long) value).longValue();
									if(l>0){
										pList.add(cb.equal(root.get(f.getName()), value));
									}
								}

							}else  if(f.getType().isAssignableFrom(int.class))
							{
								log.info("打印当前得数据:{},====xingjiade==={}",f.getType(),value);
								pList.add(cb.equal(root.get(f.getName()), value));
							}
							else {
								pList.add(cb.equal(root.get(f.getName()), value));
							}
						}
					}
				}
				if(idField != null && queryParam.containsKey("notId") ){
					Object idObjs = queryParam.get("notId");
					List<Object> ids = new ArrayList<Object>();
					if(idObjs instanceof String){
						ids = Arrays.asList(idObjs.toString().split(","));
					} else if( idObjs instanceof List){
						ids = (List<Object>) idObjs;
					}
					if(!ids.isEmpty()){
						pList.add(root.get(idField.getName()).in(ids).not());
					}
				}


				if(idField != null && queryParam.containsKey("idlist") ){
					Object idObjs = queryParam.get("idlist");
					List<Object> ids = new ArrayList<Object>();
					if(idObjs instanceof String){
						ids = Arrays.asList(idObjs.toString().split(","));
					} else if( idObjs instanceof List){
						ids = (List<Object>) idObjs;
					}
					if(!ids.isEmpty()){
						pList.add(root.get(idField.getName()).in(ids));
					}
				}

				Predicate[] pArr = new Predicate[pList.size()];
				p = cb.and(pList.toArray(pArr));

				return p;
			}
		};
		return spec;
	}




}
