/*
 * @(#)Http2FilterData.java 3.8.1 2009/03/11
 */
package kr.co.wisenut.common.filter.custom;

import java.io.File;

import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.filter.Filter;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.HttpDownLoad;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.io.IOUtil;

/**
 * 
 * Http2FilterData
 * 
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 * 
 * @author WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 * 
 */
public class Http2FilterData extends Filter {
	private int MAX_FILTERED_LIMIT_SIZE = 1024 * 1024 * 3;

	/**
	 * FilterData Constructor
	 * @param filterDel
	 *            filtered file delete y/n
	 */
	public Http2FilterData(Boolean filterDel) {
		super(filterDel);
	}

	public void setFilteredTextDir(String filteredTextDir) {
		this.filteredTextDir = filteredTextDir;
	}

	public String getFilterData(String[][] sourceFileInfo, FilterSource filter, String charset) {
		String preFixDir = filter.getDir();
		String retrieve = filter.getRetrival();
		String condition = filter.getCondition();
		String jungumKey = filter.getJungumKey();
		String split = filter.getSplit();
		String filterType = filter.getFilterType();

		Log2.debug("[Http2FilterData] [Custum Filter -> kr.co.wisenut.common.filter.custom.Http2FilterData]", 4);
		debug(sourceFileInfo, preFixDir, retrieve, condition, filterType);

		String[] srcURL = null;
		String sourceFile = "";
		String targetFile = "";
		// String filterDir = FileUtil.lastSeparator(m_wcse_home) + "Filter" + FileUtil.getFileSeperator();
		String filterDir = filteredTextDir;
		FileUtil.makeDir(filterDir);
		if (!preFixDir.equals("")) {
			preFixDir = FileUtil.lastSeparator(preFixDir);
		}

		/*
		 * if( charset.toLowerCase().equals("utf-8") ) {
		 * MAX_FILTERED_LIMIT_SIZE = 1024 * 1024 * 3;
		 * MAX_FILTERED_LIMIT_SIZE = MAX_FILTERED_LIMIT_SIZE * 3 ;
		 * }
		 * int maxLen = MAX_FILTERED_LIMIT_SIZE / sourceFileInfo.length;
		 */
		int maxLen = MAX_FILTERED_LIMIT_SIZE;
		if (charset.toLowerCase().equals("utf-8")) {
			maxLen = MAX_FILTERED_LIMIT_SIZE * 3;
		}
		maxLen = maxLen / sourceFileInfo.length;

		StringBuffer sbData = new StringBuffer();
		int sourceFileLen = sourceFileInfo.length;

		for (int i = 0; i < sourceFileLen; i++) {
			String chk_ext_condition = condition;
			if (sourceFileInfo[i][0] == null) {
				continue;
			}
			if (sourceFileInfo[i].length > 1 || sourceFileInfo[i][0].lastIndexOf(".") == -1) {
				chk_ext_condition = "none-chk-ext";
			}
			if (sourceFileInfo[i].length > 1 && !FileUtil.isFiltered(sourceFileInfo[i][1])) {
				sbData.append(" ");
				Log2.debug("[Filter ] [None Filtered File Ext FileName or Ext Name, sourceFileInfo[i][1] is "
						+ "FileName or FileExt" + sourceFileInfo[i][1] + "]", 3);
			} else {
				if (!retrieve.equalsIgnoreCase("url")) {
					Log2.debug("[Filter ] [retrive type = " + retrieve + "]", 3);
					continue;
				}
				if (!isHttp(sourceFileInfo[i][0])) {
					sbData.append(" ");
					continue;
				}
				if (split.equals("")) {
					srcURL = new String[] { sourceFileInfo[i][0] };
				} else {
					srcURL = StringUtil.split(sourceFileInfo[i][0], split);
				}
				int size = srcURL.length;
				if (size > 0) {
					for (int n = 0; n < size; n++) {
						// sourceFile = getExtractHttpToFileName(preFixDir, srcURL[n]);
						// 2009년 9월 21일 새로운 클래스로 변경 by ikcho
						int spos = srcURL[n].lastIndexOf("/");
						if (spos > -1) {
							sourceFile = srcURL[n].substring(spos + 1, srcURL[n].length());
							// preFixDir
							int paramIdx = sourceFile.lastIndexOf("?");
							if (paramIdx > -1) {
								// http://www.changwon.go.kr/FileDownload?p_nm=mb&f_nm=%BE%C8%B3%BB%B9%AE%28%B0%A1%C1%B7%B3%EE%C0%CC%29.hwp&groupNo=10097
								sourceFile = sourceFile.substring(paramIdx + 1, sourceFile.length());
								String[] params = StringUtil.split(sourceFile, "&");
								int length = params.length;
								for (int k = 0; k < length; k++) {
									int lastIndexOfPeriod = params[k].lastIndexOf(".");
									if (lastIndexOfPeriod > -1) {
										// find file name !!
										int equalsIdx = params[k].indexOf("=");
										if (equalsIdx > -1) {
											sourceFile = params[k].substring(equalsIdx + 1, params[k].length());
										} else {
											sourceFile = params[k];
										}

									}
								}
							}

							sourceFile = sourceFile.replace(' ', '_'); // 2013.12.05 : File_Name Replace Space to Underscore @JunSeok.Jung
							sourceFile = FileUtil.lastSeparator(preFixDir) + sourceFile;

							// saveHttpThreadContent(sourceFile, srcURL[n]);
							targetFile = filterDir + StringUtil.getTimeBasedUniqueID() + ".txt";

							String strUrl = srcURL[n].replaceAll(" ", "%20");
							HttpDownLoad httpDownLoad = new HttpDownLoad();
							httpDownLoad.FileDownLoad(strUrl, sourceFile);

							if (filteringFile(sourceFile, targetFile, chk_ext_condition)) {
								if (!charset.toLowerCase().equals("utf-8")) {
									sbData.append(readTextFile(targetFile, maxLen)).append(" ");
								} else {
									sbData.append(readTextFile(targetFile, maxLen, "UTF-16")).append(" ");
								}
							}
						}
					}
				}
			}

			if (condition.equals("source-delete") && srcURL != null) {
				int size = srcURL.length;
				if (size > 0) {
					boolean isDeleted = true;
					for (int n = 0; n < size; n++) {
						int spos = srcURL[n].lastIndexOf("/");
						String sourceFileName = srcURL[n];
						if (spos > -1) {
							try {
								sourceFileName = FileUtil.lastSeparator(preFixDir)
										+ sourceFileName.substring(spos + 1, sourceFileName.length());

								sourceFileName = StringUtil.spaceReplace(sourceFileName);
								File file = new File(sourceFileName);

								if (file.exists()) {
									file.delete();
								} else {
									isDeleted = false;
									Log2.debug("" + sourceFileName, 3);
								}
								/*
								 * else {
								 * for (int count = 0; count < 10; count++) {
								 * if (file.exists()) {
								 * file.delete();
								 * isDeleted = true;
								 * } else {
								 * Thread.sleep(1000);
								 * }
								 * Log2.debug("try delete for " + (count + 1), 3);
								 * }
								 * }
								 */
							} catch (Exception e) {
								Log2.error("delete error: " + sourceFileName + IOUtil.StackTraceToString(e));
							}
						}
					}

					// 2013.12.05 : Failed Delete File Retry @JunSeok.Jung
					if (!isDeleted) {
						File deleteDir = new File(preFixDir);

						String[] children = deleteDir.list();

						if (children != null) {
							for (int idx = 0; idx < children.length; idx++) {
								String delFileName = preFixDir + children[i];

								File delFile = new File(delFileName);

								if (delFile.exists()) {
									delFile.delete();
								}
							}
						}
					}
				}
			}
		}
		return sbData.toString();
	}
}