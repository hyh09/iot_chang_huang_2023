package org.thingsboard.server.dao.hs.service.Impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thingsboard.server.dao.hs.entity.enums.FileScopeEnum;
import org.thingsboard.server.dao.hs.service.OSSService;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * oss client component
 *
 * @author wwj
 * @since 2021.11.26
 */
//@Component
@Slf4j
public class OSSServiceImpl implements OSSService {

//    @Value("${hs_oss.endpoint}")
    private String endpoint;

//    @Value("${hs_oss.accessKeyId}")
    private String accessKeyId;

//    @Value("${hs_oss.accessKeySecret}")
    private String accessKeySecret;

//    @Value("${hs_oss.bucketName}")
    private String bucketName;

    /**
     * create bucket
     */
    @PostConstruct
    public void createBucket() {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            boolean exists = ossClient.doesBucketExist(bucketName);
            if (!exists) {
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                ossClient.createBucket(createBucketRequest);
            }
        } catch (Exception e) {
            log.error("tb oss post construct error: [{}]", e.getMessage());
        } finally {
            if (ossClient != null)
                ossClient.shutdown();
        }
    }

    /**
     * 上传文件
     *
     * @param uuid        文件uuid
     * @param scope       范围
     * @param inputStream 输入流
     */
    @Override
    public void uploadFile(@NonNull UUID uuid, @NonNull FileScopeEnum scope, @NonNull InputStream inputStream) throws Exception {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.putObject(bucketName, toFileName(uuid, scope), inputStream);
        } catch (Exception e) {
            log.error("tb oss uploadFile error: [{}]", e.getMessage());
            throw e;
        } finally {
            if (ossClient != null)
                ossClient.shutdown();
        }
    }

    /**
     * 分片上传文件
     *
     * @param uuid        uuid
     * @param scope       范围
     * @param fileLength  文件长度
     * @param inputStream 输入流
     */
    @Override
    public void uploadMultipartFile(@NonNull UUID uuid, @NonNull FileScopeEnum scope, @NonNull long fileLength, @NonNull InputStream inputStream) throws Exception {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            String objectName = toFileName(uuid, scope);
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName);
            InitiateMultipartUploadResult upResult = ossClient.initiateMultipartUpload(request);
            String uploadId = upResult.getUploadId();

            List<PartETag> partETags = new ArrayList<>();
            final long partSize = 1024 * 1024L;
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                inputStream.skip(startPos);
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setKey(objectName);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(inputStream);
                uploadPartRequest.setPartSize(curPartSize);
                uploadPartRequest.setPartNumber(i + 1);
                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                partETags.add(uploadPartResult.getPartETag());
            }
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETags);
            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        } catch (Exception e) {
            log.error("tb oss uploadMultipartFile error: [{}]", e.getMessage());
            throw e;
        } finally {
            if (ossClient != null)
                ossClient.shutdown();
        }
    }

    /**
     * 下载文件
     *
     * @param uuid  uuid
     * @param scope 范围
     */
    @Override
    public InputStream downloadFile(@NonNull UUID uuid, @NonNull FileScopeEnum scope) {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            OSSObject ossObject = ossClient.getObject(bucketName, toFileName(uuid, scope));
            return ossObject.getObjectContent();
        } catch (Exception e) {
            log.error("tb oss downloadFile error: [{}]", e.getMessage());
            throw e;
        } finally {
            if (ossClient != null)
                ossClient.shutdown();
        }
    }

    /**
     * 生成签名URL
     *
     * @param uuid  uuid
     * @param scope 范围
     */
    @Override
    public URL generateUrl(@NonNull UUID uuid, @NonNull FileScopeEnum scope) throws Exception {
        OSS ossClient = null;
        String securityToken = "";
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, securityToken);
            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
            return ossClient.generatePresignedUrl(bucketName, toFileName(uuid, scope), expiration);
        } catch (Exception e) {
            log.error("tb oss generateUrl error: [{}]", e.getMessage());
            throw e;
        } finally {
            if (ossClient != null)
                ossClient.shutdown();
        }
    }

    /**
     * 删除文件
     *
     * @param uuid  文件uuid
     * @param scope 范围
     */
    @Override
    public void deleteFile(@NonNull UUID uuid, @NonNull FileScopeEnum scope) throws Exception {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.deleteObject(bucketName, toFileName(uuid, scope));
        } catch (Exception e) {
            log.error("tb oss deleteFile error: [{}]", e.getMessage());
            throw e;
        } finally {
            if (ossClient != null)
                ossClient.shutdown();
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param uuid  文件uuid
     * @param scope 范围
     */
    @Override
    public boolean isFileExist(@NonNull UUID uuid, @NonNull FileScopeEnum scope) throws Exception {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            return ossClient.doesObjectExist(bucketName, toFileName(uuid, scope));
        } catch (Exception e) {
            log.error("tb oss isFileExist error: [{}]", e.getMessage());
            throw e;
        } finally {
            if (ossClient != null)
                ossClient.shutdown();
        }
    }

    /**
     * 列举全部文件
     */
    @Override
    public List<OSSObjectSummary> listFiles() throws Exception {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ObjectListing objectListing = ossClient.listObjects(bucketName);
            return objectListing.getObjectSummaries();
        } catch (Exception e) {
            log.error("tb oss listFile error: [{}]", e.getMessage());
            throw e;
        } finally {
            if (ossClient != null)
                ossClient.shutdown();
        }
    }

    /**
     * 列举某个范围内的全部文件
     *
     * @param scope 范围
     */
    @Override
    public List<OSSObjectSummary> listScopeFiles(@NonNull FileScopeEnum scope) throws Exception {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ObjectListing objectListing = ossClient.listObjects(scope.getCode());
            return objectListing.getObjectSummaries();
        } catch (Exception e) {
            log.error("tb oss listScopeFile error: [{}]", e.getMessage());
            throw e;
        } finally {
            if (ossClient != null)
                ossClient.shutdown();
        }
    }

    /**
     * 包装成目录
     *
     * @param uuid  uuid
     * @param scope 文件范围
     */
    public String toFileName(@NonNull UUID uuid, @NonNull FileScopeEnum scope) {
        return scope.getCode() + uuid.toString();
    }
}
