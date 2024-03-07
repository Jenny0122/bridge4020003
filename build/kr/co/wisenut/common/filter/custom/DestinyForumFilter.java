/*
 * @(#)DestinyFilter.java 3.8.1 2009/03/11
 */

package kr.co.wisenut.common.filter.custom;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.filter.Filter;
import kr.co.wisenut.common.filter.custom.softforum.DocEncrypt;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.DateUtil;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.StringUtil;

/**
 * 
 * DestinyFilter
 * 
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 * 
 * @author WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 * 
 */
public class DestinyForumFilter extends Filter {
	private int MAX_FILTERED_LIMIT_SIZE = 1024 * 1024 * 3;

	/**
	 * FilterData Constructor
	 * @param filterDel
	 *            filtered file delete y/n
	 */
	public DestinyForumFilter(Boolean filterDel) {
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

		Log2.debug("[DestinyFilter] [Custum Filter -> kr.co.wisenut.common.filter.custom.DestinyFilter]", 4);
		// print debug message
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
			String chk_ext_condition = condition;
			if (sourceFileInfo[i][0] != null
					&& (sourceFileInfo[i].length > 1 || sourceFileInfo[i][0].lastIndexOf(".") == -1)) {
				chk_ext_condition = "none-chk-ext";
			}
			if (sourceFileInfo[i].length > 1 && !FileUtil.isFiltered(sourceFileInfo[i][1])) {
				sbData.append(" ");
				Log2.debug("[DestinyFilter ] [None Filtered File Ext FileName or Ext Name, "
						+ "sourceFileInfo[i][1] is FileName or FileExt" + sourceFileInfo[i][1] + "]", 3);
			} else {
				if (sourceFileInfo[i][0].length() > 15) {
					srcFile = preFixDir + DateUtil.getUnixTime2Year(sourceFileInfo[i][0]) + FileUtil.getFileSeperator()
							+ sourceFileInfo[i][0].substring(13, 16) + FileUtil.getFileSeperator()
							+ sourceFileInfo[i][0] + ".db";

					Log2.debug("[DestinyFilter ] [Destiny Filter: " + srcFile + "]", 3);

					targetFile = filterDir + StringUtil.getTimeBasedUniqueID() + ".txt";

					StringBuffer tmpSbData = new StringBuffer();
					if (filteringFile(srcFile, targetFile, chk_ext_condition)) {
						if (!charset.toLowerCase().equals("utf-8")) {
							tmpSbData.append(readTextFile(targetFile, maxLen)).append(" ");
							sbData = tmpSbData;
						} else {
							tmpSbData.append(readTextFile(targetFile, maxLen, "UTF-16")).append(" ");
							sbData = tmpSbData;
						}

						// SoftForum Encrypt
						DocEncrypt doc = new DocEncrypt(tmpSbData.toString());

						StringBuffer strEncode = new StringBuffer();

						if (doc.init()) {
							strEncode.append(doc.WNEncode());
						}
						doc.end();

						try {
							BufferedWriter bw = new BufferedWriter(new FileWriter(targetFile));
							bw.write(strEncode.toString());
							bw.close();
						} catch (IOException e) {
						}
					}
				}
			}

			if (condition.equals("source-delete")) { // delete attach binary file
				File file = new File(srcFile);
				if (file.exists()) {
					file.delete();
					Log2.debug("[DestinyFilter] [delete temporary file success.]", 4);
				}
			}
		}
		return sbData.toString();
	}
}
