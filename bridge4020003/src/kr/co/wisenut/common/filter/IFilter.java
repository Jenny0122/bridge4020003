/*
 * @(#)IFilter.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.filter;

import kr.co.wisenut.common.filter.FilterSource;

/**
 *
 * IFilter
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public interface IFilter {

    public static final String FILTER_EXE = "snf_exe";

    //public static final int MAX_FILTER_SIZE = 1024 * 1024 * 3; // 3MB

    public static final long MAX_WATE_TIME = 10000 * 60 * 5;       // filter 5Min Wate Time
    
    public String getFilterData(String[][] sourceFileInfo, FilterSource filter, String charset);

    /*public boolean filteringFile(String sourceFile, String targetFile, String condition);

    public boolean filteringFile(String sourceFile, String targetFile, String condition, String filter);
*/
    public String readTextFile(String sourceFile, int readSize);
    
    /**
     * Set filtered text directory.
     * @param filteredTextDir
     */
    public void setFilteredTextDir(String filteredTextDir);
    
}
