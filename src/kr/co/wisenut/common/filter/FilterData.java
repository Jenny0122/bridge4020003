/*
 * @(#)FilterData.java 3.8.1 2009/03/11
 */
package kr.co.wisenut.common.filter;

import java.io.File;

import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.time.StopWatch;

/**
 * 
 * FilterData
 * 
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 * 
 * @author WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 * 
 */
public class FilterData extends Filter {
	private StringBuffer sbData = new StringBuffer(8192);
	private int MAX_FILTERED_LIMIT_SIZE = 1024 * 1024 * 3;
	
	/**
	 * FilterData Constructor
	 * @param filterDel
	 *            filtered file delete y/n
	 */
	public FilterData(Boolean filterDel) {
		super(filterDel);
	}

	public void setFilteredTextDir(String filteredTextDir) {
		super.filteredTextDir = filteredTextDir;

	}
	
	public String getFilterData(String[][] sourceFileInfo, FilterSource filter, String charset) {
		// print debug message

		String preFixDir = filter.getDir();
		String retrieve = filter.getRetrival();
		String condition = filter.getCondition();
		String jungumKey = filter.getJungumKey();
		String split = filter.getSplit();
		String filterType = filter.getFilterType();
		String seperator = filter.getSeperator();

		debug(sourceFileInfo, preFixDir, retrieve, condition, filterType, jungumKey);
		String[] srcFile = null;
		String targetFile = "";
		String filterDir = filteredTextDir;
		FileUtil.makeDir(filterDir);

		int sourceLen = sourceFileInfo.length;
		
		for (int i = 0; i < sourceLen; i++) {
			String chk_ext_condition = condition;
			if (sourceFileInfo[i][0] != null
					&& (sourceFileInfo[i].length > 1 || sourceFileInfo[i][0].lastIndexOf(".") == -1)) {
				chk_ext_condition = "none-chk-ext";
			}

			if (retrieve.equals("blob") && condition.equals("source-delete")) {
				chk_ext_condition = "none-chk-ext";
			}
			if (sourceFileInfo[i].length > 1 && !FileUtil.isFiltered(sourceFileInfo[i][1])) {
				sbData.append(" ");
				Log2.debug("[FilterData ] [None Filtered File Ext FileName or Ext Name,"
						+ " sourceFileInfo[i][1] is FileName or FileExt" + sourceFileInfo[i][1] + "]", 3);
			} else {
				// attach1 || split|| attach2 AS ATTACH processing
				if (split.equals("")) {
					srcFile = new String[] { sourceFileInfo[i][0] };
				} else {
					srcFile = StringUtil.split(sourceFileInfo[i][0], split);
				}
				int size = srcFile.length;
				int fieldNumber = 1;
				if (sourceLen > 1) {
					fieldNumber = sourceLen;
				}

				if (size > 0) {

					int maxLen = MAX_FILTERED_LIMIT_SIZE;
					if (charset.toLowerCase().equals("utf-8")) {
						maxLen = MAX_FILTERED_LIMIT_SIZE * 3;
					}
					maxLen = maxLen / (fieldNumber * size);

					for (int n = 0; n < size; n++) {
						File srcFileTmp;
						targetFile = filterDir + StringUtil.getTimeBasedUniqueID() + ".txt";
						if (srcFile[n] == null || srcFile[n].equals("")) {
							continue;
						}

						if (!preFixDir.equals("")) {
							srcFileTmp = new File(preFixDir, srcFile[n]);
						} else {
							srcFileTmp = new File(srcFile[n]);
						}

						Log2.debug("[FilterData] [filtering...]");
						StopWatch stopWatch = new StopWatch();
						stopWatch.start();
						if (filteringFile(srcFileTmp.getPath(), targetFile, chk_ext_condition, filterType, jungumKey,
								charset)) {
							if (!charset.toLowerCase().equals("utf-8")) {
								sbData.append(readTextFile(targetFile, maxLen)).append(seperator);
							} else {
								sbData.append(readTextFile(targetFile, maxLen, "UTF-16")).append(seperator);
							}
						} else {
							Log2.debug("[FilterData] [filtering failed. " + srcFileTmp.getPath() + "]");
						}
						stopWatch.stop();
						Log2.debug("[FilterData] [filtering completed. elapsed time : " + stopWatch.getTime()
								+ " msec]");

					}
				}
			}

			if (condition.equals("source-delete")) {
				int size = srcFile.length;
				if (size > 0) {
					for (int n = 0; n < size; n++) {
						// blob �곗씠��� 鍮꾩뼱��寃쎌슦 null 泥섎━. by jwlee 20091127
						if (srcFile[n] != null) {
							File file = null;
							if (!preFixDir.equals("")) {
								file = new File(preFixDir, srcFile[n]);
							} else {
								file = new File(srcFile[n]);
							}

							FileUtil.delete(file);
						}
					}
				}
			}
		}
		
		String sbString = sbData.toString();
		sbData.setLength(0);
		return sbString;
	}
}