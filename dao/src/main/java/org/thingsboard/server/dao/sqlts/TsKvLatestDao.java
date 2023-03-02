package org.thingsboard.server.dao.sqlts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.dao.model.sqlts.dictionary.TsKvDictionary;
import org.thingsboard.server.dao.model.sqlts.latest.TsKvLatestEntity;
import org.thingsboard.server.dao.sqlts.dictionary.TsKvDictionaryRepository;
import org.thingsboard.server.dao.sqlts.latest.TsKvLatestRepository;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fwy
 * @date 2023/1/11 9:55
 */
@Slf4j
@Component
public class TsKvLatestDao {
    @Resource
    private TsKvLatestRepository tsKvLatestRepository;
    @Resource
    private TsKvDictionaryRepository tsKvDictionaryRepository;

    /**
     * 查询设备对应的switch属性值
     *
     * @param entityIds
     * @return k 设备id v 属性值
     */
    public Map<UUID, Long> getSwitch(List<UUID> entityIds) {
        //查询设备对应的switch状态
        Optional<TsKvDictionary> aSwitch = tsKvDictionaryRepository.findByKey("switch");
        if (aSwitch.isEmpty()) {
            return new HashMap<>();
        }
        TsKvDictionary tsKvDictionary = aSwitch.get();
        int keyId = tsKvDictionary.getKeyId();
        List<TsKvLatestEntity> allByKeyEquals = tsKvLatestRepository.findAllByKeyEqualsAAndEntityIdIn(keyId, entityIds);
        return allByKeyEquals.stream().filter(e -> e.getLongValue() != null).collect(Collectors.toMap(TsKvLatestEntity::getEntityId, TsKvLatestEntity::getLongValue, (v1, v2) -> v1));
    }
}
