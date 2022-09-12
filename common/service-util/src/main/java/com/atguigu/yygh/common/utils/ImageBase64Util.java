package com.atguigu.yygh.common.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.*;

/**
 * 图片base64工具类
 */
public class ImageBase64Util {

    public static void main(String[] args) {
        String imageFile= "E:\\Adobe\\图库\\壁纸\\123.jpg";// 待处理的图片
        System.out.println(getImageString(imageFile));
    }

    public static String getImageString(String imageFile){
        InputStream is = null;
        try {
            byte[] data = null;
            is = new FileInputStream(new File(imageFile));
            data = new byte[is.available()];
            is.read(data);
            return new String(Base64.encodeBase64(data));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                    is = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
