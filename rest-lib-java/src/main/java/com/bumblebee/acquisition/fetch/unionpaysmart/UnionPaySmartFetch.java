package com.bumblebee.acquisition.fetch.unionpaysmart ;

import static com.larluo.Lib.* ;
import static com.larluo.Adapter.* ;

import com.larluo.Lib ;
import static java.util.Arrays.asList ;
import static java.util.Optional.ofNullable;
import java.util.Map ;


import static java.util.stream.Collectors.joining ;

import com.bumblebee.acquisition.core.Fetcher ;
import com.bumblebee.acquisition.core.model.AcquisitionParameter;
import com.bumblebee.acquisition.core.model.ExpensesFlowDetail ;
import com.bumblebee.acquisition.core.model.Result ;
import com.bumblebee.acquisition.exception.CrawlException ;

public class UnionPaySmartFetch implements Fetcher {

    @Override
    public Result fetch(AcquisitionParameter ap, ExpensesFlowDetail ep)
    throws CrawlException {
        Map<String, String> basedParams = ap.getGroupParameter("based_params") ;
        Map<String, String> apiParams = ap.getGroupParameter("api_params") ;

        try {
            String url = ensureGet(basedParams, "url") ;

            Map<String, Object> data = null ;
            if (url.endsWith("/index/personal")) {
                String card = ensureGet(apiParams, "card") ;
                data = maps (
                  map ( "card", sm3(card.length() + card) )
                , optmap ( "orderId", uuidShort()
                         , "name", sm3(trimGet(apiParams, "name"))
                         , "indices", trimGet(apiParams, "indices")
                         , "identityType", trimGet(apiParams, "identityType")
                         , "identityCard", sm3(trimGet(apiParams, "identityCard")) 
                         , "mobile", sm3(trimGet(apiParams, "mobile"))
                         , "email", trimGet(apiParams, "email")
                         , "address", trimGet(apiParams, "address"))
                ) ;
            } else if (url.endsWith("/index/merchant")) {
                data = maps (
                  map ( "mid", sm3(ensureGet(apiParams, "mid")) )
                , optmap ( "pid", sm3(trimGet(apiParams, "pid"))
                         , "orderId", uuidShort()
                         , "indicies", trimGet(apiParams, "indicies")
                         , "mname", trimGet(apiParams, "mname")
                         , "regNo", trimGet(apiParams, "regNo")
                         , "legalName", trimGet(apiParams, "legalName") )
                ) ;
            } else if (url.endsWith("/score/super")) {
                data = maps (
                  map ( "orderId", uuidShort()
                      , "name", md5(ensureGet(apiParams, "name"))
                      , "cid", md5(ensureGet(apiParams, "cid")))
                , optmap ( "card", ofNullable(trimGet(apiParams, "card")).map ( e ->
                                     asList(e.split(",")).stream().map(Lib::md5).collect(joining(","))
                                   ).orElse(null)
                         , "mobile", md5(trimGet(apiParams, "mobile"))
                         , "address", trimGet(apiParams, "address")
                         )
                ) ;
            }
            String dataRSA = rsaEncrypt(RSAMethod.PRIVATE, ensureGet(basedParams, "myPrivate"), 245, jsonString(data)) ;
            
            Map<String, Object> params = map (
              "account", ensureGet(basedParams, "account")
            , "encrypt", dataRSA
            , "sign", md5(ensureGet(basedParams, "account") + dataRSA) ) ;
            
            ep.setValidFlg("T") ;
            Object encryptJSON = httpJSON(HttpMethod.POST, ensureGet(basedParams, "url"), params) ;
            String encryptStr = jsonGetterString(encryptJSON, "encrypt") ;
            if (encryptStr == null) {
                ep.setValidFlg("F") ;
                return new Result(asList(ep), true, "请求失败: ERRMSG:" + jsonString(encryptJSON), "") ;
            }
            String apiJsonStr = rsaDecrypt(RSAMethod.PUBLIC, ensureGet(basedParams, "serverPublic"), 256, encryptStr) ;
            ep.setValidFlg("T") ;
            return new Result(asList(ep), true, "请求成功", apiJsonStr) ;
        } catch (Exception e) {
            ep.setValidFlg("F") ;
            e.printStackTrace() ;
            return new Result(asList(ep), true, "请求异常: ERRMSG:" + e.getMessage(), "") ;
        }
    }
    @Override
    public Result test(AcquisitionParameter ap) { return null ;}

    public static void test_index_personal() 
    throws Exception {
        Object params = map (
          "header"
        , map ( "userName", "admin"
              , "interfaceId", "hx_getcreditinfoAv3"
              , "systemId", "000000"
              , "secret", "a0bf5d35c66de4b038cd9ca373b251a44"
              , "msgId", "20190109161140001")
        , "body"
        , map ("card", "6217850800017832563")
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups",
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "url", "value", "https://warcraft-test.unionpaysmart.com/index/personal")
                                     , map( "name", "account", "value", "T3010011")
                                     , map( "name", "myPrivate", "value", "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCa6duaXYP5KfoT0cF2obKdUs5zkCAfBNnt1uNXzvuBQJQCdpmfjW0Q1ck0bViOJE7+/5nLhn1cMgmOpH9W93C1nqQKJsFP1WL/GiKDZDILltrUuyAIpc0XJxeyNSt9EASasC/DB0/DXBHBl8Yv8Couhy8evWmQ6nyqV9awFysOynnRYrvHTcv+cpHiHXoW/eXiMDEFtQizb6NZ5XWfIaRtHdor8dSHfBkiOtLwvEUJgUvqpbTQOTDMAmc4WMYRWa+hulR1VvExvh1fzT/goI4AYBf36vIvQMfNmxggHnvVYY5YqNv4xVc67spgrJG5sKB/7Qt5e7k615Mjw5am6s1TAgMBAAECggEAAbpYC7m9ThsswenWCZe2IVEaImnaId5w+9kowgL6BTAONpTSE9iE/eoUi3Qox7UZ+HgrY/zFCzE3sBKfpP2vkScK114lS9mqeAP3nJWShVSSbksECXSn+/kgp4k2SffSxVLvxXIgbqJpREqIGGLKZah2WxZrxgUHjPLb3yzy+wA8tvr1YC1Va/gInzJjEPyXocNWc5tw7H4nXFksnl6QHag68CdAzn8sLn3QY9YGHSSUEN040T3GLNcZKlkYhYV9Fo6Y+cGBhW3mRKrolyMWLOiX0hma+ZlS2sZW0SgM2BHHYBa+CGC+3nMrp0Dp2Ryz1g/3JcRd3pPQheRzhKJ5qQKBgQDRCdhKaozS8aR8uyvLWlh4HBI/xuoJHsBeHgGhDFZm0GjUIUBq6KdhOv5EY5vpu6rfqfFUmYuL2Ddwo10YVb+yFDiZLzO9WFRW/zsgIxh5j1iLNy96SFxHiDkLl3xF6yPZ8pYiBwi/LyjKVTzdSSdLqrvjHAzl+rVhPvfJSBp0xwKBgQC9tzIRAicF5tLHU2rC6v4qD1IMOvpwuGDgMvKM8O99+QpD5M3LEKenj6/Q3NQHi+6Dj8ALTTwIMgiY674WlAZXMHZWpCyPoC6yN4jtpSFcPDrGS0OsAs3t2c1azl74llLpU3iX7qmYrY3uYsoFraEp1wM1EOh5RHJbUhGzEyv/FQKBgGx/j6QQaceTZKzY+YIzAnhVGaPAuvjA3Vf8exv8K1xyD/cKy0SyPS5aUF9u1B4dKjOtIXWnvKZ5M01jfY6D81ua40WfGlvM3PnKwAWOMcXmgS7BFgOCS2NVcUZy+i3QPmuBXiuLOx8Ae0uJsm2o6px7ocnfZVJ5LmurIzF2G0VBAoGAcDsZ4Tn2v+UHOGe+tsSxy0v468dQNLK+jKNXHc3CwDE9w2Bt5f1aUSQNjG+29yfu1+GIaKcuJmMC9wQvFchH3/iJaUQCII1QUpnL5VpU2+29j2UjCXU3QcghVeDUChdTwThiFO9ZYTr6xdWnUaifuNK70P7JqEO3qxJn6hojYukCgYBDcuHySdGbiyV6j0QipACqd1Sxl6/L57m/DuHNAaPQ7YF7QArwKq1Eg7tFZmky6a/22NTlJ9mPz6gRESz8Xv01xfIed+2cHdBnCVOep0ANOjhZAKjleqhR/U67hezJHWm322fVbIeFJSHBJOztWtRsRiGBm/GZI2/KeTRPtdF9Uw==")
                                     , map( "name", "serverPublic", "value", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4bCkrxzCcTA6RDWrYKscBkg3vCsfybfbcuMO6opLoJiYgrXKFPdqFiqlQyU0f/zAJoNXhl0+jKNYS5ZiF8noESepJMU6SELMbprPMVjUemWfjX9KmHTMigbFdygf/9zg+3Xk+7QP974+Z7ZnZ+Rjno95XBnXEGuuKr8mfivo8zauMJkUkYWsBvLOA5iG6rV5WcA86XvqzuBORypIzI4eqb+1cR9LYF35/W+2DWYlLr/I+V9DucZOsPwFN02Bl3DPFnpIi4JGp/xM8eDQerkjzHGk45xpJnl7qebCgwnWTQ8pAWc/jGAWl51K4b3jv5HXmlbI+fT8nuqtLAgXRoB15wIDAQAB")
                                     ) )
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ( "name", "card", "value", jsonGetter(params, "body.card") )
                                    ) )) )
        ) ;

        System.out.println( jsonString(params) ) ;
        System.out.println( jsonString(config) ) ;
        Result r1 = new UnionPaySmartFetch().fetch( acquisition(config), expense(params) ) ;
        System.out.println("Result.message:" + r1.getMessage()) ;
        System.out.println("Result.data:" +r1.getData()) ;
    }

    public static void test_index_merchant() 
    throws Exception {
        Object params = map (
          "header"
        , map ( "userName", "admin"
              , "interfaceId", "hx_getcreditinfoAv3"
              , "systemId", "000000"
              , "secret", "a0bf5d35c66de4b038cd9ca373b251a44"
              , "msgId", "20190109161140001")
        , "body"
        , map ("mid", "350181600194514")
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups",
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "url", "value", "https://warcraft-test.unionpaysmart.com/index/merchant")
                                     , map( "name", "account", "value", "T3010011")
                                     , map( "name", "myPrivate", "value", "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCa6duaXYP5KfoT0cF2obKdUs5zkCAfBNnt1uNXzvuBQJQCdpmfjW0Q1ck0bViOJE7+/5nLhn1cMgmOpH9W93C1nqQKJsFP1WL/GiKDZDILltrUuyAIpc0XJxeyNSt9EASasC/DB0/DXBHBl8Yv8Couhy8evWmQ6nyqV9awFysOynnRYrvHTcv+cpHiHXoW/eXiMDEFtQizb6NZ5XWfIaRtHdor8dSHfBkiOtLwvEUJgUvqpbTQOTDMAmc4WMYRWa+hulR1VvExvh1fzT/goI4AYBf36vIvQMfNmxggHnvVYY5YqNv4xVc67spgrJG5sKB/7Qt5e7k615Mjw5am6s1TAgMBAAECggEAAbpYC7m9ThsswenWCZe2IVEaImnaId5w+9kowgL6BTAONpTSE9iE/eoUi3Qox7UZ+HgrY/zFCzE3sBKfpP2vkScK114lS9mqeAP3nJWShVSSbksECXSn+/kgp4k2SffSxVLvxXIgbqJpREqIGGLKZah2WxZrxgUHjPLb3yzy+wA8tvr1YC1Va/gInzJjEPyXocNWc5tw7H4nXFksnl6QHag68CdAzn8sLn3QY9YGHSSUEN040T3GLNcZKlkYhYV9Fo6Y+cGBhW3mRKrolyMWLOiX0hma+ZlS2sZW0SgM2BHHYBa+CGC+3nMrp0Dp2Ryz1g/3JcRd3pPQheRzhKJ5qQKBgQDRCdhKaozS8aR8uyvLWlh4HBI/xuoJHsBeHgGhDFZm0GjUIUBq6KdhOv5EY5vpu6rfqfFUmYuL2Ddwo10YVb+yFDiZLzO9WFRW/zsgIxh5j1iLNy96SFxHiDkLl3xF6yPZ8pYiBwi/LyjKVTzdSSdLqrvjHAzl+rVhPvfJSBp0xwKBgQC9tzIRAicF5tLHU2rC6v4qD1IMOvpwuGDgMvKM8O99+QpD5M3LEKenj6/Q3NQHi+6Dj8ALTTwIMgiY674WlAZXMHZWpCyPoC6yN4jtpSFcPDrGS0OsAs3t2c1azl74llLpU3iX7qmYrY3uYsoFraEp1wM1EOh5RHJbUhGzEyv/FQKBgGx/j6QQaceTZKzY+YIzAnhVGaPAuvjA3Vf8exv8K1xyD/cKy0SyPS5aUF9u1B4dKjOtIXWnvKZ5M01jfY6D81ua40WfGlvM3PnKwAWOMcXmgS7BFgOCS2NVcUZy+i3QPmuBXiuLOx8Ae0uJsm2o6px7ocnfZVJ5LmurIzF2G0VBAoGAcDsZ4Tn2v+UHOGe+tsSxy0v468dQNLK+jKNXHc3CwDE9w2Bt5f1aUSQNjG+29yfu1+GIaKcuJmMC9wQvFchH3/iJaUQCII1QUpnL5VpU2+29j2UjCXU3QcghVeDUChdTwThiFO9ZYTr6xdWnUaifuNK70P7JqEO3qxJn6hojYukCgYBDcuHySdGbiyV6j0QipACqd1Sxl6/L57m/DuHNAaPQ7YF7QArwKq1Eg7tFZmky6a/22NTlJ9mPz6gRESz8Xv01xfIed+2cHdBnCVOep0ANOjhZAKjleqhR/U67hezJHWm322fVbIeFJSHBJOztWtRsRiGBm/GZI2/KeTRPtdF9Uw==")
                                     , map( "name", "serverPublic", "value", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4bCkrxzCcTA6RDWrYKscBkg3vCsfybfbcuMO6opLoJiYgrXKFPdqFiqlQyU0f/zAJoNXhl0+jKNYS5ZiF8noESepJMU6SELMbprPMVjUemWfjX9KmHTMigbFdygf/9zg+3Xk+7QP974+Z7ZnZ+Rjno95XBnXEGuuKr8mfivo8zauMJkUkYWsBvLOA5iG6rV5WcA86XvqzuBORypIzI4eqb+1cR9LYF35/W+2DWYlLr/I+V9DucZOsPwFN02Bl3DPFnpIi4JGp/xM8eDQerkjzHGk45xpJnl7qebCgwnWTQ8pAWc/jGAWl51K4b3jv5HXmlbI+fT8nuqtLAgXRoB15wIDAQAB")
                                     ) )
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ( "name", "mid", "value", jsonGetter(params, "body.mid") )
                                    ) )) )
        ) ;

        System.out.println( jsonString(params) ) ;
        System.out.println( jsonString(config) ) ;
        Result r1 = new UnionPaySmartFetch().fetch( acquisition(config), expense(params) ) ;
        System.out.println("Result.message:" + r1.getMessage()) ;
        System.out.println("Result.data:" +r1.getData()) ;
    }
    public static void test_score_super() 
    throws Exception {
        Object params = map (
          "header"
        , map ( "userName", "admin"
              , "interfaceId", "hx_getcreditinfoAv3"
              , "systemId", "000000"
              , "secret", "a0bf5d35c66de4b038cd9ca373b251a44"
              , "msgId", "20190109161140001")
        , "body"
        , map ( "cid", "522627199205170415"
              , "name", "陆曙")
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups",
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "url", "value", "https://warcraft-test.unionpaysmart.com/score/super")
                                     , map( "name", "account", "value", "T3010011")
                                     , map( "name", "myPrivate", "value", "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCa6duaXYP5KfoT0cF2obKdUs5zkCAfBNnt1uNXzvuBQJQCdpmfjW0Q1ck0bViOJE7+/5nLhn1cMgmOpH9W93C1nqQKJsFP1WL/GiKDZDILltrUuyAIpc0XJxeyNSt9EASasC/DB0/DXBHBl8Yv8Couhy8evWmQ6nyqV9awFysOynnRYrvHTcv+cpHiHXoW/eXiMDEFtQizb6NZ5XWfIaRtHdor8dSHfBkiOtLwvEUJgUvqpbTQOTDMAmc4WMYRWa+hulR1VvExvh1fzT/goI4AYBf36vIvQMfNmxggHnvVYY5YqNv4xVc67spgrJG5sKB/7Qt5e7k615Mjw5am6s1TAgMBAAECggEAAbpYC7m9ThsswenWCZe2IVEaImnaId5w+9kowgL6BTAONpTSE9iE/eoUi3Qox7UZ+HgrY/zFCzE3sBKfpP2vkScK114lS9mqeAP3nJWShVSSbksECXSn+/kgp4k2SffSxVLvxXIgbqJpREqIGGLKZah2WxZrxgUHjPLb3yzy+wA8tvr1YC1Va/gInzJjEPyXocNWc5tw7H4nXFksnl6QHag68CdAzn8sLn3QY9YGHSSUEN040T3GLNcZKlkYhYV9Fo6Y+cGBhW3mRKrolyMWLOiX0hma+ZlS2sZW0SgM2BHHYBa+CGC+3nMrp0Dp2Ryz1g/3JcRd3pPQheRzhKJ5qQKBgQDRCdhKaozS8aR8uyvLWlh4HBI/xuoJHsBeHgGhDFZm0GjUIUBq6KdhOv5EY5vpu6rfqfFUmYuL2Ddwo10YVb+yFDiZLzO9WFRW/zsgIxh5j1iLNy96SFxHiDkLl3xF6yPZ8pYiBwi/LyjKVTzdSSdLqrvjHAzl+rVhPvfJSBp0xwKBgQC9tzIRAicF5tLHU2rC6v4qD1IMOvpwuGDgMvKM8O99+QpD5M3LEKenj6/Q3NQHi+6Dj8ALTTwIMgiY674WlAZXMHZWpCyPoC6yN4jtpSFcPDrGS0OsAs3t2c1azl74llLpU3iX7qmYrY3uYsoFraEp1wM1EOh5RHJbUhGzEyv/FQKBgGx/j6QQaceTZKzY+YIzAnhVGaPAuvjA3Vf8exv8K1xyD/cKy0SyPS5aUF9u1B4dKjOtIXWnvKZ5M01jfY6D81ua40WfGlvM3PnKwAWOMcXmgS7BFgOCS2NVcUZy+i3QPmuBXiuLOx8Ae0uJsm2o6px7ocnfZVJ5LmurIzF2G0VBAoGAcDsZ4Tn2v+UHOGe+tsSxy0v468dQNLK+jKNXHc3CwDE9w2Bt5f1aUSQNjG+29yfu1+GIaKcuJmMC9wQvFchH3/iJaUQCII1QUpnL5VpU2+29j2UjCXU3QcghVeDUChdTwThiFO9ZYTr6xdWnUaifuNK70P7JqEO3qxJn6hojYukCgYBDcuHySdGbiyV6j0QipACqd1Sxl6/L57m/DuHNAaPQ7YF7QArwKq1Eg7tFZmky6a/22NTlJ9mPz6gRESz8Xv01xfIed+2cHdBnCVOep0ANOjhZAKjleqhR/U67hezJHWm322fVbIeFJSHBJOztWtRsRiGBm/GZI2/KeTRPtdF9Uw==")
                                     , map( "name", "serverPublic", "value", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4bCkrxzCcTA6RDWrYKscBkg3vCsfybfbcuMO6opLoJiYgrXKFPdqFiqlQyU0f/zAJoNXhl0+jKNYS5ZiF8noESepJMU6SELMbprPMVjUemWfjX9KmHTMigbFdygf/9zg+3Xk+7QP974+Z7ZnZ+Rjno95XBnXEGuuKr8mfivo8zauMJkUkYWsBvLOA5iG6rV5WcA86XvqzuBORypIzI4eqb+1cR9LYF35/W+2DWYlLr/I+V9DucZOsPwFN02Bl3DPFnpIi4JGp/xM8eDQerkjzHGk45xpJnl7qebCgwnWTQ8pAWc/jGAWl51K4b3jv5HXmlbI+fT8nuqtLAgXRoB15wIDAQAB")
                                     ) )
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ( "name", "cid", "value", jsonGetter(params, "body.cid") )
                                    , map ( "name", "name", "value", jsonGetter(params, "body.name") )
                                    ) )) )
        ) ;

        System.out.println( jsonString(params) ) ;
        System.out.println( jsonString(config) ) ;
        Result r1 = new UnionPaySmartFetch().fetch( acquisition(config), expense(params) ) ;
        System.out.println("Result.message:" + r1.getMessage()) ;
        System.out.println("Result.data:" +r1.getData()) ;
    }

    public static void main(String[] args) 
    throws Exception {
        System.setProperty("http.proxyHost", "10.132.37.200") ;
        System.setProperty("http.proxyPort", "8118") ;


        // test_index_personal () ;
        // test_index_merchant () ;
        test_score_super() ;
        System.out.println("finished") ;
    }
}
