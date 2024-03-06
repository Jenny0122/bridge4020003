/*
 * @(#)InfoSet.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3.config.catalogInfo;

/**
 *
 * InfoSet
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class InfoSet {
    private int collumn = 0;
    private String fieldName = "";
    private boolean isSourceAlias = false;
    private boolean isHtml = false;
    private String className = "0x00";
    private String type = "";
    private String typeValue = "";
    private int maxLen = 0;
    private String convert = "";

    public boolean isHtml() {
        return isHtml;
    }

    public void setHtml(String html) {
        if(html.equalsIgnoreCase("y"))
            isHtml = true;
    }

    public int getCollumn() {
        return collumn;
    }

    public void setCollumn(int collumn) {
        this.collumn = collumn;
    }

    public String getTagName() {
        return fieldName;
    }

    public void setTagName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean isSourceAlias() {
        return isSourceAlias;
    }

    public void setSourceAlias(String sourceAlias) {
        if(sourceAlias.equalsIgnoreCase("y"))
            isSourceAlias = true;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(int maxLen) {
        this.maxLen = maxLen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConvert() {
        return convert;
    }

    public void setConvert(String convert) {
        this.convert = convert;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }


}
