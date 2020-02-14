package com.bumblebee.acquisition.fetch.hljk ;

import com.bumblebee.acquisition.core.Fetcher ;
import com.bumblebee.acquisition.core.model.AcquisitionParameter;
import com.bumblebee.acquisition.core.model.ExpensesFlowDetail ;
import com.bumblebee.acquisition.core.model.Result ;
import com.bumblebee.acquisition.exception.CrawlException ;

public class HljkFetch implements Fetcher {
    @Override
    public Result fetch(AcquisitionParameter ap, ExpensesFlowDetail ep)
    throws CrawlException {
        return null ;
    }
    @Override
    public Result test(AcquisitionParameter ap) { return null ;}

    public static void main(String[] args) {
        System.setProperty("http.proxyHost", "10.132.37.200") ;
        System.setProperty("http.proxyPort", "8118") ;
    }
}
