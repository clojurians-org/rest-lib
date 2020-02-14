package com.bumblebee.acquisition.core;

import com.bumblebee.acquisition.core.model.AcquisitionParameter;
import com.bumblebee.acquisition.core.model.Result;
import com.bumblebee.acquisition.exception.ValidationException;

/**
 * 数据解析接口 Created by renhua.zhang on 2017/5/23.
 */
public interface Parse {

    /**
     * 解析文本内容成对象
     *
     * @param parameter 数据采集参数
     *                  文本内容
     * @return ParseResult
     * @throws ValidationException
     */
    Result parse(AcquisitionParameter parameter, Result result) throws ValidationException;


}
