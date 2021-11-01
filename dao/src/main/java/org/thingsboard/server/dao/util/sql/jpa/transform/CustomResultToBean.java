package org.thingsboard.server.dao.util.sql.jpa.transform;


import org.hibernate.HibernateException;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyChainedImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.transform.AliasToBeanResultTransformer;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;


public class CustomResultToBean extends AliasToBeanResultTransformer {
	private final Class resultClass;
	private boolean isInitialized;
	private String[] aliases;
	private Setter[] setters;
	private NameTransform trans = NameTransform.UNDERLINE_TO_CAMEL;
	

	public <T> CustomResultToBean(Class<T> resultClass) {
		super(resultClass);
		if ( resultClass == null ) {
			throw new IllegalArgumentException( "resultClass cannot be null" );
		}
		isInitialized = false;
		this.resultClass = resultClass;
	}
	
	public <T> CustomResultToBean(Class<T> resultClass, NameTransform trans) {
		super(resultClass);
		if ( resultClass == null ) {
			throw new IllegalArgumentException( "resultClass cannot be null" );
		}
		isInitialized = false;
		this.resultClass = resultClass;
		this.trans = trans;
	}

	@Override
	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
		return false;
	}






	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		CustomResultToBean that = ( CustomResultToBean ) o;

		if ( ! resultClass.equals( that.resultClass ) ) {
			return false;
		}
		if ( ! Arrays.equals( aliases, that.aliases ) ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = resultClass.hashCode();
		result = 31 * result + ( aliases != null ? Arrays.hashCode( aliases ) : 0 );
		return result;
	}

}
