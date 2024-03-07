/*
 * @(#)SubQuery.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.config.source;

import java.util.HashMap;

import kr.co.wisenut.common.util.StringUtil;

/**
 *
 * SubQuery
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class SubQuery {
    private int order = -1;
    private String type = "";
    private String query = "";
    private String split = " ";
    private String seperator = " ";
    private int whereCnt = 1;
    private int maxCount = 0;
    private int subquerymode = -1;
    private String className = "";
    private boolean isStatement = false; //add 2006/02/13
    private String nullValue = "";
    
    /**
     * <SubMappingMap> > <SubMapping> closed
     * type : <String, String>, value : <index attrubute, className attribute>
     */
    private HashMap subMappingMap = null;

    public String getNullValue() 
    {
    	return nullValue;
    }
    public void setNullValue (String nullValue)
    {
    	this.nullValue = nullValue;
    }
    
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isStatement() {
        return isStatement;
    }

    public void setStatement(String statement) {
         if(statement.equalsIgnoreCase("y"))
            isStatement = true;
    }

    public int getSubquerymode() {
        return subquerymode;
    }

    public void setSubquerymode(int subquerymode) {
        this.subquerymode = subquerymode;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = StringUtil.trimDuplecateSpace(query);
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getSeperator() {
        return seperator;
    }

    public void setSeperator(String seperator) {
        this.seperator = seperator;
    }

    public int getWhereCnt() {
        return whereCnt;
    }

    public void setWhereCnt(int whereCnt) {
        this.whereCnt = whereCnt;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

	public HashMap getSubMappingMap() {
    	return subMappingMap;
    }

	public void setSubMappingMap(HashMap subMapping) {
    	this.subMappingMap = subMapping;
    }
    
    
}
