/*
 * @(#)HandyAttachInfo.java   3.8.1 2009/03/11
 *
 */

package kr.co.wisenut.common.filter.custom.handy;

/**
 *
 * HandyAttachInfo
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class HandyAttachInfo {
    private String formID;
    private String wordType;
    private String[][] attachArray;

    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public String getWordType() {
        return wordType;
    }

    public void setWordType(String wordType) {
        this.wordType = wordType;
    }

    public String[][] getAttachArray() {
        return attachArray;
    }

    public void setAttachArray(String[][] attachArray) {
        this.attachArray = attachArray;
    }
}
