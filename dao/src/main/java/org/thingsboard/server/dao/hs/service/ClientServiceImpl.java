package org.thingsboard.server.dao.hs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.dao.entity.AbstractEntityService;

/**
 * 二方库接口实现类
 *
 * @author wwj
 * @since 2021.11.1
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class ClientServiceImpl extends AbstractEntityService implements ClientService {
}
