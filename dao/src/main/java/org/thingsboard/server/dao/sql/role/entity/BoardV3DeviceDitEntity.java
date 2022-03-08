package org.thingsboard.server.dao.sql.role.entity;

import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.dao.hs.dao.HsModelConstants;
import org.thingsboard.server.dao.util.sql.entity.TenantBaseEntity;

import javax.persistence.*;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 设备字典的返回接口
 * @author: HU.YUNHUI
 * @create: 2022-03-07 11:26
 **/
@Data
@Entity
@Table(name = "hs_dict_device")
//@IdClass(TimescaleTsKvCompositeKey.class)
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "boardV3DeviceDitEntity_map01",
                classes = {
                        @ConstructorResult(
                                targetClass = BoardV3DeviceDitEntity.class,
                                columns = {
                                        @ColumnResult(name = "id", type = UUID.class),
                                        @ColumnResult(name = "code", type = String.class),
                                        @ColumnResult(name = "name",type = String.class),
                                }
                        ),
                }

        ),



})
@ToString
public class BoardV3DeviceDitEntity extends TenantBaseEntity {




    /**
     * 编码
     */
    @Transient
    @Column(name = HsModelConstants.DICT_DEVICE_CODE)
    private String code;

    /**
     * 名称
     */
    @Transient
    @Column(name = HsModelConstants.DICT_DEVICE_NAME)
    private String name;


    public BoardV3DeviceDitEntity(UUID id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }
}
