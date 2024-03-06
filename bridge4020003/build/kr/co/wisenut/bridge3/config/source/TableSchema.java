/*
 * @(#)TableSchema.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.config.source;

/**
 *
 * TableSchema
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class TableSchema {
    private String type = "pk";
    private String logTbName = "";
    private String[] pkColunms = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLogTbName() {
        return logTbName;
    }

    public void setLogTbName(String logTbName) {
        this.logTbName = logTbName;
    }

    public String[] getPrimaryKeys() {
        return pkColunms;
    }

    public void setColumn(String[] pkColunms) {
        this.pkColunms = pkColunms;
    }
}
