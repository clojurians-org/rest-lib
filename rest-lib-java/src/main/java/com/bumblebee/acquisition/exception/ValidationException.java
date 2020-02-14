package com.bumblebee.acquisition.exception;

/**
 * 参数以及数据格式校验异常
 * <p>
 * Created by hua on 2016/12/29.
 */
public class ValidationException extends CrawlException {

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message) {
        super(message);
    }
}
