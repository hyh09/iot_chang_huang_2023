package org.thingsboard.server.dao.util.sql.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.stereotype.Component;
import org.thingsboard.server.dao.util.sql.jpa.transform.NameTransform;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Map;

/**
 * 基于jpa的数据库操作访问接口
 * @author Lee
 *
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T>, BaseDao<T, ID>{

    boolean support(String modelType);

     EntityManager getEntityManager();

    Class<T> getDomainClass();

    RepositoryInformation getInformation();


    public T updateNonNull(ID id, T entity);

    void deleteByEntity( T entity);

    public <T> Page<T> querySql(String sql, Map<String, Object> param, Class<T> cls, Pageable pageable, NameTransform trans, boolean isNativeSql);


}
