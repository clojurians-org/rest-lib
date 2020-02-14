package com.bumblebee.acquisition.exception;

/**
 * 内容解析异常
 * <p>
 * Created by hua on 2016/12/29.
 */
public class ParseException extends CrawlException {

    public ParseException(Throwable cause) {
        super(cause);
    }

    public ParseException(String message) {
        super(message);
    }
}
