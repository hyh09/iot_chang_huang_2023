package org.thingsboard.server.dao.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Project Name: thingsboard
 * File Name: Md5Utils
 * Package Name: org.thingsboard.server.dao.util
 * Date: 2022/6/13 17:47
 * author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
public class Md5Utils {


    /**
     * md5加密 32位 小写
     * @param plainText
     * @return
     */
    public static String encryption(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }

            re_md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }


}
