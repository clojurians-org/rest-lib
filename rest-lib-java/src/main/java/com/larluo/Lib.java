package com.larluo;

import java.lang.reflect.Method ;
import java.lang.reflect.Constructor ;
import java.math.BigInteger ;
import java.io.File ;
import java.io.IOException ;
import java.io.InputStream;
import java.io.FileInputStream ;
import java.net.URL ;

import java.util.Base64 ;
import java.util.Arrays ;
import java.util.List ;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.UUID ;
import java.util.function.Function;

import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.* ;
import java.net.URLEncoder ;

import java.time.LocalDateTime ;
import java.time.format.DateTimeFormatter ;

import org.apache.commons.io.IOUtils ;
import static org.apache.commons.io.IOUtils.* ;
import org.apache.commons.io.output.ByteArrayOutputStream ;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import org.apache.commons.codec.binary.Hex ;

import com.moandjiezana.toml.Toml ;
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
    public static class Resource {
        public URL v ;
        public Resource(URL v) { this.v = v; }
    }
    public static class Json {
        public Object v ;
        public Json(Object v) { this.v = v ;}
    }
    public static class Xml {
        public Document v ;
        public Xml(Document v) { this.v = v; }
    }
    public static enum HttpMethod {GET, POST} ;
    public static enum RSAMethod {PUBLIC,PRIVATE} ;

    public static Resource resource(String in) throws IOException {
        return new Resource(Lib.class.getClassLoader().getResource(in)) ;
    }
    public static InputStream inputStream(File in) throws IOException {
        return new FileInputStream(in) ;
    }
    public static InputStream inputStream(String in) throws Exception {
        return inputStream(new File(in)) ;
    }
    public static InputStream inputStream(Resource in) throws Exception {
        return in.v.openStream() ;
    }
    public static InputStream inputStream(Json json) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream() ;
        new ObjectMapper().writeValue(out, json.v) ;
        return out.toInputStream() ;
    }
    public static InputStream inputStream(Xml xml) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream() ;
        Transformer tf = TransformerFactory.newInstance().newTransformer() ;
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        tf.transform(new DOMSource(xml.v), new StreamResult(bout)) ;
        return bout.toInputStream() ;
    }
    public static String string(InputStream in) throws Exception { return IOUtils.toString(in, UTF_8) ; }
    public static String string(Json json)  {
        try { 
            return IOUtils.toString(inputStream(json), UTF_8) ;
        } catch (Exception e) { return null ; }
    }

    public static Json json(Object v) { return new Json(v); }
    public static Json json(InputStream in) {
        try {
          return new Json(new ObjectMapper().readValue(in, Object.class)) ;
        } catch (Exception e) { return null ; }
    }
    public static Json json(String s) { return json(toInputStream(s, UTF_8)) ; }

    @SuppressWarnings("unchecked")
    private static Element _xmlElement(Document d, List<Object> in) {
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
          Element eChild = _xmlElement(d, (List<Object>)x) ;
          element.appendChild(eChild) ;
        }) ;
        return element ;
    }

    public static Xml xml(InputStream in) 
    throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder() ;
        return new Xml(db.parse(in)) ;
    }
    public static Xml xml(List<Object> in) 
    throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder() ;
        Document d = db.newDocument() ;
        d.appendChild(_xmlElement(d, in)) ;
        return new Xml(d) ;
    }

    public static Toml toml(InputStream in) {
        return new Toml().read(in) ;
    }
    public static Toml toml(Resource res) throws Exception {
        return toml(inputStream(res)) ;
    }

    public static Map<String, Object> map(boolean keepNull, Object... xs) {
        int length = xs.length ;
        int offset =  0 ;
        Map<String, Object> result = new HashMap<String, Object>() ;
        while (offset < length) {
          Object v = xs[offset+1] ;
          if(v != null || keepNull) result.put(xs[offset].toString(), xs[offset+1]) ;
          offset += 2 ;
        }
        return result ;
    }
    public static Map<String, Object> map(Object... xs) {
        return map(true, xs) ;
    }
    public static Map<String, Object> optmap(Object... xs) {
        return map(false, xs) ;
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
    public static Map<String, String> stringmap (Map<String, Object> m) {
        Map<String, String> ret = stringmap() ;
        m.forEach( (k, v) -> ret.put(k, v.toString()) ) ;
        return ret ;
    }
    @SafeVarargs
    public static Map<String, Object> maps(Map<String, Object>... xs) {
        Map<String, Object> map = map() ;
        for (Map<String, Object> m : xs) {
            map.putAll(m) ;
        }
        return map ;
    }
    public static String trimGet(Map<String, String> m, String key) {
        String v = m.get(key) ;
        if (v == null) return null ;
        if (v.trim().equals("")) return null ;
        return v ;
    }
    public static String ensureGet(Map<String, String> m, String key) 
    throws Exception {
        String v = m.get(key) ;
        if (v == null) throw new Exception("DATA IS MISSING FOR KEY: " + key) ;
        return v ;
    }
    public static String uuid() { return UUID.randomUUID().toString() ; }
    public static String uuidShort() { return uuid().replaceAll("-","") ; }
    public static long nowVal() { return System.currentTimeMillis() ; }
    public static String now() { return Long.toString(nowVal()) ; }
    @Deprecated
    public static String nowString() { return now(); }
    public static String nowLocal() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) ;
    }
    public static String nowUTC() {
        return "" ;
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
        if (in == null) return null ;
        return md5Hex(in);
    }
    public static String upperMD5(String in) {
        return md5(in).toUpperCase() ;
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
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
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
    }

    public static String rsaDecrypt(RSAMethod method, String privateKey, int blockSize, String in) 
    throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding") ;
        cipher.init(Cipher.DECRYPT_MODE, rsaKey(method, privateKey)) ;

        byte[] inBytes = unbase64Bin(in.getBytes()) ;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
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
    }

    @Deprecated
    public static String jsonString(Object v) { return string(json(v)) ; }

    public static String urlPath(List<Object> xs) {
        int length = xs.size() ;
        int offset = 0 ;
        StringBuilder sb = new StringBuilder() ;
        while (offset < length) {
          if(offset != 0) sb.append("&") ;
          sb.append(String.format("%s=%s", xs.get(offset), xs.get(offset+1))) ;
          offset += 2 ;
        }
        return sb.toString() ;
    }
    public static String md5Path(Object... xs) {
        return md5(urlPath(Arrays.asList(xs))) ;
    }
    public static String urlPath(Map<String, Object> params) {
        return params.entrySet().stream()
                      .map(e -> e.getKey()+"="+e.getValue().toString())
                      .collect(Collectors.joining("&")) ;
    }
    public static InputStream http(HttpMethod method, String url, InputStream params, Map<String, String> header)
    throws Exception {
        final Request req = (method == HttpMethod.GET) ? Request.Get(url) : Request.Post(url).bodyStream(params) ;
        if ((System.getProperty("http.proxyHost") != null || System.getProperty("http.proxyPort") != null))  {
          req.viaProxy(String.format("http://%s:%s"
                       , System.getProperty("http.proxyHost", "127.0.0.1")
                       , System.getProperty("http.proxyPort", "8118"))) ;
        }
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
        String urlFull = url + ((method == HttpMethod.GET) ? "?" + urlPath(params) : "");
        InputStream in = (method == HttpMethod.POST) ? inputStream(json(params)) : null ;
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
        Xml d = xml(params) ;
        return xml(http(method, url, inputStream(d))) ;
    }
    public static Object httpJSONEmpty(HttpMethod method, String url, Map<String, String> header)
    throws Exception {
        return httpJSON(method, url, map(), header) ;
    }
    
    public static String httpString(HttpMethod method, String url, Map<String, Object> params, Map<String, String> header) 
    throws Exception {
        return string(http(method, url, params, header)) ;
    }
    public static String httpString(HttpMethod method, String url, Map<String, Object> params) 
    throws Exception {
        return httpString(method, url, params, stringmap()) ;
    }
    
    @SuppressWarnings("unchecked")
    public static Object jsonGetter(Object json, String path) {
        return Arrays.stream(path.split("\\.")).reduce(json
        , (v, name) -> {
            if (v != null && v instanceof Map) { return ((Map<Object, Object>) v).get(name) ; }
            return null ;
          }
        , (a,b) -> b
        ) ;
    }
    public static String jsonGetterString(Object json, String path) {
        Object v = jsonGetter(json, path) ;
        if (v == null) return null ;
        return v.toString() ;
    }
    public static String xmlGetterString(Object xml, String path) 
    throws Exception {
        return XPathFactory.newInstance().newXPath().compile(path).evaluate(xml) ;
    }

    public static String sm3(String in) 
    throws Exception {
        if (in == null) return null ;
        return new String(Hex.encodeHex(hash(in.getBytes()), false)) ;
    }

    /************************************************
     * INNER FUNCTION, PLEASE SKIP
     ************************************************/
    private static final String ivHexStr = "7380166f 4914b2b9 172442d7 da8a0600 a96f30bc 163138aa e38dee4d b0fb0e4e";
    private static final BigInteger IV = new BigInteger(ivHexStr.replaceAll(" ", ""), 16);
    private static final Integer Tj15 = Integer.valueOf("79cc4519", 16);
    private static final Integer Tj63 = Integer.valueOf("7a879d8a", 16);
    private static final byte[] FirstPadding = {(byte) 0x80};
    private static final byte[] ZeroPadding = {(byte) 0x00};
    private static Integer FF(Integer x, Integer y, Integer z, int j) {
        if (j >= 0 && j <= 15) {
            return Integer.valueOf(x.intValue() ^ y.intValue() ^ z.intValue());
        } else if (j >= 16 && j <= 63) {
            return Integer.valueOf((x.intValue() & y.intValue())
                    | (x.intValue() & z.intValue())
                    | (y.intValue() & z.intValue()));
        } else {
            throw new RuntimeException("data invalid");
        }
    }

    private static Integer GG(Integer x, Integer y, Integer z, int j) {
        if (j >= 0 && j <= 15) {
            return Integer.valueOf(x.intValue() ^ y.intValue() ^ z.intValue());
        } else if (j >= 16 && j <= 63) {
            return Integer.valueOf((x.intValue() & y.intValue())
                    | (~x.intValue() & z.intValue()));
        } else {
            throw new RuntimeException("data invalid");
        }
    }

    private static Integer P0(Integer x) {
        return Integer.valueOf(x.intValue()
                ^ Integer.rotateLeft(x.intValue(), 9)
                ^ Integer.rotateLeft(x.intValue(), 17));
    }

    private static Integer P1(Integer x) {
        return Integer.valueOf(x.intValue()
                ^ Integer.rotateLeft(x.intValue(), 15)
                ^ Integer.rotateLeft(x.intValue(), 23));
    }

    private static byte[] padding(byte[] source) throws IOException {
        if (source.length >= 0x2000000000000000l) {
            throw new RuntimeException("src data invalid.");
        }
        long l = source.length * 8;
        long k = 448 - (l + 1) % 512;
        if (k < 0) {
            k = k + 512;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
          baos.write(source);
          baos.write(FirstPadding);
          long i = k - 7;
          while (i > 0) {
              baos.write(ZeroPadding);
              i -= 8;
          }
          baos.write(long2bytes(l));
          return baos.toByteArray();
        }
    }

    private static byte[] long2bytes(long l) {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (l >>> ((7 - i) * 8));
        }
        return bytes;
    }

    private static byte[] hash(byte[] source) throws IOException {
        byte[] m1 = padding(source);
        int n = m1.length / (512 / 8);
        byte[] b;
        byte[] vi = IV.toByteArray();
        byte[] vi1 = null;
        for (int i = 0; i < n; i++) {
            b = Arrays.copyOfRange(m1, i * 64, (i + 1) * 64);
            vi1 = CF(vi, b);
            vi = vi1;
        }
        return vi1;
    }

    private static byte[] CF(byte[] vi, byte[] bi) throws IOException {
        int a, b, c, d, e, f, g, h;
        a = toInteger(vi, 0);
        b = toInteger(vi, 1);
        c = toInteger(vi, 2);
        d = toInteger(vi, 3);
        e = toInteger(vi, 4);
        f = toInteger(vi, 5);
        g = toInteger(vi, 6);
        h = toInteger(vi, 7);

        int[] w = new int[68];
        int[] w1 = new int[64];
        for (int i = 0; i < 16; i++) {
            w[i] = toInteger(bi, i);
        }
        for (int j = 16; j < 68; j++) {
            w[j] = P1(w[j - 16] ^ w[j - 9] ^ Integer.rotateLeft(w[j - 3], 15))
                    ^ Integer.rotateLeft(w[j - 13], 7) ^ w[j - 6];
        }
        for (int j = 0; j < 64; j++) {
            w1[j] = w[j] ^ w[j + 4];
        }
        int ss1, ss2, tt1, tt2;
        for (int j = 0; j < 64; j++) {
            ss1 = Integer.rotateLeft(Integer.rotateLeft(a, 12) + e + Integer.rotateLeft(T(j), j), 7);
            ss2 = ss1 ^ Integer.rotateLeft(a, 12);
            tt1 = FF(a, b, c, j) + d + ss2 + w1[j];
            tt2 = GG(e, f, g, j) + h + ss1 + w[j];
            d = c;
            c = Integer.rotateLeft(b, 9);
            b = a;
            a = tt1;
            h = g;
            g = Integer.rotateLeft(f, 19);
            f = e;
            e = P0(tt2);
        }
        byte[] v = toByteArray(a, b, c, d, e, f, g, h);
        for (int i = 0; i < v.length; i++) {
            v[i] = (byte) (v[i] ^ vi[i]);
        }
        return v;
    }

    private static int toInteger(byte[] source, int index) {
        byte[] sub = Arrays.copyOfRange(source, index * 4, (index + 1) * 4);
        char[] chars = Hex.encodeHex(sub, false);
        return Long.valueOf(new String(chars), 16).intValue();
    }

    private static byte[] toByteArray(int a, int b, int c, int d, int e, int f,
                                      int g, int h) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(32)) {
          baos.write(toByteArray(a));
          baos.write(toByteArray(b));
          baos.write(toByteArray(c));
          baos.write(toByteArray(d));
          baos.write(toByteArray(e));
          baos.write(toByteArray(f));
          baos.write(toByteArray(g));
          baos.write(toByteArray(h));
          return baos.toByteArray();
        }
    }

    private static byte[] toByteArray(int i) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte) (i >>> 24);
        byteArray[1] = (byte) ((i & 0xFFFFFF) >>> 16);
        byteArray[2] = (byte) ((i & 0xFFFF) >>> 8);
        byteArray[3] = (byte) (i & 0xFF);
        return byteArray;
    }

    private static int T(int j) {
        if (j >= 0 && j <= 15) {
            return Tj15.intValue();
        } else if (j >= 16 && j <= 63) {
            return Tj63.intValue();
        } else {
            throw new RuntimeException("data invalid");
        }
    }

    /********** MAIN FUNCTION *************/
    public static void main(String[] args) throws Exception {
        // System.out.println(nowLocal()) ;
        // System.out.println(base64("larluo"));
        // System.out.println(md5("larluo"));
        // System.out.println(sha256("larluo"));
        // System.out.println(sm3("6222088750761350_898440691390031_20921200")) ;
        System.out.println(toml(resource("metadata.toml"))) ;
    }
}
