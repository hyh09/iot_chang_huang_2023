package org.thingsboard.server.dao.sql.role.dao.kv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.dao.sql.role.dao.FindKeyIdByKeyNameSvc;
import org.thingsboard.server.dao.sqlts.BaseAbstractSqlTimeseriesDao;

/**
 * @Project Name: thingsboard
 * @File Name: FindKeyIdByKeyNameImpl
 * @Date: 2022/11/7 11:11
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@Service
public class FindKeyIdByKeyNameImpl extends BaseAbstractSqlTimeseriesDao implements FindKeyIdByKeyNameSvc {
    @Override
    public Integer getKeyIdByKeyName(String strKey) {
        return super.getOrSaveKeyId(strKey);
    }
}
