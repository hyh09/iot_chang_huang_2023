package org.thingsboard.server.dao.hs.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.hs.HSConstants;

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

    /**
     * 转换为文件目录
     *
     * @param tenantId 租户Id
     */
    public Path toFileRootDir(TenantId tenantId) throws IOException {
        var path = Paths.get(location, HSConstants.FILE_STR, tenantId.toString());
        Files.createDirectories(path);
        return path;
    }

    /**
     * 转换为临时文件目录
     *
     * @param tenantId 租户Id
     */
    public Path toFileRootTempDir(TenantId tenantId) throws IOException {
        var path = Paths.get(location, HSConstants.FILE_STR, tenantId.toString(), HSConstants.TEMP_STR);
        Files.createDirectories(path);
        return path;
    }
}
