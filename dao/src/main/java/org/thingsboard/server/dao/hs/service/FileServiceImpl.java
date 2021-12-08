package org.thingsboard.server.dao.hs.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.common.util.ThingsBoardThreadFactory;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.hs.dao.DictDeviceEntity;
import org.thingsboard.server.dao.hs.dao.DictDeviceRepository;
import org.thingsboard.server.dao.hs.dao.FileEntity;
import org.thingsboard.server.dao.hs.dao.FileRepository;
import org.thingsboard.server.dao.hs.entity.enums.FileScopeEnum;
import org.thingsboard.server.dao.hs.entity.po.FileInfo;
import org.thingsboard.server.dao.hs.entity.vo.FileInfoDictDeviceModelVO;
import org.thingsboard.server.dao.hs.utils.CommonComponent;
import org.thingsboard.server.dao.hs.utils.CommonUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文件接口实现类
 *
 * @author wwj
 * @since 2021.10.18
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class FileServiceImpl extends AbstractEntityService implements FileService, CommonService {

    private FileRepository fileRepository;

    private CommonComponent commonComponent;

    private DictDeviceRepository dictDeviceRepository;

    ScheduledExecutorService service;

    @PostConstruct
    public void initExecutor() {
        service = Executors.newSingleThreadScheduledExecutor(ThingsBoardThreadFactory.forName("hs-file-cleaner"));
        service.scheduleAtFixedRate(() -> this.fileRepository.findAllUnusedFiles(CommonUtil.getYesterdayStartTime()).forEach(e -> {
            try {
                this.deleteFile(e.getId().toString());
                log.info("定时删除文件成功：【{}】", e.getId().toString());
            } catch (Exception ignore) {
                log.error("定时删除文件失败：【{}】", e.getId().toString());
            }
        }), 60, 60 * 60, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdownExecutor() {
        if (service != null) {
            service.shutdownNow();
        }
    }

    /**
     * 上传文件
     *
     * @param tenantId          租户Id
     * @param checksum          校验和
     * @param checksumAlgorithm 校验和算法
     * @param file              文件
     * @return 文件Id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(TenantId tenantId, String checksum, ChecksumAlgorithm checksumAlgorithm, MultipartFile file) throws IOException, ThingsboardException {
//        byte[] bytes = file.getBytes();
//        var calCheckSum = CommonUtil.generateChecksum(checksumAlgorithm, ByteBuffer.wrap(bytes));
//        if (StringUtils.isNotBlank(checksum) && !calCheckSum.equals(checksum))
//            throw new ThingsboardException("文件校验失败", ThingsboardErrorCode.GENERAL);

        var fileEntity = new FileEntity(FileInfo.builder()
                .checkSum(checksum)
                .contentType(file.getContentType())
                .fileName(file.getOriginalFilename())
                .dataSize(file.getSize())
                .checksumAlgorithm(checksumAlgorithm.toString())
                .tenantId(tenantId.toString())
                .build());
        this.fileRepository.save(fileEntity);

        var path = commonComponent.toFileRootDir(tenantId);
        var filePath = Paths.get(path.toString(), fileEntity.getId().toString());
        fileEntity.setLocation(filePath.toString());
        this.fileRepository.save(fileEntity);

        try (InputStream is = file.getInputStream(); OutputStream os = Files.newOutputStream(filePath)) {
            byte[] buffer = new byte[4096];
            int read = 0;
            while ((read = is.read(buffer)) > 0) {
                os.write(buffer, 0, read);
            }
        }

        return fileEntity.getId().toString();
    }

    /**
     * 获得文件详情
     *
     * @param tenantId 租户Id
     * @param id       文件Id
     * @return 文件详情
     */
    @Override
    public FileInfo getFileInfo(TenantId tenantId, String id) throws ThingsboardException {
        return this.fileRepository.findByTenantIdAndId(tenantId.getId(), toUUID(id)).map(FileEntity::toData)
                .orElseThrow(() -> new ThingsboardException("文件不存在！", ThingsboardErrorCode.GENERAL));
    }

    /**
     * 获得文件详情,不校验是否存在
     *
     * @param tenantId 租户Id
     * @param id       文件Id
     * @return 文件详情
     */
    @Override
    public FileInfo getFileInfoNotCheckExist(TenantId tenantId, String id) throws ThingsboardException {
        return this.fileRepository.findByTenantIdAndId(tenantId.getId(), toUUID(id)).map(FileEntity::toData)
                .orElse(null);
    }

    /**
     * 删除文件
     *
     * @param tenantId 租户Id
     * @param id       文件Id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(TenantId tenantId, String id) throws IOException, ThingsboardException {
        var fileInfo = this.fileRepository.findByTenantIdAndId(tenantId.getId(), toUUID(id)).map(FileEntity::toData)
                .orElseThrow(() -> new ThingsboardException("文件不存在！", ThingsboardErrorCode.GENERAL));
        this.fileRepository.deleteById(toUUID(id));
        Files.deleteIfExists(Paths.get(fileInfo.getLocation()));
    }

    /**
     * 删除文件, 不验证租户
     *
     * @param id 文件Id
     */
    @Override
    public void deleteFile(String id) throws IOException, ThingsboardException {
        var fileInfo = this.fileRepository.findById(toUUID(id)).map(FileEntity::toData)
                .orElseThrow(() -> new ThingsboardException("文件不存在！", ThingsboardErrorCode.GENERAL));
        this.fileRepository.deleteById(toUUID(id));
        Files.deleteIfExists(Paths.get(fileInfo.getLocation()));
    }

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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadMultiPartFile(TenantId tenantId, String guid, String checksum, ChecksumAlgorithm checksumAlgorithm, int chunks, String fileName) throws IOException, ThingsboardException {
        for (int i = 1; i <= chunks; i++) {
            var path = Paths.get(commonComponent.toFileRootTempDir(tenantId).toString(), guid, String.valueOf(i));
            if (!Files.exists(path))
                return null;
        }

        var fileEntity = new FileEntity(FileInfo.builder()
                .checkSum(checksum)
                .fileName(fileName)
                .checksumAlgorithm(checksumAlgorithm.toString())
                .tenantId(tenantId.toString())
                .build());
        this.fileRepository.save(fileEntity);
        var filePath = Paths.get(commonComponent.toFileRootDir(tenantId).toString(), fileEntity.getId().toString());
        fileEntity.setLocation(filePath.toString());
        this.fileRepository.save(fileEntity);

        Vector<InputStream> v = new Vector<>();
        SequenceInputStream sis = null;
        BufferedOutputStream bos = null;
        try {
            for (int i = 1; i <= chunks; i++) {
                var path = Paths.get(commonComponent.toFileRootTempDir(tenantId).toString(), guid, String.valueOf(i));
                var inputStream = Files.newInputStream(path);
                v.add(inputStream);
            }

            Enumeration<InputStream> e = v.elements();
            sis = new SequenceInputStream(e);
            bos = new BufferedOutputStream(new FileOutputStream(filePath.toString()));
            byte[] bys = new byte[4096];
            int len;
            while ((len = sis.read(bys)) != -1) {
                bos.write(bys, 0, len);
            }

            for (int i = 1; i <= chunks; i++) {
                var path = Paths.get(commonComponent.toFileRootTempDir(tenantId).toString(), guid, String.valueOf(i));
                Files.deleteIfExists(path);
            }

            return fileEntity.getId().toString();
        } catch (Exception ex) {
            throw new ThingsboardException(ex.getMessage(), ThingsboardErrorCode.GENERAL);
        } finally {
            if (sis != null)
                try {
                    sis.close();
                } catch (Exception ignore) {
                }
            if (bos != null)
                try {
                    bos.close();
                } catch (Exception ignore) {
                }
            if (!v.isEmpty()) {
                for (InputStream inputStream : v) {
                    try {
                        inputStream.close();
                    } catch (Exception ignore) {
                    }
                }
            }
        }
    }

    /**
     * 取消分片上传文件
     *
     * @param tenantId 租户Id
     * @param guid     文件上传id
     * @param chunks   分片数量
     */
    @Override
    public void cancelUploadMultiPartFile(TenantId tenantId, String guid, int chunks) throws IOException {
        for (int i = 1; i <= chunks; i++) {
            var path = Paths.get(commonComponent.toFileRootTempDir(tenantId).toString(), guid, String.valueOf(i));
            Files.deleteIfExists(path);
        }
    }

    /**
     * 文件信息完善
     *
     * @param tenantId  租户Id
     * @param fileId    文件id
     * @param scopeEnum 范围
     * @param entityId  实体Id
     */
    @Override
    @Transactional
    public void updateFileScope(TenantId tenantId, @NotNull UUID fileId, @NotNull FileScopeEnum scopeEnum, @NotNull UUID entityId) throws ThingsboardException {
        var fileEntity = this.fileRepository.findByTenantIdAndId(tenantId.getId(), fileId)
                .orElseThrow(() -> new ThingsboardException("文件不存在！", ThingsboardErrorCode.GENERAL));
        fileEntity.setScope(scopeEnum.getCode());
        fileEntity.setEntityId(entityId);
        this.fileRepository.save(fileEntity);
    }

    /**
     * 按范围和实体Id查询文件
     *
     * @param tenantId  租户Id
     * @param scopeEnum 范围
     * @param entityId  实体Id
     */
    @Override
    public FileInfo getFileInfoByScopeAndEntityId(@NotNull TenantId tenantId, @NotNull FileScopeEnum scopeEnum, @NotNull UUID entityId) {
        return this.fileRepository.findByTenantIdAndScopeAndEntityId(tenantId.getId(), scopeEnum.getCode(), entityId)
                .map(FileEntity::toData).orElse(null);
    }

    /**
     * 按范围和实体Id查询文件列表
     *
     * @param tenantId  租户Id
     * @param scopeEnum 范围
     * @param entityId  实体Id
     */
    @Override
    public List<FileInfo> listFileInfosByScopeAndEntityId(@NotNull TenantId tenantId, @NotNull FileScopeEnum scopeEnum, @NotNull UUID entityId) {
        return DaoUtil.convertDataList(this.fileRepository.findAllByTenantIdAndScopeAndEntityIdOrderByCreatedTimeDesc(tenantId.getId(), scopeEnum.getCode(), entityId));
    }

    /**
     * 按范围查询文件列表
     *
     * @param tenantId  租户Id
     * @param scopeEnum 范围
     */
    @Override
    public List<FileInfo> listFileInfosByScope(TenantId tenantId, FileScopeEnum scopeEnum) {
        return DaoUtil.convertDataList(this.fileRepository.findAllByTenantIdAndScopeOrderByCreatedTimeDesc(tenantId.getId(), scopeEnum.getCode()));
    }

    /**
     * 删除文件按范围和实体Id
     *
     * @param tenantId  租户Id
     * @param scopeEnum 范围
     * @param entityId  实体Id
     */
    @Override
    @Transactional
    public void deleteFilesByScopeAndEntityId(TenantId tenantId, FileScopeEnum scopeEnum, UUID entityId) {
        this.fileRepository.findAllByTenantIdAndScopeAndEntityIdOrderByCreatedTimeDesc(tenantId.getId(), scopeEnum.getCode(), entityId).forEach(e -> {
            try {
                Files.deleteIfExists(Paths.get(e.getLocation()));
            } catch (IOException ignore) {
            }
        });
        this.fileRepository.deleteAllByTenantIdAndScopeAndEntityId(tenantId.getId(), scopeEnum.getCode(), entityId);
    }

    /**
     * 获得模型库列表
     *
     * @param tenantId  租户Id
     * @param scopeEnum 范围
     * @return 模型库列表
     */
    @Override
    public List<FileInfoDictDeviceModelVO> listModels(TenantId tenantId, FileScopeEnum scopeEnum) {
        var fileInfoList = listFileInfosByScope(tenantId, scopeEnum);
        var map = this.dictDeviceRepository.findAllByTenantIdAndIdIn(tenantId.getId(), fileInfoList.stream().map(FileInfo::getEntityId).map(this::toUUID).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(e -> e.getId().toString(), Function.identity()));
        return fileInfoList.stream().map(e -> {
            FileInfoDictDeviceModelVO fileInfoVO = new FileInfoDictDeviceModelVO();
            BeanUtils.copyProperties(e, fileInfoVO);
            fileInfoVO.setPicture(Optional.ofNullable(map.get(e.getEntityId())).map(DictDeviceEntity::getPicture).orElse(null));
            return fileInfoVO;
        }).collect(Collectors.toList());
    }

    @Autowired
    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Autowired
    public void setCommonComponent(CommonComponent commonComponent) {
        this.commonComponent = commonComponent;
    }

    @Autowired
    public void setDeviceRepository(DictDeviceRepository dictDeviceRepository) {
        this.dictDeviceRepository = dictDeviceRepository;
    }
}
