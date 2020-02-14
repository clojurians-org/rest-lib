package com.bumblebee.acquisition.core;

import com.bumblebee.acquisition.core.model.AcquisitionParameter;
import com.bumblebee.acquisition.core.model.ExpensesFlowDetail;
import com.bumblebee.acquisition.core.model.Result;
import com.bumblebee.acquisition.exception.CrawlException;

import java.util.List;

/**
 * 爬虫接口
 * Created by renhua.zhang on 2017/5/24.
 */
public interface Crawl {

    /**
     * 爬取数据
     *
     * @param parameter 数据采集参数
     * @return 数据
     */
    Result crawl(AcquisitionParameter parameter, List<ExpensesFlowDetail> expensesFlowDetails) throws CrawlException;

}
