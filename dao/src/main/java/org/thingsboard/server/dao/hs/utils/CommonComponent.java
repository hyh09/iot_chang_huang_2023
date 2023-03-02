package org.thingsboard.server.dao.hs.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.service.ClientService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 工具类
 *
 * @author wwj
 * @since 2021.10.21
 */
@Component
public class CommonComponent {
    @Value("${hs.file.location}")
    private String location;

    @Value("${database.ts_latest.type:sql}")
    @Getter
    private String sqlType;

    @Autowired
    private ClientService clientService;

    /**
     * 是否cassandra持久
     */
    public boolean isPersistToCassandra() {
        return this.getSqlType().toLowerCase().contains("cassandra");
    }


    /**
     * 转换为文件目录
     *
     * @param tenantId 租户Id
     */
    public Path toFileRootDir(TenantId tenantId) throws IOException {
        var path = Paths.get(location, tenantId.toString(), String.valueOf(CommonUtil.getTodayCurrentTime()));
        Files.createDirectories(path);
        return path;
    }

    /**
     * 转换为临时文件目录
     *
     * @param tenantId 租户Id
     */
    public Path toFileRootTempDir(TenantId tenantId) throws IOException {
        var path = Paths.get(location, tenantId.toString(), HSConstants.TEMP_STR);
        Files.createDirectories(path);
        return path;
    }
}
