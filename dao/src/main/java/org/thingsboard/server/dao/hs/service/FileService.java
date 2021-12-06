package org.thingsboard.server.dao.hs.service;


import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.dao.hs.entity.enums.FileScopeEnum;
import org.thingsboard.server.dao.hs.entity.po.FileInfo;
import org.thingsboard.server.dao.hs.entity.vo.FileInfoDictDeviceModelVO;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 文件接口
 *
 * @author wwj
 * @since 2021.10.18
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param tenantId          租户Id
     * @param checksum          校验和
     * @param checksumAlgorithm 校验和算法
     * @param file              文件
     * @return 文件Id
     */
    String uploadFile(TenantId tenantId, String checksum, ChecksumAlgorithm checksumAlgorithm, MultipartFile file) throws IOException, ThingsboardException;

    /**
     * 获得文件详情
     *
     * @param tenantId 租户Id
     * @param id       文件Id
     * @return 文件详情
     */
    FileInfo getFileInfo(TenantId tenantId, String id) throws ThingsboardException;

    /**
     * 获得文件详情,不校验是否存在
     *
     * @param tenantId 租户Id
     * @param id       文件Id
     * @return 文件详情
     */
    FileInfo getFileInfoNotCheckExist(TenantId tenantId, String id) throws ThingsboardException;

    /**
     * 删除文件
     *
     * @param tenantId 租户Id
     * @param id       文件Id
     */
    void deleteFile(TenantId tenantId, String id) throws IOException, ThingsboardException;

    /**
     * 删除文件, 不验证租户
     *
     * @param id 文件Id
     */
    void deleteFile(String id) throws IOException, ThingsboardException;

    /**
     * 分片上传合并文件
     *
     * @param tenantId          租户Id
     * @param guid              文件上传id
     * @param checksum          检验和
     * @param checksumAlgorithm 算法
     * @param chunks            分片数量
     * @param fileName          文件名
     * @return 文件Id
     */
    String uploadMultiPartFile(TenantId tenantId, String guid, String checksum, ChecksumAlgorithm checksumAlgorithm, int chunks, String fileName) throws IOException, ThingsboardException;

    /**
     * 取消分片上传文件
     *
     * @param tenantId 租户Id
     * @param guid     文件上传id
     * @param chunks   分片数量
     */
    void cancelUploadMultiPartFile(TenantId tenantId, String guid, int chunks) throws IOException;

    /**
     * 文件范围实体信息完善
     *
     * @param tenantId  租户Id
     * @param fileId    文件id
     * @param scopeEnum 范围
     * @param entityId  实体Id
     */
    void updateFileScope(TenantId tenantId, UUID fileId, FileScopeEnum scopeEnum, UUID entityId) throws ThingsboardException;

    /**
     * 按范围和实体Id查询文件
     *
     * @param tenantId  租户Id
     * @param scopeEnum 范围
     * @param entityId  实体Id
     */
    FileInfo getFileInfoByScopeAndEntityId(TenantId tenantId, FileScopeEnum scopeEnum, UUID entityId);

    /**
     * 按范围和实体Id查询文件列表
     *
     * @param tenantId  租户Id
     * @param scopeEnum 范围
     * @param entityId  实体Id
     */
    List<FileInfo> listFileInfosByScopeAndEntityId(TenantId tenantId, FileScopeEnum scopeEnum, UUID entityId);

    /**
     * 按范围查询文件列表
     *
     * @param tenantId  租户Id
     * @param scopeEnum 范围
     */
    List<FileInfo> listFileInfosByScope(TenantId tenantId, FileScopeEnum scopeEnum);

    /**
     * 删除文件按范围和实体Id
     *
     * @param tenantId  租户Id
     * @param scopeEnum 范围
     * @param entityId  实体Id
     */
    void deleteFilesByScopeAndEntityId(TenantId tenantId, FileScopeEnum scopeEnum, UUID entityId);

    /**
     * 获得模型库列表
     *
     * @param tenantId  租户Id
     * @param scopeEnum 范围
     * @return 模型库列表
     */
    List<FileInfoDictDeviceModelVO> listModels(TenantId tenantId, FileScopeEnum scopeEnum);
}
