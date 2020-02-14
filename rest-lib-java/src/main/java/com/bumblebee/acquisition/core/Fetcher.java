package com.bumblebee.acquisition.core;

import com.bumblebee.acquisition.core.model.AcquisitionParameter;
import com.bumblebee.acquisition.core.model.ExpensesFlowDetail;
import com.bumblebee.acquisition.core.model.Result;
import com.bumblebee.acquisition.exception.CrawlException;

/**
 * 数据获取接口
 * Created by renhua.zhang on 2017/5/23.
 */
public interface Fetcher {

    /**
     * 获取数据
     *
     * @param parameter 数据采集参数
     * @return 数据
     */
    Result fetch(AcquisitionParameter parameter, ExpensesFlowDetail expensesFlowDetail) throws CrawlException;

    /**
     * 验证
     *
     * @param parameter 参数
     * @return 数据
     */
    Result test(AcquisitionParameter parameter) throws Exception;
}
