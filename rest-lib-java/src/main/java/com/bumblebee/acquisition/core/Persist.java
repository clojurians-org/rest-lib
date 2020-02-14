package com.bumblebee.acquisition.core;

import com.bumblebee.acquisition.core.model.AcquisitionParameter;
import com.bumblebee.acquisition.core.model.Result;
import com.bumblebee.acquisition.exception.PersistException;

/**
 * 持久化接口
 * Created by hua on 2017/5/29.
 */
public interface Persist extends Bumblebee {

    /**
     * 持久化
     *
     * @param parameter 数据采集参数
     * @param result    解析返回对象
     */
    Result persist(AcquisitionParameter parameter, Result result) throws PersistException;

}
