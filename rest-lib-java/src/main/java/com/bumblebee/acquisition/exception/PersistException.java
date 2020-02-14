package com.bumblebee.acquisition.exception;

/**
 * 内容解析异常
 * <p>
 * Created by hua on 2016/12/29.
 */
public class PersistException extends CrawlException {

    public PersistException(Throwable cause) {
        super(cause);
    }

    public PersistException(String message) {
        super(message);
    }
}
