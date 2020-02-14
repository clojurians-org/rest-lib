package com.bumblebee.acquisition.exception;

/**
 * http请求异常
 * <p>
 * Created by hua on 2016/12/29.
 */
public class HttpRequestException extends CrawlException {

    public HttpRequestException(Throwable cause) {
        super(cause);
    }

    public HttpRequestException(String message) {
        super(message);
    }
}
