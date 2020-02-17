package com.bumblebee.acquisition.fetch.chinaunicom ;


import static com.larluo.Lib.* ;
import static com.larluo.Adapter.* ;

import static java.util.Arrays.asList ;
import static java.util.Optional.ofNullable;
import java.util.Map ;


import static java.util.stream.Collectors.joining ;

import com.bumblebee.acquisition.core.Fetcher ;
import com.bumblebee.acquisition.core.model.AcquisitionParameter;
import com.bumblebee.acquisition.core.model.ExpensesFlowDetail ;
import com.bumblebee.acquisition.core.model.Result ;
import com.bumblebee.acquisition.exception.CrawlException ;

public class ChinaUnicomFetch implements Fetcher {

    @Override
    public Result fetch(AcquisitionParameter ap, ExpensesFlowDetail ep)
    throws CrawlException {
        Map<String, String> basedParams = ap.getGroupParameter("based_params") ;
        Map<String, String> apiParams = ap.getGroupParameter("api_params") ;

        try {
            String authUrl = ensureGet(basedParams, "authUrl") ;
            Map<String, Object> authParams = map ( "username", ensureGet(basedParams, "username")
                                                 , "password", ensureGet(basedParams, "password") ) ;
            Object authJSON = httpJSON(HttpMethod.POST, authUrl, authParams) ;

            String token = jsonGetterString(authJSON, "data.token") ;
            if (token == null || token.equals("")) {
                ep.setValidFlg("F") ;
                return new Result(asList(ep), true, "验证失败: ERRMSG:" + jsonString(authJSON), "") ;              
            }
            String url = ensureGet(basedParams, "url") ;

            Map<String, Object> params = null ;
            if (url.endsWith("/3ElementsVerificationSimpleClearly")) {
                params = maps (
                  map ( "name", ensureGet(apiParams, "name") 
                      , "mobile", ensureGet(apiParams, "mobile") 
                      , "idCard", ensureGet(apiParams, "idCard")
                      )
                , optmap ( "idType", trimGet(apiParams, "idType") 
                         )
                ) ;
            } else if (url.endsWith("/wholenetThreeElementsVerify")) {
                params = maps (
                  map ( "name", ensureGet(apiParams, "name")
                      , "mobile", ensureGet(apiParams, "mobile")
                      , "idCard", ensureGet(apiParams, "idCard")
                      )
                , optmap ()
                ) ;
            } else if (url.endsWith("/wholenetTelStatus")) {
                params = maps (
                  map ( "mobile", ensureGet(apiParams, "mobile")
                      )
                , optmap ()
                ) ;
            } else if (url.endsWith("/wholenetTelOnlineTime")) {
                params = maps (
                  map ( "mobile", ensureGet(apiParams, "mobile")
                      )
                , optmap ()
                ) ;
            }

            Object apiJson = httpJSON(HttpMethod.POST, ensureGet(basedParams, "url"), params, stringmap("Authorization", token)) ;
            ep.setValidFlg("T") ;
            return new Result(asList(ep), true, "请求成功", jsonString(apiJson)) ;
        } catch (Exception e) {
            ep.setValidFlg("F") ;
            e.printStackTrace() ;
            return new Result(asList(ep), true, "请求异常: ERRMSG:" + e.getMessage(), "") ;
        }
    }
    @Override
    public Result test(AcquisitionParameter ap) { return null ;}

    public static void test_3ElementsVerificationSimpleClearly() 
    throws Exception {
        Object params = map (
          "header"
        , map ( "userName", "admin"
              , "interfaceId", "hx_getcreditinfoAv3"
              , "systemId", "000000"
              , "secret", "a0bf5d35c66de4b038cd9ca373b251a44"
              , "msgId", "20190109161140001")
        , "body"
        , map ( "name", "larluo"
              , "mobile", "18217307226" 
              , "idCard", "420822198907103718" )
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups",
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "authUrl", "value", "https://140.206.76.195/bigdata/sh-credit/auth/apilogin")
                                     , map( "name", "username", "value", "huaruibank")
                                     , map( "name", "password", "value", "hrb@1q2w3e")
                                     , map( "name", "url", "value", "https://140.206.76.195/bigdata/sh-credit/mobile/cu/3ElementsVerificationSimpleClearly")
                                     ) )
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ( "name", "name", "value", jsonGetter(params, "body.name") )
                                    , map ( "name", "mobile", "value", jsonGetter(params, "body.mobile") )
                                    , map ( "name", "idCard", "value", jsonGetter(params, "body.idCard") )
                                    , map ( "name", "idType", "value", jsonGetter(params, "body.idType") )
                                    ) )) )
        ) ;

        System.out.println( jsonString(params) ) ;
        System.out.println( jsonString(config) ) ;
        Result r1 = new ChinaUnicomFetch().fetch( acquisition(config), expense(params) ) ;
        System.out.println("Result.message:" + r1.getMessage()) ;
        System.out.println("Result.data:" +r1.getData()) ;
    }

    public static void test_wholenetThreeElementsVerify () 
    throws Exception {
        Object params = map (
          "header"
        , map ( "userName", "admin"
              , "interfaceId", "hx_getcreditinfoAv3"
              , "systemId", "000000"
              , "secret", "a0bf5d35c66de4b038cd9ca373b251a44"
              , "msgId", "20190109161140001")
        , "body"
        , map ( "name", "罗浩"
              , "mobile", "18217307226" 
              , "idCard", "420822198907103718" )
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups",
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "authUrl", "value", "https://140.206.76.195/bigdata/sh-credit/auth/apilogin")
                                     , map( "name", "username", "value", "huaruibank")
                                     , map( "name", "password", "value", "hrb@1q2w3e")
                                     , map( "name", "url", "value", "https://140.206.76.195/bigdata/sh-credit/cus/wholenetThreeElementsVerify")
                                     ) )
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ( "name", "name", "value", jsonGetter(params, "body.name") )
                                    , map ( "name", "mobile", "value", jsonGetter(params, "body.mobile") )
                                    , map ( "name", "idCard", "value", jsonGetter(params, "body.idCard") )
                                    ) )) )
        ) ;

        System.out.println( jsonString(params) ) ;
        System.out.println( jsonString(config) ) ;
        Result r1 = new ChinaUnicomFetch().fetch( acquisition(config), expense(params) ) ;
        System.out.println("Result.message:" + r1.getMessage()) ;
        System.out.println("Result.data:" +r1.getData()) ;
    }
    public static void test_wholenetTelStatus () 
    throws Exception {
        Object params = map (
          "header"
        , map ( "userName", "admin"
              , "interfaceId", "hx_getcreditinfoAv3"
              , "systemId", "000000"
              , "secret", "a0bf5d35c66de4b038cd9ca373b251a44"
              , "msgId", "20190109161140001")
        , "body"
        , map ( "name", "larluo"
              , "mobile", "18217307226" 
              , "idCard", "420822198907103718" )
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups",
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "authUrl", "value", "https://140.206.76.195/bigdata/sh-credit/auth/apilogin")
                                     , map( "name", "username", "value", "huaruibank")
                                     , map( "name", "password", "value", "hrb@1q2w3e")
                                     , map( "name", "url", "value", "https://140.206.76.195/bigdata/sh-credit/cus/wholenetTelStatus")
                                     ) )
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ( "name", "mobile", "value", jsonGetter(params, "body.mobile") )
                                    ) )) )
        ) ;

        System.out.println( jsonString(params) ) ;
        System.out.println( jsonString(config) ) ;
        Result r1 = new ChinaUnicomFetch().fetch( acquisition(config), expense(params) ) ;
        System.out.println("Result.message:" + r1.getMessage()) ;
        System.out.println("Result.data:" +r1.getData()) ;

    }
    public static void test_wholenetTelOnlineTime () 
    throws Exception {
        Object params = map (
          "header"
        , map ( "userName", "admin"
              , "interfaceId", "hx_getcreditinfoAv3"
              , "systemId", "000000"
              , "secret", "a0bf5d35c66de4b038cd9ca373b251a44"
              , "msgId", "20190109161140001")
        , "body"
        , map ( "name", "larluo"
              , "mobile", "18217307226" 
              , "idCard", "420822198907103718" )
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups",
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "authUrl", "value", "https://140.206.76.195/bigdata/sh-credit/auth/apilogin")
                                     , map( "name", "username", "value", "huaruibank")
                                     , map( "name", "password", "value", "hrb@1q2w3e")
                                     , map( "name", "url", "value", "https://140.206.76.195/bigdata/sh-credit/cus/wholenetTelOnlineTime")
                                     ) )
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ( "name", "mobile", "value", jsonGetter(params, "body.mobile") )
                                    ) )) )
        ) ;

        System.out.println( jsonString(params) ) ;
        System.out.println( jsonString(config) ) ;
        Result r1 = new ChinaUnicomFetch().fetch( acquisition(config), expense(params) ) ;
        System.out.println("Result.message:" + r1.getMessage()) ;
        System.out.println("Result.data:" +r1.getData()) ;

    }
    public static void main(String[] args) 
    throws Exception {
        System.setProperty("http.proxyHost", "10.132.37.200") ;
        System.setProperty("http.proxyPort", "8118") ;

        // test_3ElementsVerificationSimpleClearly () ;
        // test_wholenetThreeElementsVerify () ;
        // test_wholenetTelStatus () ;
        test_wholenetTelOnlineTime () ;

        System.out.println("finished") ;
    }
}
