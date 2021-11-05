package org.thingsboard.server.dao.util.sql.jpa.repository;

import lombok.Data;
import org.thingsboard.server.common.data.page.PageLink;

/**
 * @program: thingsboard
 * @description: 自定义排序解析
 * @author: HU.YUNHUI
 * @create: 2021-11-03 19:32
 **/
@Data
public class SortRowName {

    private  String  preField;

    private  String order;

    public SortRowName(String preField, String order) {
        this.preField = preField;
        this.order = order;
    }
}
