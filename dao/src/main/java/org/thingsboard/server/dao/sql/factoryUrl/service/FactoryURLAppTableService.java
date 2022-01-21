package org.thingsboard.server.dao.sql.factoryUrl.service;	
	
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.sql.factoryUrl.dao.FactoryURLAppTableDao;
import org.thingsboard.server.dao.sql.factoryUrl.entity.FactoryURLAppTableEntity;
import org.thingsboard.server.dao.util.BeanToMap;
import org.thingsboard.server.dao.util.sql.jpa.BaseSQLServiceImpl;

import java.util.List;
import java.util.UUID;
/**	
  创建时间: 2022-01-20 12:28:03	
  创建人: HU.YUNHUI	
  描述: 【当天的产能能耗的增量数据和当天历史数据】 对应的service	
*/	
@Service	
public class FactoryURLAppTableService  extends BaseSQLServiceImpl<FactoryURLAppTableEntity, UUID, FactoryURLAppTableDao> {	
	
  	protected Logger logger = LoggerFactory.getLogger(this.getClass());


  	public  FactoryURLAppTableEntity  queryAllByAppUrl(String appUrl)
    {
        return   dao.queryAllByAppUrl(appUrl);
    }
	
	
    /**	
     *根据实体保存	
     * @param factoryURLAppTable	
     * @return FactoryURLAppTableEntity	
     */	
    @Transactional	
    public FactoryURLAppTableEntity save(FactoryURLAppTableEntity factoryURLAppTable){	
        return super.save(factoryURLAppTable);	
    }	
	
   /**	
    * 根据实体类的查询	
    * @param factoryURLAppTable  实体对象	
    * @return List<FactoryURLAppTableEntity> list对象	
    * @throws Exception	
    */	
  public  List<FactoryURLAppTableEntity> findAllByFactoryURLAppTableEntity(FactoryURLAppTableEntity factoryURLAppTable) throws Exception {	
            List<FactoryURLAppTableEntity> factoryURLAppTablelist = findAll( BeanToMap.beanToMapByJackson(factoryURLAppTable));	
            return  factoryURLAppTablelist;	
   }	
	
    /**	
      *根据实体更新	
      * @param factoryURLAppTable	
      * @return FactoryURLAppTableEntity	
      */	
      public FactoryURLAppTableEntity updateRecord(FactoryURLAppTableEntity factoryURLAppTable)  throws ThingsboardException {	
	
            if (factoryURLAppTable.getId() == null) {	
          throw new ThingsboardException("Requested id wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);	
             }	
            return this.updateNonNull(factoryURLAppTable.getId(), factoryURLAppTable);	
        }	
	
	
	
}	
