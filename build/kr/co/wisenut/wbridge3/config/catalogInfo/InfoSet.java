/*
 * @(#)InfoSet.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.config.catalogInfo;

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
    private String className = "0x00";
    private int maxLen = 0;
    private String convert = "";
    private String metaInfo = "";
    private String userTag = "";
    private String type = "" ;
    private String typeValue = "";
    private boolean useTitleTag = false;
    private boolean htmlParse = true;
    
    public boolean isUseTitleTag() {
        return useTitleTag;
    }

    public void setUseTitleTag(String titleTag) {
        if(titleTag.toLowerCase().equals("y")){
            this.useTitleTag = true;
        }
    }
    
    public boolean isHtmlParse() {
    	return htmlParse;
    }
    
    public void setHtmlParse(String html) {
    	if(html.toLowerCase().equals("n")) {
    		this.htmlParse = false;
    	}
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserTag() {
        return userTag;
    }

    public void setUserTag(String userTag) {
        this.userTag = userTag;
    }

    public String getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(String metaInfo) {
        this.metaInfo = metaInfo;
    }

    /**
     * Get Catalog Info Collumn Number Funtion
     * @return  collumn
     */
    public int getCollumn() {
        return collumn;
    }

    /**
     * Set Catalog Info Collumn Number Funtion
     * @param collumn
     */
    public void setCollumn(int collumn) {
        this.collumn = collumn;
    }

    /**
     * Get Catalog Info Filed Name Funtion
     * @return   fieldName
     */
    public String getTagName() {
        return fieldName;
    }

    /**
     *  Set Catalog Info Filed Name Funtion
     * @param fieldName
     */
    public void setTagName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Get Catalog Info Alias Field Function
     * @return isSourceAlias
     */
    public boolean isSourceAlias() {
        return isSourceAlias;
    }

    /**
     * Set Catalog Info Alias Field Function
     * @param sourceAlias
     */
    public void setSourceAlias(String sourceAlias) {
        if(sourceAlias.equalsIgnoreCase("y"))
            isSourceAlias = true;
    }

    /**
     * Get Catalog Info Custom Class Field Function
     * @return   className
     */
    public String getClassName() {
        return className;
    }

    /**
     *  Set Catalog Info Custom Class Field Function
     * @param className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Get Catalog Info Field  Length Function
     * @return maxLen
     */
    public int getMaxLen() {
        return maxLen;
    }

    /**
     * Set Catalog Info Field  Length Function
     * @param maxLen
     */
    public void setMaxLen(int maxLen) {
        this.maxLen = maxLen;
    }

    /**
     * Get Catalog Info CharSet Convert Field  Function
     * @return  convert
     */
    public String getConvert() {
        return convert;
    }

    /**
     * Set Catalog Info CharSet Convert Field  Function
     * @param convert
     */
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
