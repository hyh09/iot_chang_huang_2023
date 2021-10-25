package org.thingsboard.server.dao.util.sql.jpa.transform;

import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义结果集转map时键名称转换规则
 * @author Lee
 *
 */
public class CustomResultToMap extends AliasedTupleSubsetResultTransformer {
	
	
	private NameTransform trans = NameTransform.UNDERLINE_TO_CAMEL;
	
	public CustomResultToMap() {
		
	}
	
	public CustomResultToMap(NameTransform trans) {
		if(trans != null){
			this.trans = trans;
		}
	}



	@Override
	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
		return false;
	}

}
