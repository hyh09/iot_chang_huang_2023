package org.thingsboard.server.dao.dingding.service.svc;

import org.thingsboard.server.common.data.kv.AttributeKvEntry;

import java.util.UUID;

public interface DdingDingSendMssSvc {

   void  send(UUID entityId, AttributeKvEntry entry);
}
