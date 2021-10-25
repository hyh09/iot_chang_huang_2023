package org.thingsboard.server.dao.sql.role.service;

import org.springframework.stereotype.Service;
import org.thingsboard.server.dao.sql.role.dao.UserMenuRoleDao;
import org.thingsboard.server.dao.sql.role.entity.UserMenuRoleEntity;
import org.thingsboard.server.dao.util.sql.jpa.BaseSQLServiceImpl;

import java.util.List;
import java.util.UUID;

/**
 * 用户角色关系接口
 */
@Service
public class UserMenuRoleService extends BaseSQLServiceImpl<UserMenuRoleEntity, UUID, UserMenuRoleDao> {


    /**
     * 根据实体类的查询
     * @param serviceSummarySheet  实体对象
     * @return List<ServiceSummarySheetEntity> list对象
     * @throws Exception
     */
    public List<UserMenuRoleEntity> findAllByServiceSummarySheetEntity(UserMenuRoleEntity serviceSummarySheet) throws Exception
    {
        List<UserMenuRoleEntity> serviceSummarySheetlist = findAll( null);//找个实体转map的现成代码来凑下 fastjson方便
        return  serviceSummarySheetlist;
    }

}
