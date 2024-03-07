/*
 * @(#)WSFilterData.java 3.8.1 2009/03/11
 */
package kr.co.wisenut.common.filter.custom;

import java.util.Calendar;
import java.util.Date;

import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.filter.Filter;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.StringUtil;

/**
 * 
 * WSFilterData
 * 
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 * 
 * @author WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 * 
 */
public class WSFilterData extends Filter {
	private int MAX_FILTERED_LIMIT_SIZE = 1024 * 1024 * 3;

	/**
	 * FilterData Constructor
	 * @param filterDel
	 *            filtered file delete y/n
	 */
	public WSFilterData(Boolean filterDel) {
		super(filterDel);
	}

	public void setFilteredTextDir(String filteredTextDir) {
		super.filteredTextDir = filteredTextDir;

	}

	public String getFilterData(String[][] sourceFileInfo, FilterSource filter, String charset) {
		String preFixDir = filter.getDir();
		String retrieve = filter.getRetrival();
		String condition = filter.getCondition();
		String jungumKey = filter.getJungumKey();
		String split = filter.getSplit();
		String filterType = filter.getFilterType();

		Log2.debug("[WSFilterData] [Custum Filter -> kr.co.wisenu.filter.custom.WSFilterData]", 4);
		debug(sourceFileInfo, preFixDir, retrieve, condition, filterType);

		String srcFile = "";
		String targetFile = "";
		// String filterDir = FileUtil.lastSeparator(m_wcse_home) + "Filter" + FileUtil.getFileSeperator();
		String filterDir = filteredTextDir;
		FileUtil.makeDir(filterDir);
		if (!preFixDir.equals("")) {
			preFixDir = FileUtil.lastSeparator(preFixDir);
		}
		if (charset.toLowerCase().equals("utf-8")) {
			MAX_FILTERED_LIMIT_SIZE = MAX_FILTERED_LIMIT_SIZE * 3;
		}
		int maxLen = MAX_FILTERED_LIMIT_SIZE / sourceFileInfo.length;

		StringBuffer sbData = new StringBuffer();
		for (int i = 0; i < sourceFileInfo.length; i++) {
			if (FileUtil.isFiltered(sourceFileInfo[i][1])) {
				srcFile = preFixDir + longTime2WyzPath(sourceFileInfo[i][0]);
				targetFile = filterDir + StringUtil.getTimeBasedUniqueID() + ".txt";
				if (filteringFile(srcFile, targetFile, condition)) {
					if (!charset.toLowerCase().equals("utf-8")) {
						sbData.append(readTextFile(targetFile, maxLen)).append(" ");
					} else {
						sbData.append(readTextFile(targetFile, maxLen, "UTF-16")).append(" ");
					}
				}
			}
		}
		return sbData.toString();
	}

	private String longTime2WyzPath(String longTime) {
		String wyzPath = "";
		if (longTime.length() > 15) {
			Date date = new Date();
			date.setTime(Long.parseLong(longTime.substring(0, 13)));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			wyzPath = Integer.toString(calendar.get(Calendar.YEAR)) + FileUtil.getFileSeperator();
			wyzPath += StringUtil.convertFormat(Integer.toString(calendar.get(Calendar.MONTH) + 1), "00");
			wyzPath += StringUtil.convertFormat(Integer.toString(calendar.get(Calendar.DATE)), "00")
					+ FileUtil.getFileSeperator();
			wyzPath += longTime.substring(12, 15) + FileUtil.getFileSeperator() + longTime + ".fre";
		}
		return wyzPath;
	}
}
