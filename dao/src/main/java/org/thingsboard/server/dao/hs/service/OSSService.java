package org.thingsboard.server.dao.hs.service;

import com.aliyun.oss.model.*;
import lombok.NonNull;
import org.thingsboard.server.dao.hs.entity.enums.FileScopeEnum;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * oss 接口
 *
 * @author wwj
 * @since 2021.11.26
 */
public interface OSSService {

    /**
     * 上传文件
     *
     * @param uuid        文件uuid
     * @param scope       范围
     * @param inputStream 输入流
     */
    void uploadFile(@NonNull UUID uuid, @NonNull FileScopeEnum scope, @NonNull InputStream inputStream) throws Exception;

    /**
     * 分片上传文件
     *
     * @param uuid        uuid
     * @param scope       范围
     * @param fileLength  文件长度
     * @param inputStream 输入流
     */
    void uploadMultipartFile(@NonNull UUID uuid, @NonNull FileScopeEnum scope, @NonNull long fileLength, @NonNull InputStream inputStream) throws Exception;

    /**
     * 下载文件
     *
     * @param uuid  uuid
     * @param scope 范围
     */
    InputStream downloadFile(@NonNull UUID uuid, @NonNull FileScopeEnum scope);

    /**
     * 生成签名URL
     *
     * @param uuid  uuid
     * @param scope 范围
     */
    URL generateUrl(@NonNull UUID uuid, @NonNull FileScopeEnum scope) throws Exception;

    /**
     * 删除文件
     *
     * @param uuid  文件uuid
     * @param scope 范围
     */
    void deleteFile(@NonNull UUID uuid, @NonNull FileScopeEnum scope) throws Exception;

    /**
     * 判断文件是否存在
     *
     * @param uuid  文件uuid
     * @param scope 范围
     */
    boolean isFileExist(@NonNull UUID uuid, @NonNull FileScopeEnum scope) throws Exception;

    /**
     * 列举全部文件
     */
    List<OSSObjectSummary> listFiles() throws Exception;

    /**
     * 列举某个范围内的全部文件
     *
     * @param scope 范围
     */
    List<OSSObjectSummary> listScopeFiles(@NonNull FileScopeEnum scope) throws Exception;
}
