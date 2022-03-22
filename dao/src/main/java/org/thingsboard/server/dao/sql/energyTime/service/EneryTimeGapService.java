package org.thingsboard.server.dao.sql.energyTime.service;	
	
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;	
import org.thingsboard.server.common.data.exception.ThingsboardException;	
import org.thingsboard.server.dao.util.sql.jpa.BaseSQLServiceImpl;	
import org.thingsboard.server.dao.sql.energyTime.dao.EneryTimeGapDao;	
import org.thingsboard.server.dao.sql.energyTime.entity.EneryTimeGapEntity;	
import org.thingsboard.server.dao.util.BeanToMap;	
import org.apache.commons.collections.CollectionUtils;	
import org.springframework.stereotype.Service;	
import org.springframework.transaction.annotation.Transactional;	
import org.slf4j.Logger;	
import org.slf4j.LoggerFactory;	
import org.apache.commons.lang3.StringUtils;	
import java.util.UUID;	
	
	
import java.util.List;	
import java.util.stream.Collectors;	
/**	
  创建时间: 2021-12-16 11:17:13	
  创建人: HU.YUNHUI	
  描述: 【能耗超过30分钟的时间的遥测时间差保存】 对应的service	
*/	
@Service	
public class EneryTimeGapService  extends BaseSQLServiceImpl<EneryTimeGapEntity, UUID, EneryTimeGapDao> {	
	
  	protected Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	
    /**	
     *根据实体保存	
     * @param eneryTimeGap	
     * @return EneryTimeGapEntity	
     */	
    @Transactional	
    public EneryTimeGapEntity save(EneryTimeGapEntity eneryTimeGap){	
        return super.save(eneryTimeGap);	
    }	
	
   /**	
    * 根据实体类的查询	
    * @param eneryTimeGap  实体对象	
    * @return List<EneryTimeGapEntity> list对象	
    * @throws Exception	
    */	
  public  List<EneryTimeGapEntity> findAllByEneryTimeGapEntity(EneryTimeGapEntity eneryTimeGap) throws Exception {	
            List<EneryTimeGapEntity> eneryTimeGaplist = findAll( BeanToMap.beanToMapByJackson(eneryTimeGap));	
            return  eneryTimeGaplist;	
   }	
	
    /**	
      *根据实体更新	
      * @param eneryTimeGap	
      * @return EneryTimeGapEntity	
      */	
      public EneryTimeGapEntity updateRecord(EneryTimeGapEntity eneryTimeGap)  throws ThingsboardException {	
	
            if (eneryTimeGap.getId() == null) {	
          throw new ThingsboardException("Requested id wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);	
             }	
            return this.updateNonNull(eneryTimeGap.getId(), eneryTimeGap);	
        }	
	
	
	
}	
