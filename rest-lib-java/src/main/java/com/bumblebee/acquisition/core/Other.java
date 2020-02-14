package com.bumblebee.acquisition.core;

import com.bumblebee.acquisition.core.model.AcquisitionParameter;
import com.bumblebee.acquisition.core.model.Result;
import com.bumblebee.acquisition.exception.CrawlException;

/**
 * 其它自定义
 * Created by renhua.zhang on 2017/5/24.
 */
public interface Other {

    /**
     * 执行操作
     *
     * @param parameter 参数
     * @return 自定义result
     */
    Result execute(AcquisitionParameter parameter) throws CrawlException;

}
