package com.larluo ;


import static com.larluo.Lib.* ;
import static java.util.Arrays.asList ;
// import com.larluo.internal.ExternalInterfaceDTO; 
// import com.larluo.internal.IntegratedParameterDTO;

import java.util.List ;
import java.util.Map ;
import java.util.Date ;

import java.util.stream.Collectors;

import com.bumblebee.acquisition.core.model.AcquisitionParameter;
import com.bumblebee.acquisition.core.model.ExpensesFlowDetail;
import com.bumblebee.acquisition.util.Pair ;

public class Adapter {
    public static ExpensesFlowDetail expense(Object params) {
        ExpensesFlowDetail ep = new ExpensesFlowDetail () ;
        ep.setSystemId( jsonGetterString(params, "header.systemId") );
        ep.setInterfaceId( jsonGetterString(params, "header.interfaceId") ) ;
        ep.setUserId( jsonGetterString(params, "header.userName") ) ;
        ep.setCreateDt(new Date()) ;
        ep.setMsgid( jsonGetterString(params, "header.msgId") ) ;
        ep.setTranUserId( jsonGetterString(params, "header.userName") ) ;
        ep.setIscached("F") ;
        ep.setValidFlg("T") ;
        return ep ;
    }

    @SuppressWarnings("unchecked")
    public static AcquisitionParameter acquisition(Object in) {
        AcquisitionParameter ap = new AcquisitionParameter() ;
        ((List<Object>)jsonGetter(in, "fetcherPlugin.pluginParameterGroups")).stream()
          .forEach(v -> 
             ap.put( jsonGetterString(v, "name")
                   , ((List<Object>)jsonGetter(v, "parameters")).stream()
                       .map(m -> 
                          new Pair<>( jsonGetterString(m, "name")
                                    , jsonGetterString(m, "value")))
                       .collect(Collectors.toList()) )) ;
        
        return ap ;
    }
    public static void main(String[] args) {
        Object params = map (
          "header"
        , map 
              ( "userName", "admin"
              , "interfaceId", "hx_getcreditinfoAv3"
              , "systemId", "000000"
              , "secret", "a0bf5d35c66de4b038cd9ca373b251a44"
              , "msgId", "20190109161140001")
        , "body"
        , map ("idnum", "1", "name", "ren", "encry", "3")
        ) ;
        Object config = map (
          "fetcherPlugin",
          map ( "pluginID", "fetch-xxx"
              , "pluginParameterGroups", 
                asList ( map( "name", "based_params"
                            , "parameters"
                            , asList ( map( "name", "url", "value", "https://api.xiaoheer.com:443/ws/report/PSAScore.asmx/FlightScoreReport") 
                                      ) ) 
                       , map( "name", "api_params"
                            , "parameters"
                            , asList( map ("name", "Hashcode", "value", "682cac52d8904671b1ef0b8e2bea4e33") 
                                    , map ("name", "mobile", "value", "${mobile}") ) )) )
        ) ;
        // Map<String, Map<String, String>> params = (Map<String, Map<String, String>>) paramsRaw ;
        // ExternalInterfaceDTO eif  = externalInterfaceDTO(params, null) ;
        // System.out.println(params.get("header").get("msgId")) ;
        System.out.println(acquisition(config).toMap()) ;
        
    }
}
