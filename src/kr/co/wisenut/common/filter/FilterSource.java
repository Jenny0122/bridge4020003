/*
 * @(#)FilterSource.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.filter;

import kr.co.wisenut.common.util.StringUtil;

/**
 *
 * FilterSource
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class FilterSource {
    private int order = -1;
    private String filterType = "all";
    private String retrival = "local";
    private String condition = "";
    private String dir = "";
    private String fileext = "";


    private String jungumKey = "";
    private String query = "";
    private String serverid = "";
    private int whereCnt = 1;
    private int useFilteringCnt = 1;
    private String seperator = " ";
    private String split = " ";
    private String className = "";
    private boolean isStatement = false;  //Add 2006/02/13
    
    private int maxrow = -1;

    public boolean isStatement() {
        return isStatement;
    }

    public void setStatement(String statement) {
         if(statement.equalsIgnoreCase("y"))
            isStatement = true;
    }

    public int getWhereCnt() {
        return whereCnt;
    }

    public void setWhereCnt(int whereCnt) {
        this.whereCnt = whereCnt;
    }

    public int getUseFilteringCnt() {
        return useFilteringCnt;
    }

    public void setUseFilteringCnt(int useFilteringCnt) {
        this.useFilteringCnt = useFilteringCnt;
    }
    
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getRetrival() {
        return retrival;
    }

    public void setRetrival(String retrival) {
        this.retrival = retrival;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFileext() {
        return fileext;
    }

    public void setFileext(String fileext) {
        this.fileext = fileext;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = StringUtil.trimDuplecateSpace(query);
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }

    public String getSeperator() {
        return seperator;
    }

    public void setSeperator(String seperator) {
        this.seperator = seperator;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split.trim();
    }

    public String getJungumKey() {
        return jungumKey;
    }

    public void setJungumKey(String jungumKey) {
        this.jungumKey = jungumKey;
    }
    
    public int getMaxrow() {
    	return maxrow;
    }
    public void setMaxrow(int maxrow) {
    	this.maxrow = maxrow;
    }
}
