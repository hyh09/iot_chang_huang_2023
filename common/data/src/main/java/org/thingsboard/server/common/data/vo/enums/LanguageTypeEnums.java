package org.thingsboard.server.common.data.vo.enums;

import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.common.data.StringUtils;

import java.util.Locale;

/**
 * @program: thingsboard
 * @description: 语言的类别
 * @author: HU.YUNHUI
 * @create: 2021-12-20 16:44
 **/
public enum LanguageTypeEnums {

    SIMPLIFIED_CHINESE("zh_CN",Locale.SIMPLIFIED_CHINESE,"ZhCH"),
    US("en_US",Locale.US,"")
    ;


    private  String  langType;

    private Locale locale;


    private  String fileSuffix;

    /**
     * user.getAdditionalInfo()
     * @param jsonNode
     * @return
     */
    public static LanguageTypeEnums  getLocaleByType(JsonNode  jsonNode)
    {
        String lang = "";
        if (jsonNode.isObject()) {
            JsonNode additionalInfo = jsonNode;
           if(additionalInfo.has("lang")) {
               lang = additionalInfo.get("lang").asText();
               if (StringUtils.isEmpty(lang)) {
                   return SIMPLIFIED_CHINESE;
               }
               for (LanguageTypeEnums enums : LanguageTypeEnums.values()) {
                   if (enums.equals(lang)) {
                       return enums;
                   }
               }
           }
        }

        return SIMPLIFIED_CHINESE;

    }

    LanguageTypeEnums(String langType, Locale locale, String fileSuffix) {
        this.langType = langType;
        this.locale = locale;
        this.fileSuffix = fileSuffix;
    }

    public String getLangType() {
        return langType;
    }

    public void setLangType(String langType) {
        this.langType = langType;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }
}
