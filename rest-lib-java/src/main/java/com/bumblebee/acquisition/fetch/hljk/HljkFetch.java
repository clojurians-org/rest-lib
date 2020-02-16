package com.bumblebee.acquisition.fetch.hljk ;

import static com.larluo.Lib.* ;
import static com.larluo.Adapter.* ;

import static java.util.Arrays.asList ;
import java.util.Map ;

import com.bumblebee.acquisition.core.Fetcher ;
import com.bumblebee.acquisition.core.model.AcquisitionParameter;
import com.bumblebee.acquisition.core.model.ExpensesFlowDetail ;
import com.bumblebee.acquisition.core.model.Result ;
import com.bumblebee.acquisition.exception.CrawlException ;

public class HljkFetch implements Fetcher {

    @Override
    public Result fetch(AcquisitionParameter ap, ExpensesFlowDetail ep)
    throws CrawlException {
        Map<String, String> basedParams = ap.getGroupParameter("based_params") ;
        Map<String, String> apiParams = ap.getGroupParameter("api_params") ;

        try {
            String nowLocal = nowLocal() ;
            Map<String, Object> data = maps (
              map ( "encryptMobile", upperMD5(ensureGet(apiParams, "mobile"))
                  , "idcard", upperMD5(ensureGet(apiParams, "idcard"))
                  , "name", upperMD5(ensureGet(apiParams, "name")) )
            , optmap ( "setmealCode", trimGet(apiParams, "setmealCode")
                     , "tags", trimGet(apiParams, "tags") 
                     , "recallMonth", trimGet(apiParams, "recallMonth")
                     , "encryptMethod", trimGet(apiParams, "encryptMethod") )
            ) ;
            String dataRSA = rsaEncrypt(RSAMethod.PUBLIC, ensureGet(basedParams, "pubkey"), 117, jsonString(data)) ;
            
            Map<String, Object> params = map (
              "channel", ensureGet(basedParams, "channel")
            , "requestTime", nowLocal
            , "data", dataRSA
            , "sign"
            , md5Path(
                "channel", ensureGet(basedParams, "channel")
              , "data", dataRSA
              , "key", md5Path("password", ensureGet(basedParams, "password"), "requestTime", nowLocal)
              )) ;
            
            ep.setValidFlg("T") ;
            String apiJSON = jsonString(httpJSON(HttpMethod.POST, ensureGet(basedParams, "url"), params)) ;
            return new Result(asList(ep), true, "请求成功", apiJSON) ;
        } catch (Exception e) {
            ep.setValidFlg("F") ;
            e.printStackTrace() ;
            return new Result(asList(ep), true, "请求异常: ERRMSG:" + e.getMessage(), "") ;
        }
    }
    @Override
    public Result test(AcquisitionParameter ap) { return null ;}

    public static void main(String[] args) 
    throws Exception {
        System.setProperty("http.proxyHost", "10.132.37.200") ;
        System.setProperty("http.proxyPort", "8118") ;

        Object params = map (
          "header"
        , map ( "userName", "admin"
              , "interfaceId", "hx_getcreditinfoAv3"
              , "systemId", "000000"
              , "secret", "a0bf5d35c66de4b038cd9ca373b251a44"
              , "msgId", "20190109161140001")
        , "body"
        , map ("mobile", "18217307226", "idcard", "420822198907103718", "name", "larluo")
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups",
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "url", "value", "http://103.235.244.183/v1/open/tag")
                                     , map( "name", "pubkey", "value", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDBc5wi9MBleCKMu+KPDN6lwG9YOMAC2LlIAL/OaEelzLBiF+klwdsMRkQyrBvhw9w6aS0qbw24Y2HGMYJiGuZbTLFCD/3YZuyDqDDbziO9aM09PvLuu+C3r4VvGAToY3CSb4RDnHO4X7pf+v6b9qDtKxA2WPRJxp4ldUZ1u+S/VQIDAQAB") 
                                     , map( "name", "channel", "value", "RpVi1580970pV133068")
                                     , map( "name", "password", "value", "dbeW1580970be133069")
                                      ) )
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ( "name", "mobile", "value", jsonGetter(params, "body.mobile") )
                                    , map ( "name", "idcard", "value", jsonGetter(params, "body.idcard") )
                                    , map ( "name", "name", "value", jsonGetter(params, "body.name") )
                                    , map ( "name", "setmealCode", "value", jsonGetter(params, "body.setmealCode") )
                                    , map ( "name", "tags", "value", jsonGetter(params, "body.tags") )
                                    , map ( "name", "recallMonth", "value", jsonGetter(params, "body.recallMonth") )
                                    , map ( "name", "encryptMethod", "value", jsonGetter(params, "body.encryptMethod") )
                                    ) )) )
        ) ;

        System.out.println( jsonString(params) ) ;
        System.out.println( jsonString(config) ) ;

        System.out.println("running fetch plugin...");
        Result r = new HljkFetch().fetch( acquisition(config), expense(params) ) ;
        System.out.println("Result.message:" + r.getMessage()) ;
        System.out.println("Result.data:" +r.getData()) ;
        System.out.println("finished") ;
    }
}
