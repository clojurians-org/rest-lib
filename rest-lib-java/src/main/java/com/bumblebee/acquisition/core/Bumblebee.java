package com.bumblebee.acquisition.core;

import com.bumblebee.acquisition.core.model.AcquisitionParameter;
import com.bumblebee.acquisition.exception.ValidationException;

/**
 * 大黄蜂标准接口
 * Created by hua on 2017/7/17.
 */
public interface Bumblebee {

    /**
     * 插件初始化
     *
     * @param pluginID  插件ID
     * @param parameter 插件参数
     */
    void setUp(String pluginID, AcquisitionParameter parameter) throws ValidationException;
}
