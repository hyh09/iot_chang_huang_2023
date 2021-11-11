package org.thingsboard.server.dao.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 通用工具
 */
public class CommonUtils {
	
	/**
	 * 获取导致Exception异常的真正原因信息
	 * @return
	 */
	public static String getExceptionCauseMessage(Throwable e) {
		if(e.getCause() != null) {
			return CommonUtils.getExceptionCauseMessage(e.getCause());
		} else {
			return e.getMessage();
		}
	}

    /**
     * 将Exception中的堆栈信息转换成字符串
     * @param e
     * @return
     */
    public static String getExceptionStackTraceToString(Throwable e){
    	StringBuffer strBuf = new StringBuffer();
    	if(e != null){
	    	StackTraceElement[] stList = e.getStackTrace();
	    	StackTraceElement st = null;
	    	for(int i=0; i<stList.length; i++){
	    		st = stList[i];
	    		if(i==0){
	    			strBuf.append(e.getMessage()).append("\t").append(st.getClassName()).append(".").append(st.getMethodName())
	    				.append("(").append(st.getFileName()).append(":").append(st.getLineNumber()).append(")");
	    		}else{
	    			strBuf.append("\tat  ")
	    				.append(st.getClassName()).append(".").append(st.getMethodName())
	    				.append("(").append(st.getFileName()).append(":").append(st.getLineNumber()).append(")");
	    		}
	    	}
    	}
    	return strBuf.toString();
    }
    
    /**
     * 取得当前应用服务器时间
     * @return
     */
    public static Timestamp getCurentAppServerTimestamp(){
    	return new Timestamp(System.currentTimeMillis());
    }


    /**
     * 获得当前日期 yyyy-MM-dd HH:mm:ss
     *
     * @return 2019-08-27 14:12:40
     */
    public static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return df.format(date);
    }


    
    /**
     * 取得当前应用服务器时间(date)
     */
    public static Date getCurentAppServerDate(){
    	return new Date(getCurentAppServerTimestamp().getTime());
    }
    
    /**
     * 把得到的完整类名，首字母变小写
     * @param className
     * @return
     */
    public static String lowerFirstBeanName(String className){
    	
    	 String tempstring = className.substring(className.lastIndexOf(".")+1,className.lastIndexOf(".")+2);
         className =className.substring(className.lastIndexOf(".")+2,className.length());
         className =tempstring.toLowerCase()+className;
         return className;
    }
    
    /**
     * 将空值的属性从目标实体类中复制到源实体类中
     * @param src : 要将属性中的空值覆盖的对象(源实体类)
     * @param target :从数据库根据id查询出来的目标对象
     */
    public static void copyNonNullProperties(Object src, Object target){
        BeanUtils.copyProperties(src,target,getNullProperties(src));
    }

    /**
     * 将为空的properties给找出来,然后返回出来
     * @param src
     * @return
     */
    public static String[] getNullProperties(Object src){
        BeanWrapper srcBean=new BeanWrapperImpl(src);
        PropertyDescriptor[] pds=srcBean.getPropertyDescriptors();
        Set<String> emptyName=new HashSet<>();
        for(PropertyDescriptor p:pds){
            Object srcValue=srcBean.getPropertyValue(p.getName());
            if(srcValue==null) emptyName.add(p.getName());
        }
        String[] result=new String[emptyName.size()];
        return emptyName.toArray(result);
    }


    /**
     * 获取当天的零时时间
     */
    public  static  long  getZero()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
      return   zero.getTime();
    }

    /**
     * 获取当前的时间
     */
    public  static  long getNowTime()
    {
        Date  date =  new Date();
        return  date.getTime();
    }
}
