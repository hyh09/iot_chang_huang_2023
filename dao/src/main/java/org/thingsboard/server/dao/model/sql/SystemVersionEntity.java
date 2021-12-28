package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.systemversion.SystemVersion;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.SYSTEM_VERSION_COLUMN_FAMILY_NAME)
public class SystemVersionEntity extends AbstractSystemVersionEntity<SystemVersion> {

    public SystemVersionEntity() {
        super();
    }

    public SystemVersionEntity(SystemVersion systemVersion) {
        super(systemVersion);
    }

    @Override
    public SystemVersion toData() {
        return super.toSystemVersion();
    }
}
