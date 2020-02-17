package com.bumblebee.acquisition.fetch.fahai ;

import static com.larluo.Lib.* ;
import static com.larluo.Adapter.* ;

// import com.larluo.Lib ;
import static java.util.Arrays.asList ;
import static java.util.Optional.ofNullable;
import java.util.Map ;


import static java.util.stream.Collectors.joining ;

import com.bumblebee.acquisition.core.Fetcher ;
import com.bumblebee.acquisition.core.model.AcquisitionParameter;
import com.bumblebee.acquisition.core.model.ExpensesFlowDetail ;
import com.bumblebee.acquisition.core.model.Result ;
import com.bumblebee.acquisition.exception.CrawlException ;

public class FahaiFetch implements Fetcher {

    @Override
    public Result fetch(AcquisitionParameter ap, ExpensesFlowDetail ep)
    throws CrawlException {
        Map<String, String> basedParams = ap.getGroupParameter("based_params") ;
        Map<String, String> apiParams = ap.getGroupParameter("api_params") ;

        try {
            String url = ensureGet(basedParams, "url") ;

            Map<String, Object> params = null ;
            if (url.endsWith("/vip/query/sifa")) {
                String nowStr = nowString() ;
                params = map ( 
                  "domain", "sifa"
                , "authCode", ensureGet(basedParams, "authCode")
                , "rt", nowStr
                , "sign", md5(ensureGet(basedParams, "authCode") + nowStr)
                , "args"
                , urlEncode( jsonString( maps (
                    map ("keyword", ensureGet(apiParams, "keyword"))
                  , optmap (
                      "dataType", trimGet(apiParams, "dataType")
                    , "pageno", ofNullable(trimGet(apiParams, "pageno")).map(Integer::parseInt).orElse(null)
                    , "range", ofNullable(trimGet(apiParams, "range")).map(Integer::parseInt).orElse(null)
                    )
                  )))
                ) ;
            } else if(url.endsWith("/vip/person/sifa")) {
                String nowStr = nowString() ;
                params = map ( 
                  "domain", "sifa"
                , "authCode", ensureGet(basedParams, "authCode")
                , "rt", nowStr
                , "sign", md5(ensureGet(basedParams, "authCode") + nowStr)
                , "args"
                , urlEncode( jsonString (maps (
                    map ( "name", ensureGet(apiParams, "name")
                        , "idcardNo", ensureGet(apiParams, "idcardNo")
                        )
                  , optmap (
                      "dataType", trimGet(apiParams, "dataType")
                    , "pageno", ofNullable(trimGet(apiParams, "pageno")).map(Integer::parseInt).orElse(null)
                    , "range", ofNullable(trimGet(apiParams, "range")).map(Integer::parseInt).orElse(null)
                    )
                  )))
                ) ;
            } else if (url.endsWith("/vip/query/sat")) {
                String nowStr = nowString() ;
                params = map ( 
                  "domain", "sat"
                , "authCode", ensureGet(basedParams, "authCode")
                , "rt", nowStr
                , "sign", md5(ensureGet(basedParams, "authCode") + nowStr)
                , "args"
                , urlEncode( jsonString ( maps (
                    map ("keyword", ensureGet(apiParams, "keyword"))
                  , optmap (
                      "dataType", trimGet(apiParams, "dataType")
                    , "pageno", ofNullable(trimGet(apiParams, "pageno")).map(Integer::parseInt).orElse(null)
                    , "range", ofNullable(trimGet(apiParams, "range")).map(Integer::parseInt).orElse(null)
                    )
                  )))
                ) ;
            } else if (url.contains("/vip/export/:dimension")) {
                // cpws, ktgg, zxgg, shixin, fygg, ajlc, bgt, sifacdk, pmgg
                // satparty_qs, satparty_chufa, satparty_fzc, satparty_xin
                url = url.replace(":dimension", ensureGet(apiParams, "dimension")) ;
                String nowStr = nowString() ;
                params = map ( 
                  "domain", "sifa"
                , "authCode", ensureGet(basedParams, "authCode")
                , "rt", nowStr
                , "sign", md5(ensureGet(basedParams, "authCode") + nowStr)
                , "id", ensureGet(apiParams, "id")
                ) ;
            } else {
                throw new Exception("INTERFACE URL NOT IMPLEMENTED:" + url) ;
            }

            Object apiJson = httpJSON(HttpMethod.GET, url, params) ;
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

    public static void test_vip_query_sifa() 
    throws Exception {
        Object params = map (
          "header"
        , map ( "userName", "admin"
              , "interfaceId", "hx_getcreditinfoAv3"
              , "systemId", "000000"
              , "secret", "a0bf5d35c66de4b038cd9ca373b251a44"
              , "msgId", "20190109161140001")
        , "body"
        , map ("keyword", "larluo")
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups",
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "url", "value", "https://api.fahaicc.com/vip/query/sifa")
                                     , map( "name", "authCode", "value", "uLbde9xmFDj3SeejyHu3")
                                     ) )
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ( "name", "keyword", "value", jsonGetter(params, "body.keyword") )
                                    , map ( "name", "dataType", "value", jsonGetter(params, "body.dataType") ) 
                                    , map ( "name", "pageno", "value", jsonGetter(params, "body.pageno") ) 
                                    , map ( "name", "range", "value", jsonGetter(params, "body.range") )
                                    ) )) )
        ) ;

        System.out.println( jsonString(params) ) ;
        System.out.println( jsonString(config) ) ;
        Result r1 = new FahaiFetch().fetch( acquisition(config), expense(params) ) ;
        System.out.println("Result.message:" + r1.getMessage()) ;
        System.out.println("Result.data:" +r1.getData()) ;
    }
    public static void test_vip_personal_sifa() 
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
              , "idcardNo", "420822198907103718")
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups",
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "url", "value", "https://api.fahaicc.com/vip/person/sifa")
                                     , map( "name", "authCode", "value", "uLbde9xmFDj3SeejyHu3")
                                     ) )
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ( "name", "name", "value", jsonGetter(params, "body.name") )
                                    , map ( "name", "idcardNo", "value", jsonGetter(params, "body.idcardNo") ) 
                                    , map ( "name", "dataType", "value", jsonGetter(params, "body.dataType") )
                                    , map ( "name", "pageno", "value", jsonGetter(params, "body.pageno") ) 
                                    , map ( "name", "range", "value", jsonGetter(params, "body.range") )
                                    ) )) )
        ) ;

        System.out.println( jsonString(params) ) ;
        System.out.println( jsonString(config) ) ;
        Result r1 = new FahaiFetch().fetch( acquisition(config), expense(params) ) ;
        System.out.println("Result.message:" + r1.getMessage()) ;
        System.out.println("Result.data:" +r1.getData()) ;
    }
    public static void test_vip_sat() 
    throws Exception {
        Object params = map (
          "header"
        , map ( "userName", "admin"
              , "interfaceId", "hx_getcreditinfoAv3"
              , "systemId", "000000"
              , "secret", "a0bf5d35c66de4b038cd9ca373b251a44"
              , "msgId", "20190109161140001")
        , "body"
        , map ("keyword", "larluo")
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups",
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "url", "value", "https://api.fahaicc.com/vip/query/sat")
                                     , map( "name", "authCode", "value", "uLbde9xmFDj3SeejyHu3")
                                     ) )
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ( "name", "keyword", "value", jsonGetter(params, "body.keyword") )
                                    , map ( "name", "dataType", "value", jsonGetter(params, "body.dataType") ) 
                                    , map ( "name", "pageno", "value", jsonGetter(params, "body.pageno") ) 
                                    , map ( "name", "range", "value", jsonGetter(params, "body.range") )
                                    ) )) )
        ) ;

        System.out.println( jsonString(params) ) ;
        System.out.println( jsonString(config) ) ;
        Result r1 = new FahaiFetch().fetch( acquisition(config), expense(params) ) ;
        System.out.println("Result.message:" + r1.getMessage()) ;
        System.out.println("Result.data:" +r1.getData()) ;
    }
    public static void test_vip_export_sifa() 
    throws Exception {
        Object params = map (
          "header"
        , map ( "userName", "admin"
              , "interfaceId", "hx_getcreditinfoAv3"
              , "systemId", "000000"
              , "secret", "a0bf5d35c66de4b038cd9ca373b251a44"
              , "msgId", "20190109161140001")
        , "body"
        , map ( "dimension", "cpws"
              , "id", "larluo")
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups",
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "url", "value", "https://api.fahaicc.com/vip/export/:dimension")
                                     , map( "name", "authCode", "value", "uLbde9xmFDj3SeejyHu3")
                                     ) )
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ( "name", "dimension", "value", jsonGetter(params, "body.dimension") )
                                    , map ( "name", "id", "value", jsonGetter(params, "body.id") )
                                    ) )) )
        ) ;

        System.out.println( jsonString(params) ) ;
        System.out.println( jsonString(config) ) ;
        Result r1 = new FahaiFetch().fetch( acquisition(config), expense(params) ) ;
        System.out.println("Result.message:" + r1.getMessage()) ;
        System.out.println("Result.data:" +r1.getData()) ;

    }
    public static void test_vip_export_sat() 
    throws Exception {
        Object params = map (
          "header"
        , map ( "userName", "admin"
              , "interfaceId", "hx_getcreditinfoAv3"
              , "systemId", "000000"
              , "secret", "a0bf5d35c66de4b038cd9ca373b251a44"
              , "msgId", "20190109161140001")
        , "body"
        , map ( "dimension", "satparty_qs"
              , "id", "larluo")
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups",
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "url", "value", "https://api.fahaicc.com/vip/export/:dimension")
                                     , map( "name", "authCode", "value", "uLbde9xmFDj3SeejyHu3")
                                     ) )
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ( "name", "dimension", "value", jsonGetter(params, "body.dimension") )
                                    , map ( "name", "id", "value", jsonGetter(params, "body.id") )
                                    ) )) )
        ) ;

        System.out.println( jsonString(params) ) ;
        System.out.println( jsonString(config) ) ;
        Result r1 = new FahaiFetch().fetch( acquisition(config), expense(params) ) ;
        System.out.println("Result.message:" + r1.getMessage()) ;
        System.out.println("Result.data:" +r1.getData()) ;

    }

    public static void main(String[] args) 
    throws Exception {
        System.setProperty("http.proxyHost", "10.132.37.200") ;
        System.setProperty("http.proxyPort", "8118") ;

        // test_vip_query_sifa() ;
        // test_vip_personal_sifa() ;
        // test_vip_sat() ;
        // test_vip_export_sifa() ;
        test_vip_export_sat() ;
      
        System.out.println("finished") ;
    }
}
