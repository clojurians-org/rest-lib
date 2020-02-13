package com.larluo;

import java.lang.reflect.Method ;
import java.lang.reflect.Constructor ;
import java.io.InputStream;
import java.io.OutputStream ;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream ;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.Base64 ;
import java.util.Arrays ;
import java.util.List ;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.UUID ;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder ;

import java.time.LocalDateTime ;
import java.time.format.DateTimeFormatter ;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.protocol.HttpContext ;
import org.apache.http.client.HttpClient; 
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient ;
import org.apache.http.HttpResponse ;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response ;


import org.apache.http.ssl.SSLContexts ;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory ;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy ;
import org.apache.http.conn.ssl.NoopHostnameVerifier ;

import org.w3c.dom.Document ;
import org.w3c.dom.Attr ;
import org.w3c.dom.Element ;
import javax.xml.parsers.DocumentBuilderFactory ;
import javax.xml.parsers.DocumentBuilder ;
import javax.xml.transform.dom.DOMSource ;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer ;
import javax.xml.transform.stream.StreamResult ;
import javax.xml.transform.OutputKeys ;
import javax.xml.xpath.XPathFactory ;

import javax.crypto.Cipher ;
import java.security.KeyFactory ;
import java.security.Key ;
import java.security.spec.X509EncodedKeySpec ;
import java.security.spec.PKCS8EncodedKeySpec ;

public class Lib {
    public static enum HttpMethod {GET, POST} ;
    public static enum RSAMethod {PUBLIC,PRIVATE} ;

    public static Map<String, Object> map(Object... xs) {
        int length = xs.length ;
        int offset =  0 ;
        Map<String, Object> result = new HashMap<String, Object>() ;
        while (offset < length) {
          result.put(xs[offset].toString(), xs[offset+1]) ;
          offset += 2 ;
        }
        return result ;
    }
    public static Map<String, String> stringmap (Object... xs) {
        int length = xs.length ;
        int offset =  0 ;
        Map<String, String> result = new HashMap<String, String>() ;
        while (offset < length) {
          result.put(xs[offset].toString(), xs[offset+1].toString()) ;
          offset += 2 ;
        }
        return result ;
    }
    public static String uuid() { return UUID.randomUUID().toString() ; }
    public static String uuidShort() { return uuid().replaceAll("-","") ; }
    public static long now() { return System.currentTimeMillis() ; }
    public static String nowString() { return Long.toString(now()) ; }
    public static String nowLocal() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) ;
    }
    public static String nowUTC() {
        return "" ;
    }
    public static String slurp(InputStream in) {
        return new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n")) ;
    }

    public static String urlEncode(String in) 
    throws Exception {
        return URLEncoder.encode(in, StandardCharsets.UTF_8.toString()) ;
    }

    public static byte[] base64Bin(byte[] in) {
        return Base64.getEncoder().encode(in) ;
    }
    public static String base64(String in) {
        return Base64.getEncoder().encodeToString(in.getBytes()) ;
    }
    public static byte[] unbase64Bin(byte[] in) {
        return Base64.getDecoder().decode(in) ;
    }
    public static String unbase64(String in) {
        return new String(Base64.getDecoder().decode(in)) ;
    }
    public static String md5(String in) {
        return md5Hex(in);
    }

    public static String sha256(String in) {
        return sha256Hex(in);
    }


    public static Key rsaKey(RSAMethod method, String in) 
    throws Exception {
        byte[] unbase64Bin = unbase64Bin(in.getBytes()) ;
        return (method == RSAMethod.PUBLIC)
           ? KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(unbase64Bin))
           : KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(unbase64Bin)) ;
    }
    public static String rsaEncrypt(RSAMethod method, String publicKey, int blockSize, String in) 
    throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding") ;
        cipher.init(Cipher.ENCRYPT_MODE, rsaKey(method, publicKey)) ;

        byte[] inBytes = in.getBytes() ;
        ByteArrayOutputStream out = new ByteArrayOutputStream() ;
        int length = inBytes.length ;
        int offset = 0 ;
        while (offset < length) {
          int actualBlockSize = (length - offset > blockSize) ? blockSize : length - offset ;
          out.write(cipher.doFinal(inBytes, offset, actualBlockSize)) ;
          offset += actualBlockSize ;
        }
        out.flush() ;
        return new String(base64Bin(out.toByteArray())) ;
    }

    public static String rsaDecrypt(RSAMethod method, String privateKey, int blockSize, String in) 
    throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding") ;
        cipher.init(Cipher.DECRYPT_MODE, rsaKey(method, privateKey)) ;

        byte[] inBytes = unbase64Bin(in.getBytes()) ;
        ByteArrayOutputStream out = new ByteArrayOutputStream() ;
        int length = inBytes.length ;
        int offset = 0 ;
        while (offset < length) {
          int actualBlockSize = (length - offset > blockSize) ? blockSize : length - offset ;
          out.write(cipher.doFinal(inBytes, offset, actualBlockSize)) ;
          offset += actualBlockSize ;
        }
        out.flush() ;
        return new String(out.toByteArray()) ;
    }

    @SuppressWarnings("unchecked")
    public static Element xmlElement(Document d, List<Object> in) {
        if (in.size() != 3) return null ;
        String name = (String)in.get(0) ;
        Map<String,String> attrs = (Map<String,String>)in.get(1) ;
        List<Object> childrens = (List<Object>)in.get(2) ;
        Element element = d.createElement(name) ;
        attrs.entrySet().stream().forEach (e -> {
          Attr attr = d.createAttribute(e.getKey()) ;
          attr.setValue(e.getValue()) ;
          element.setAttributeNode(attr) ;
        }) ;
        childrens.stream().forEach (x -> {
          Element eChild = xmlElement(d, (List<Object>)x) ;
          element.appendChild(eChild) ;
        }) ;
        return element ;
    }
    public static String jsonString(Object v) 
    throws Exception {
        return new ObjectMapper().writeValueAsString(v) ;
    }
    public static Document xml(List<Object> in) 
    throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder() ;
        Document d = db.newDocument() ;
        d.appendChild(xmlElement(d, in)) ;
        return d ;
    }
    public static Document xml(InputStream in) 
    throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder() ;
        return db.parse(in) ;
    }
    public static InputStream inputStream(Document in) 
    throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream() ;
        Transformer tf = TransformerFactory.newInstance().newTransformer() ;
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        tf.transform(new DOMSource(in), new StreamResult(bout)) ;
        return new ByteArrayInputStream(bout.toByteArray()) ;
    }
    public static String urlPath(List<List<Object>> params) {
        return params.stream()
                     .map(xs -> xs.get(0) + "=" + xs.get(1))
                     .collect(Collectors.joining("&")) ;
    }
    public static String urlPath(Map<String, Object> params) {
        return params.entrySet().stream()
                      .map(e -> e.getKey()+"="+e.getValue().toString())
                      .collect(Collectors.joining("&")) ;
    }
    public static InputStream http(HttpMethod method, String url, InputStream params, Map<String, String> header)
    throws Exception {
        final Request req = (method == HttpMethod.GET) ? Request.Get(url) : Request.Post(url).bodyStream(params) ;
        req.viaProxy("http://localhost:8118") ;
        header.entrySet().stream().forEach(e -> req.addHeader(e.getKey(), e.getValue())) ;
        Method internalExecute = Request.class.getDeclaredMethod(
            "internalExecute", new Class[] { HttpClient.class, HttpContext.class }
        ) ;
        internalExecute.setAccessible(true) ;
        
        Constructor<Response> newResponse = Response.class.getDeclaredConstructor(HttpResponse.class) ;
        newResponse.setAccessible(true) ;
        try (CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(new SSLConnectionSocketFactory (
                SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build()
              , NoopHostnameVerifier.INSTANCE
              )).build()) {

          Response resp = newResponse.newInstance(internalExecute.invoke(req, httpClient, null)) ;
          return resp.returnContent().asStream() ;
        }
    }
    public static InputStream http(HttpMethod method, String url, InputStream params) 
    throws Exception {
        return http(method, url, params, stringmap()) ;
    }
    public static InputStream http(HttpMethod method, String url, Map<String, Object> params, Map<String, String> header)
    throws Exception {
        String urlFull = url + ((method == HttpMethod.GET) ? urlPath(params) : "");
        InputStream in = (method == HttpMethod.POST) ? new ByteArrayInputStream(new ObjectMapper().writeValueAsBytes(params)) : null ;
        return http(method, urlFull, in, header) ;
    }

    public static InputStream http(HttpMethod method, String url, Map<String, Object> params) 
    throws Exception {
        return http(method, url, params, stringmap());
    }

    public static Object httpJSON(HttpMethod method, String url, Map<String, Object> params, Map<String, String> header)
    throws Exception {
        if (method == HttpMethod.POST) header.put("Content-Type", "application/json");
        return new ObjectMapper().readValue(http(method, url, params, header), Object.class);
    }

    public static Object httpJSON(HttpMethod method,String url, Map<String, Object> params) 
    throws Exception {
        return httpJSON(method, url, params, stringmap());
    }
    public static Object httpXML(HttpMethod method,String url, List<Object> params) 
    throws Exception {
        Document d = xml(params) ;
        // return slurp(http(method, url, inputStream(d))) ;
        return xml(http(method, url, inputStream(d))) ;
    }
    public static Object httpJSONEmpty(HttpMethod method, String url, Map<String, String> header)
    throws Exception {
        return httpJSON(method, url, map(), header) ;
    }
    
    public static String httpString(HttpMethod method, String url, Map<String, Object> params, Map<String, String> header) 
    throws Exception {
        return slurp(http(method, url, params, header)) ;
    }
    public static String httpString(HttpMethod method, String url, Map<String, Object> params) 
    throws Exception {
        return httpString(method, url, params, stringmap()) ;
    }
    
    @SuppressWarnings("unchecked")
    public static Object jsonGetter(Object json, String path) {
        return Arrays.stream(path.split("\\.")).reduce(json
        , (v, name) -> {
            if (v instanceof Map) { return ((Map<Object, Object>) v).get(name) ; }
            return null ;
          }
        , (a,b) -> b
        ) ;
    }
    public static String jsonGetterString(Object json, String path) {
        return jsonGetter(json, path).toString() ;
    }
    public static String xmlGetterString(Object xml, String path) 
    throws Exception {
        return XPathFactory.newInstance().newXPath().compile(path).evaluate(xml) ;
    }

    public static void main(String[] args) throws Exception {
        // System.out.println(nowLocal()) ;
        // System.out.println(base64("larluo"));
        // System.out.println(base64("larluo")) ;
        // System.out.println(md5("larluo"));
        // System.out.println(sha256("larluo"));
         
    }
}
