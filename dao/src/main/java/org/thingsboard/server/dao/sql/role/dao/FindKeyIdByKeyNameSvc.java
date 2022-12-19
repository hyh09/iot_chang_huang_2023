package org.thingsboard.server.dao.sql.role.dao;

/**
 * @Project Name: thingsboard
 * @File Name: FindKeyNameByKeyId
 * @Date: 2022/11/7 11:09
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
public interface FindKeyIdByKeyNameSvc {

     Integer getKeyIdByKeyName(String strKey);
}
