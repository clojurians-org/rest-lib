package com.bumblebee.acquisition.exception;

/**
 * 爬虫自定义异常
 * <p>
 * Created by hua on 2016/12/29.
 */
public class CrawlException extends Exception {

    private static final long serialVersionUID = 1L;

    public CrawlException(Throwable cause) {
        super(cause);
    }

    public CrawlException(String message) {
        super(message);
    }
}
