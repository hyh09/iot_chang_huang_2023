package org.thingsboard.server.dao.util;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.vo.CustomException;
import org.thingsboard.server.common.data.vo.enums.ActivityException;

import java.lang.reflect.Field;
import java.util.*;

public class BeanToMap {





    /**
     * 不包含父类
     *  对传入的对象进行数据清洗，将属性值为null和""的去掉，其他字段名和属性值存入map集合
     * @return
     */
    public static Map<String,Object> objectToMap(Object requestParameters) throws IllegalAccessException {

        Map<String, Object> map = new HashMap<>();
        // 获取f对象对应类中的所有属性域
        Field[] fields = requestParameters.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            // 获取原来的访问控制权限
            boolean accessFlag = fields[i].isAccessible();
            // 修改访问控制权限
            fields[i].setAccessible(true);
            // 获取在对象f中属性fields[i]对应的对象中的变量
            Object o = fields[i].get(requestParameters);
            if (o != null && StringUtils.isNotBlank(o.toString().trim())) {
                map.put(varName, o.toString().trim());
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            }
        }
        return map;
    }


    public static TreeMap<String, String> convertBeanToMap(Object bean) throws IllegalArgumentException,IllegalAccessException {
        TreeMap<String, String> map = new TreeMap<String, String>();
        Class<?> clazz = bean.getClass();
        for(; clazz != Object.class;clazz = clazz.getSuperclass()){
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 获取bean的属性和值
                field.setAccessible(true);
                if (field.get(bean) != null) {
                    map.put(field.getName(), field.get(bean).toString());
                }
            }

        }
        return  map;
    }


    /**
     * jackson将对象转换为map
     */
    public  static  Map beanToMapByJackson(Object bean) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(); //转换器
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json=mapper.writeValueAsString(bean);
        Map m = mapper.readValue(json, Map.class);
        return  m;
    }


    /**
     * 提取需要的属性
     * @param bean
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public  static <T>  Map beanToMapByJacksonFilter(T bean ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(); //转换器
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        Set<String> properties =   GenericsUtils.getFieldsAnnotation(bean.getClass());
        String id =  GenericsUtils.getClassAnnotation(bean.getClass(), JsonFilter.class);
        if(StringUtils.isNoneBlank(id) && CollectionUtils.isEmpty(properties))
        {
            String name = bean.getClass().getName();
            throw  new CustomException(ActivityException.FAILURE_ERROR.getCode(),"后端java类配置异常:类名{"+name+"}不应该配置 @JsonFilter 注解!");
        }



        if(StringUtils.isNotBlank(id) && !CollectionUtils.isEmpty(properties))
        {
            SimpleFilterProvider filterProvider = new SimpleFilterProvider();
            filterProvider.addFilter(id, SimpleBeanPropertyFilter.filterOutAllExcept(properties));
            mapper.setFilterProvider(filterProvider);
        }


        String json=mapper.writeValueAsString(bean);
        Map m = mapper.readValue(json, Map.class);
        return  m;
    }




}
