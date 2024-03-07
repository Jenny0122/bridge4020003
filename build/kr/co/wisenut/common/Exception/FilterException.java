/*
 * @(#)FilterException.java   3.8.1 2009/03/11
 *
 */

package kr.co.wisenut.common.Exception;

/**
 *
 * FilterException
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class FilterException extends Exception{
    protected String message = null;
    protected Throwable throwable = null;

    public FilterException() {
        this(null, null);
    }

    public FilterException(String message) {
        this(message, null);
    }

    public FilterException(Throwable throwable) {
        this(null, throwable);
    }

    public FilterException(String message, Throwable throwable) {
        super();
        this.message = message;
        this.throwable = throwable;
    }

    public String getMessage() {
        return (message);
    }

    public Throwable getThrowable() {
        return (throwable);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (message != null) {
            sb.append(message);
            if (throwable != null) {
                sb.append(":  ");
            }
        }
        if (throwable != null) {
            sb.append(throwable.toString());
        }
        return (sb.toString());
    }
}
