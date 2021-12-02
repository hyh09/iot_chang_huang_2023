package org.thingsboard.server.dao.hs.dao;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.hs.entity.po.FileInfo;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.FILE_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class FileEntity extends BasePgEntity<FileEntity> implements ToData<FileInfo> {
    /**
     * 租户Id
     */
    @Column(name = HsModelConstants.GENERAL_TENANT_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID tenantId;

    @Column(name = HsModelConstants.FILE_CHECK_SUM)
    private String checkSum;

    @Column(name = HsModelConstants.FILE_FILE_NAME)
    private String fileName;

    @Column(name = HsModelConstants.FILE_CONTENT_TYPE)
    private String contentType;

    @Column(name = HsModelConstants.FILE_CHECKSUM_ALGORITHM)
    private String checksumAlgorithm;

    @Column(name = HsModelConstants.FILE_DATA_SIZE)
    private Long dataSize;

    @Column(name = HsModelConstants.FILE_ADDITIONAL_INFO)
    private String additionalInfo;

    @Column(name = HsModelConstants.FILE_SCOPE)
    private String scope;

    @Column(name = HsModelConstants.FILE_ENTITY_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID entityId;

    @Column(name = HsModelConstants.FILE_LOCATION)
    private String location;

    public FileEntity() {
    }

    public FileEntity(FileInfo common) {
        if (common.getId() != null)
            this.id = UUID.fromString(common.getId());
        if (common.getTenantId() != null)
            this.tenantId = UUID.fromString(common.getTenantId());
        if (common.getEntityId() != null)
            this.entityId = UUID.fromString(common.getEntityId());
        this.checkSum = common.getCheckSum();
        this.fileName = common.getFileName();
        this.contentType = common.getContentType();
        this.additionalInfo = common.getAdditionalInfo();
        this.checksumAlgorithm = common.getChecksumAlgorithm();
        this.dataSize = common.getDataSize();
        this.scope = common.getScope();
        this.location = common.getLocation();

        this.setCreatedTimeAndCreatedUser(common);
    }

    /**
     * to data
     */
    public FileInfo toData() {
        FileInfo common = new FileInfo();
        common.setId(id.toString());
        if (tenantId != null) {
            common.setTenantId(tenantId.toString());
        }
        if (entityId != null) {
            common.setEntityId(entityId.toString());
        }
        common.setCheckSum(checkSum);
        common.setDataSize(dataSize);
        common.setFileName(fileName);
        common.setAdditionalInfo(additionalInfo);
        common.setChecksumAlgorithm(checksumAlgorithm);
        common.setContentType(contentType);
        common.setLocation(location);
        common.setScope(scope);

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }
}
