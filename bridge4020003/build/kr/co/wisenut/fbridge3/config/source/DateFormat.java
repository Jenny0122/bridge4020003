/*
 * @(#)DateFormat.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3.config.source;

/**
 *
 * DateFormat
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class DateFormat {
    private int order = -1;
    private String format = "none";

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format.toLowerCase();
    }
}
