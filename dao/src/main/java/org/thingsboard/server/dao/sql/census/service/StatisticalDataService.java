package org.thingsboard.server.dao.sql.census.service;	
	
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;	
import org.thingsboard.server.common.data.exception.ThingsboardException;	
import org.thingsboard.server.dao.util.sql.jpa.BaseSQLServiceImpl;	
import org.thingsboard.server.dao.sql.census.dao.StatisticalDataDao;	
import org.thingsboard.server.dao.sql.census.entity.StatisticalDataEntity;	
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
  创建时间: 2021-12-21 11:26:27	
  创建人: HU.YUNHUI	
  描述: 【当天的产能能耗的增量数据和当天历史数据】 对应的service	
*/	
@Service	
public class StatisticalDataService  extends BaseSQLServiceImpl<StatisticalDataEntity, UUID, StatisticalDataDao> {	
	
  	protected Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	
    /**	
     *根据实体保存	
     * @param statisticalData	
     * @return StatisticalDataEntity	
     */	
    @Transactional	
    public StatisticalDataEntity save(StatisticalDataEntity statisticalData){	
        return super.save(statisticalData);	
    }	
	
   /**	
    * 根据实体类的查询	
    * @param statisticalData  实体对象	
    * @return List<StatisticalDataEntity> list对象	
    * @throws Exception	
    */	
  public  List<StatisticalDataEntity> findAllByStatisticalDataEntity(StatisticalDataEntity statisticalData) throws Exception {	
            List<StatisticalDataEntity> statisticalDatalist = findAll( BeanToMap.beanToMapByJackson(statisticalData));	
            return  statisticalDatalist;	
   }	
	
    /**	
      *根据实体更新	
      * @param statisticalData	
      * @return StatisticalDataEntity	
      */
      @Transactional
      public StatisticalDataEntity updateRecord(StatisticalDataEntity statisticalData)  throws ThingsboardException {	
	
            if (statisticalData.getId() == null) {	
          throw new ThingsboardException("Requested id wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);	
             }	
            return this.updateNonNull(statisticalData.getId(), statisticalData);	
        }	
	
	
	
}	
