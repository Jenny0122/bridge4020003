package kr.co.wisenut.common.filter.custom;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.filter.Filter;
import kr.co.wisenut.common.filter.custom.softforum.DocEncrypt;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.time.StopWatch;

/**
 * �냼�봽�듃�룷�읆 而ㅼ뒪�� �븘�꽣
 * @author JunSeok.Jung
 * 
 */
public class SoftForumFilterData extends Filter {
	private StringBuffer sbData = new StringBuffer(8192);
	private int MAX_FILTERED_LIMIT_SIZE = 1024 * 1024 * 3;
	private StringBuffer buffer = new StringBuffer();

	/**
	 * SoftForumFilterData Constructor
	 * @param filterDel
	 *            filtered file delete y/n
	 */
	public SoftForumFilterData(Boolean filterDel) {
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

						StringBuffer tmpSbData = new StringBuffer();
						if (filteringFile(srcFileTmp.getPath(), targetFile, chk_ext_condition, filterType, jungumKey,
								charset)) {
							// SoftForum �븫�샇�솕
							if (!charset.toLowerCase().equals("utf-8")) {
								tmpSbData.append(readTextFile(targetFile, maxLen)).append(" ");
								sbData = tmpSbData;
							} else {
								tmpSbData.append(readTextFile(targetFile, maxLen, "UTF-16")).append(" ");
								sbData = tmpSbData;
							}

							/*
							 * DocEncrypt doc = new DocEncrypt(tmpSbData.toString());
							 * StringBuffer strEncode = new StringBuffer();
							 * doc.init();
							 * strEncode.append(doc.WNEncode());
							 */

							Log2.out("[SoftForumFilterData] [getFilterData] Start");
							DocEncrypt doc = new DocEncrypt(tmpSbData.toString());

							StringBuffer strEncode = new StringBuffer();

							if (doc.init()) {
								Log2.out("[SoftForumFilterData] [getFilterData] Document Encoding..");
								strEncode.append(doc.WNEncode());
							}
							doc.end();
							Log2.out("[SoftForumFilterData] [getFilterData] End");

							try {
								BufferedWriter bw = new BufferedWriter(new FileWriter(targetFile));
								bw.write(strEncode.toString());
								bw.close();
							} catch (IOException e) {
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
						// blob �뜲�씠��媛� 鍮꾩뼱�벝 寃쎌슦 null 泥섎━. by jwlee 20091127
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

	public String getFilterData(String[][] sourceFileInfo, String preFixDir, String retrieve, String condition,
			String filter, String split) {
		return null;
	}

}
