package org.thingsboard.server.dao.board;

import org.thingsboard.server.common.data.vo.TsSqlDayVo;
import org.thingsboard.server.dao.sql.role.entity.BoardV3DeviceDitEntity;

import java.util.List;

/**
 * @program: thingsboard
 * @description: 看板3期需求
 * @author: HU.YUNHUI
 * @create: 2022-03-07 11:18
 **/
public interface BulletinV3BoardVsSvc {

    /**
     *
      * @param tsSqlDayVo
     * @return
     */
   List<BoardV3DeviceDitEntity> queryDeviceDictionaryByEntityVo(TsSqlDayVo tsSqlDayVo);
}
