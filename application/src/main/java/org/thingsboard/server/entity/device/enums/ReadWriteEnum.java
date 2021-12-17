package org.thingsboard.server.entity.device.enums;

import com.datastax.oss.driver.shaded.guava.common.collect.Lists;
import lombok.Getter;
import org.thingsboard.server.common.data.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读写方向
 */
@Getter
public enum ReadWriteEnum {
    READ("READ","只读"),
    WRITE("WRITE","只写"),
    READ_WRITE("READ_WRITE","读写");

    private String code;
    private String name;

    ReadWriteEnum(String code,String name){
        this.code = code;
        this.name = name;
    }

    public static List<Map<String, String>> toList() {
        List list = Lists.newArrayList();//Lists.newArrayList()其实和new ArrayList()几乎一模
        //  一样, 唯一它帮你做的(其实是javac帮你做的), 就是自动推导(不是"倒")尖括号里的数据类型.

        for (ReadWriteEnum airlineTypeEnum : ReadWriteEnum.values()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", airlineTypeEnum.getCode());
            map.put("name", airlineTypeEnum.getName());
            list.add(map);
        }
        return list;
    }

    /**
     * 根据名称拿code,为空就返回null
     * @param name
     * @return
     */
    public static String getCodeByDesc(String name){
        if(StringUtils.isNotEmpty(name)){
            for(Map<String, String> map:toList()){
                for(Map.Entry<String, String> entry : map.entrySet()){
                    if(entry.getValue().equals(name)){
                        return entry.getValue();
                    }
                }
            }
        }
        return null;
    }


}
