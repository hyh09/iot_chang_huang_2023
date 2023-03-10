package org.thingsboard.server.dao.util;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thingsboard.server.common.data.vo.RowName;
import org.thingsboard.server.dao.board.factoryBoard.vo.pro.workshop.SqlOnFieldAnnotation;
import org.thingsboard.server.dao.util.anno.CustomValid;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


public class GenericsUtils {
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(GenericsUtils.class.getName());
    /** 
     * 通过反射,获得指定类的父类的泛型参数的实际类型. 如DaoSupport<Buyer> 
     * @param clazz  clazz 需要反射的类,该类必须继承范型父类 
     * @param index  泛型参数所在索引,从0开始. 
     * @return 范型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回 
     *         <code>Object.class</code> 
     */  
    @SuppressWarnings("rawtypes")
	public static Class getSuperClassGenricType(Class clazz, int index) {
        Type genType = clazz.getGenericSuperclass();// 得到泛型父类    // --->返回直接继承的父类
        // 如果没有实现ParameterizedType接口，即不支持泛型，直接返回Object.class  
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }  
        // 返回表示此类型实际类型参数的Type对象的数组,数组里放的都是对应类型的Class, 如BuyerServiceBean extends  
        // DaoSupport<Buyer,Contact>就返回Buyer和Contact类型  
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {  
            throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
        }  
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }  
        return (Class) params[index];
    } 
    
    @SuppressWarnings("rawtypes")
	public static Class[] getSuperClassGenricAllType(Class clazz) {
        Type genType = clazz.getGenericSuperclass();// 得到泛型父类
        // 如果没有实现ParameterizedType接口，即不支持泛型，直接返回Object.class  
        if (!(genType instanceof ParameterizedType)) {
            return new Class[]{Object.class};
        }  
        // 返回表示此类型实际类型参数的Type对象的数组,数组里放的都是对应类型的Class, 如BuyerServiceBean extends  
        // DaoSupport<Buyer,Contact>就返回Buyer和Contact类型  
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class[]) params;
    } 
  
    /** 
     * 通过反射,获得指定类的父类的第一个泛型参数的实际类型. 如DaoSupport<Buyer> 
     * @param clazz  clazz 需要反射的类,该类必须继承泛型父类 
     * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回 
     *         <code>Object.class</code> 
     */  
    @SuppressWarnings("rawtypes")
	public static Class getSuperClassGenricType(Class clazz) {
        return getSuperClassGenricType(clazz, 0);  
    }  
  
    /** 
     * 通过反射,获得方法返回值泛型参数的实际类型. 如: public Map<String, Buyer> getNames(){} 
     * @param Method method 方法 
     * @param int index 泛型参数所在索引,从0开始. 
     * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回 
     *         <code>Object.class</code> 
     */  
    @SuppressWarnings("rawtypes")
	public static Class getMethodGenericReturnType(Method method, int index) {
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            if (index >= typeArguments.length || index < 0) {  
                throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
            }  
            return (Class) typeArguments[index];
        }  
        return Object.class;
    }  
  
    /** 
     * 通过反射,获得方法返回值第一个泛型参数的实际类型. 如: public Map<String, Buyer> getNames(){} 
     * @param Method  method 方法 
     * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回 
     *         <code>Object.class</code> 
     */  
    @SuppressWarnings("rawtypes")
	public static Class getMethodGenericReturnType(Method method) {
        return getMethodGenericReturnType(method, 0);  
    }  
  
    /** 
     * 通过反射,获得方法输入参数第index个输入参数的所有泛型参数的实际类型. 如: public void add(Map<String, 
     * Buyer> maps, List<String> names){} 
     * @param Method method 方法 
     * @param int index 第几个输入参数 
     * @return 输入参数的泛型参数的实际类型集合, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回空集合 
     */  
    @SuppressWarnings("rawtypes")
    public static List<Class> getMethodGenericParameterTypes(Method method, int index) {
        List<Class> results = new ArrayList<Class>();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (index >= genericParameterTypes.length || index < 0) {  
            throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
        }  
        Type genericParameterType = genericParameterTypes[index];
        if (genericParameterType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericParameterType;
            Type[] parameterArgTypes = aType.getActualTypeArguments();
            for (Type parameterArgType : parameterArgTypes) {
                Class parameterArgClass = (Class) parameterArgType;
                results.add(parameterArgClass);  
            }  
            return results;  
        }  
        return results;  
    } 
    
    /** 
     * 通过反射,获得方法输入参数第一个输入参数的所有泛型参数的实际类型. 如: public void add(Map<String, Buyer> 
     * maps, List<String> names){} 
     * @param Method  method 方法 
     * @return 输入参数的泛型参数的实际类型集合, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回空集合 
     */  
    @SuppressWarnings("rawtypes")
    public static List<Class> getMethodGenericParameterTypes(Method method) {
        return getMethodGenericParameterTypes(method, 0);  
    }  
  
    /** 
     * 通过反射,获得Field泛型参数的实际类型. 如: public Map<String, Buyer> names; 
     * @param Field field 字段 
     * @param int index 泛型参数所在索引,从0开始. 
     * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回 
     *         <code>Object.class</code> 
     */  
    @SuppressWarnings("rawtypes")
    public static Class getFieldGenericType(Field field, int index) {
        Type genericFieldType = field.getGenericType();
        if (genericFieldType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            if (index >= fieldArgTypes.length || index < 0) {  
                throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
            }  
            return (Class) fieldArgTypes[index];
        }  
        return Object.class;
    }  
  
    /** 
     * 通过反射,获得Field泛型参数的实际类型. 如: public Map<String, Buyer> names; 
     * @param Field field 字段 
     * @param int index 泛型参数所在索引,从0开始. 
     * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回 
     *         <code>Object.class</code> 
     */  
    @SuppressWarnings("rawtypes")
    public static Class getFieldGenericType(Field field) {
        return getFieldGenericType(field, 0);  
    }


    /**
     * 获取类上注解属性value
     * @param cla
     * @param clb
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static  String  getClassAnnotation(Class cla,Class clb)
    {
        if(clb.isAssignableFrom(JsonFilter.class) ){
            JsonFilter anno = (JsonFilter) cla.getAnnotation(JsonFilter.class);
            if(anno == null )
            {
                return  "";
            }
            return anno.value();
        }
        return  null;

    }


    /**
     * 获取自定义注解 CustomValid
     * @param cls
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Set<String> getFieldsAnnotation(Class cls)
    {
        Set<String> properties = new HashSet<>();
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            CustomValid subFAnno = f.getAnnotation(CustomValid.class);
            if(subFAnno != null)
            {
                properties.add(f.getName());
            }

        }
        return  properties;

    }

    public static Hashtable<String,String> getRowNameHash(Class cls)
    {
        Hashtable<String,String> properties = new Hashtable<>();
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            RowName rowName = f.getAnnotation(RowName.class);
            if(rowName != null)
            {
                properties.put(f.getName(),rowName.value());
            }

        }
        return  properties;

    }


    public static Hashtable<String,SqlOnFieldAnnotation> getRowNameHashSql(Class cls)
    {
        Hashtable<String,SqlOnFieldAnnotation> properties = new Hashtable<>();
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            SqlOnFieldAnnotation rowName = f.getAnnotation(SqlOnFieldAnnotation.class);
            if(rowName != null)
            {
                properties.put(f.getName(),rowName);
            }

        }
        return  properties;

    }



}