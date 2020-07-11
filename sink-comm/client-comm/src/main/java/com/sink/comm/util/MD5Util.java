package com.sink.comm.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by SINK on 2019/12/18
 */
public class MD5Util {
    public MD5Util() {
    }

    public static String computeGBK(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("GBK"));
        } catch (NoSuchAlgorithmException var5) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException var6) {
            var6.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; ++i) {
            if (Integer.toHexString(255 & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(255 & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(255 & byteArray[i]));
            }
        }

        return md5StrBuff.toString();
    }

    public static String compute(String str, String encode) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes(encode));
        } catch (NoSuchAlgorithmException var6) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException var7) {
            var7.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; ++i) {
            if (Integer.toHexString(255 & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(255 & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(255 & byteArray[i]));
            }
        }

        return md5StrBuff.toString();
    }

    public static String computeUTF(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException var5) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException var6) {
            var6.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; ++i) {
            if (Integer.toHexString(255 & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(255 & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(255 & byteArray[i]));
            }
        }

        return md5StrBuff.toString();
    }

    public static String computeUTF(byte[] str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str);
        } catch (NoSuchAlgorithmException var5) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        }

        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; ++i) {
            if (Integer.toHexString(255 & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(255 & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(255 & byteArray[i]));
            }
        }

        return md5StrBuff.toString();
    }

    public static String compute(String inStr) {
        try {
            char[] charArray = inStr.toCharArray();
            byte[] byteArray = new byte[charArray.length];

            for (int i = 0; i < charArray.length; ++i) {
                byteArray[i] = (byte) charArray[i];
            }

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();

            for (int i = 0; i < md5Bytes.length; ++i) {
                int val = md5Bytes[i] & 255;
                if (val < 16) {
                    hexValue.append("0");
                }

                hexValue.append(Integer.toHexString(val));
            }

            return hexValue.toString();
        } catch (NoSuchAlgorithmException var8) {
            System.err.println("-----------------加密数据出错：" + var8.getMessage() + "--------------");
            return null;
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String b = "1005403795450ahyz_hfHFYDAH_****0000100018060822840581575532569710105c545cf78ef92da43ee8548ce892c732";
        System.out.println("加密后的数据:" + computeGBK(b));
        String postString = computeUTF(compute(b));
        System.out.println("加密后的数据:" + postString);
        System.out.println(compute("2BAB989750FBA827D0B3EE2C1D6552C0170914005732201709140000000002141"));
        System.out.println(MD5Util.computeUTF("vanessa"));
    }
}
