package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.dao.hs.service.FileService;
import org.thingsboard.server.dao.hs.utils.CommonComponent;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文件接口
 *
 * @author wwj
 * @since 2021.11.30
 */
@Api(value = "文件接口", tags = {"文件接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class FileController extends BaseController {

    private FileService fileService;

    private CommonComponent commonComponent;

    /**
     * 上传文件
     */
    @ApiOperation(value = "上传文件", notes = "返回文件Id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "checksum", value = "校验和", paramType = "query"),
            @ApiImplicitParam(name = "checksumAlgorithmStr", value = "校验和算法", paramType = "query"),
            @ApiImplicitParam(name = "file", value = "文件", paramType = "query", required = true),
    })
    @PostMapping(value = "/file")
    public String uploadFile(@RequestParam(required = false) String checksum,
                             @RequestParam(required = false, defaultValue = "MD5") String checksumAlgorithmStr,
                             @RequestBody MultipartFile file) throws ThingsboardException, IOException {
        if (file == null || file.isEmpty())
            throw new ThingsboardException("文件不能为空！", ThingsboardErrorCode.GENERAL);

        ChecksumAlgorithm checksumAlgorithm = ChecksumAlgorithm.valueOf(checksumAlgorithmStr.toUpperCase());
        return this.fileService.uploadFile(getTenantId(), checksum, checksumAlgorithm, file);
    }

    /**
     * 分片上传文件
     */
    @ApiOperation(value = "分片上传文件", notes = "全部上传后返回文件Id，单个上传成功返回null")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "guid", value = "临时文件名id,便于区分", paramType = "query", required = true),
            @ApiImplicitParam(name = "checksum", value = "文件校验和", paramType = "query"),
            @ApiImplicitParam(name = "checksumChunk", value = "分片文件校验和", paramType = "query"),
            @ApiImplicitParam(name = "checksumAlgorithmStr", value = "校验和算法", paramType = "query"),
            @ApiImplicitParam(name = "chunks", value = "分块数", paramType = "query", required = true),
            @ApiImplicitParam(name = "chunk", value = "分块序号,从1开始", paramType = "query", required = true),
            @ApiImplicitParam(name = "fileName", value = "文件名", paramType = "query", required = true),
            @ApiImplicitParam(name = "file", value = "分片文件", paramType = "query", required = true),
    })
    @PostMapping(value = "/file/multipart")
    public String fileUpload(@RequestParam String guid,
                             @RequestParam(required = false) String checksum,
                             @RequestParam(required = false) String checksumChunk,
                             @RequestParam(required = false, defaultValue = "MD5") String checksumAlgorithmStr,
                             @RequestParam int chunks,
                             @RequestParam int chunk,
                             @RequestParam String fileName,
                             @RequestBody MultipartFile file) throws ThingsboardException, IOException {
        if (file == null || file.isEmpty())
            throw new ThingsboardException("文件不能为空！", ThingsboardErrorCode.GENERAL);

        ChecksumAlgorithm checksumAlgorithm = ChecksumAlgorithm.valueOf(checksumAlgorithmStr.toUpperCase());
        var bytes = file.getBytes();
        if (StringUtils.isNotBlank(checksumChunk)) {
            var calCheckSum = CommonUtil.generateChecksum(checksumAlgorithm, ByteBuffer.wrap(bytes));
            if (!calCheckSum.equals(checksumChunk))
                throw new ThingsboardException("分片文件校验失败", ThingsboardErrorCode.GENERAL);
        }
        Files.write(Paths.get(commonComponent.toFileRootTempDir(getTenantId()).toString(), guid, String.valueOf(chunk)), bytes);
        return this.fileService.uploadMultiPartFile(getTenantId(), guid, checksum, checksumAlgorithm, chunks, fileName);
    }

    /**
     * 取消分片上传文件
     */
    @ApiOperation(value = "取消分片上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "guid", value = "临时文件名id,便于区分", paramType = "query", required = true),
            @ApiImplicitParam(name = "chunks", value = "分块数", paramType = "query", required = true),
    })
    @PostMapping(value = "/file/multipart/cancel")
    public void fileUploadCancel(@RequestParam String guid, @RequestParam int chunks) throws ThingsboardException, IOException {
        this.fileService.cancelUploadMultiPartFile(getTenantId(), guid, chunks);
    }

    /**
     * 下载文件
     */
    @ApiOperation(value = "下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "文件Id", paramType = "query", required = true),
    })
    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("id") String id) throws ThingsboardException, IOException {
        var fileInfo = this.fileService.getFileInfo(getTenantId(), id);
        var filePath = Paths.get(fileInfo.getLocation());

        ByteArrayResource resource = new ByteArrayResource(ByteBuffer.wrap(Files.readAllBytes(filePath)).array());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(fileInfo.getFileName(), StandardCharsets.UTF_8))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + new String(fileInfo.getFileName().getBytes("utf-8"), "ISO8859-1"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    /**
     * 删除文件
     */
    @ApiOperation(value = "删除文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "文件Id", paramType = "query", required = true),})
    @DeleteMapping("/file")
    public void deleteFile(@RequestParam("id") String id) throws ThingsboardException, IOException {
        this.fileService.deleteFile(getTenantId(), id);
    }

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    public void setCommonComponent(CommonComponent commonComponent) {
        this.commonComponent = commonComponent;
    }
}
