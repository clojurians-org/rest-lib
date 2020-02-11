package com.larluo;

import java.io.InputStream;
import java.util.Map;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;

import org.apache.http.client.fluent.Request ;


public class Lib {
    public static String base64(String in) {
        return encodeBase64String(in.getBytes());
    }
    public static String md5(String in) {
        return md5Hex(in);
    }

    public static String sha256(String in) {
        return sha256Hex(in);
    }

    public static String rsaEncypt(String in) {
        return "" ;
    }

    public static String rsaDecrypt(String in) {
        return "";
    }

    public static InputStream httpWithHeader(String url, Map<String, String> params, Map<String, String> header) {
        return null;
    }

    public static InputStream http(String url, Map<String, String> params) {
        return null;
    }

    public static Object httpJSON(InputStream in) {
        return null;
    }
    
    public static String httpString(String url) {
        return null;
    }

    public static void main(String[] args) {
        System.out.println(base64("larluo"));
        System.out.println(md5("larluo"));
        System.out.println(sha256("larluo"));
    }
}
