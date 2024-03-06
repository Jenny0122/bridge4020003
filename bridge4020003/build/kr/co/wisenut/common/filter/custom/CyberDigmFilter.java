package kr.co.wisenut.common.filter.custom;

import java.io.File;

import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.filter.Filter;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.StringUtil;
import cyberdigm.blues.IDataTransfer;
import cyberdigm.storage.StorageObject;
import cyberdigm.storage.server.RIStorage;
import cyberdigm.storage.server.StorageFactory;

public class CyberDigmFilter extends Filter {
	private int MAX_FILTERED_LIMIT_SIZE = 1024 * 1024 * 3;

	/**
	 * FilterData Constructor
	 * @param filterDel
	 *            filtered file delete y/n
	 */
	public CyberDigmFilter(Boolean filterDel) {
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

		Log2.debug("[CyberDigmFilter] [Custum Filter -> kr.co.wisenut.common.filter.custom.CyberDigmFilter]", 4);
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
				Log2.debug("[CyberDigmFilter ] [None Filtered File Ext FileName or Ext Name, "
						+ "sourceFileInfo[i][1] is FileName or FileExt" + sourceFileInfo[i][1] + "]", 3);
			} else {
				srcFile = FileUtil.lastSeparator(preFixDir) + sourceFileInfo[i][0] + "."
						+ FileUtil.getFileExt(sourceFileInfo[i][1]);
				Log2.debug("[CyberDigmFilter ] [CyberDigmFilter: " + srcFile + "]", 3);
				if (getFile(Long.parseLong(sourceFileInfo[i][0], 0), srcFile)) {
					targetFile = filterDir + StringUtil.getTimeBasedUniqueID() + ".txt";
					if (filteringFile(srcFile, targetFile, chk_ext_condition)) {
						if (!charset.toLowerCase().equals("utf-8")) {
							sbData.append(readTextFile(targetFile, maxLen)).append(" ");
						} else {
							sbData.append(readTextFile(targetFile, maxLen, "UTF-16")).append(" ");
						}
					}
				}
			}

			if (condition.equals("source-delete")) { // delete attach binary file
				File file = new File(srcFile);
				if (file.exists()) {
					file.delete();
					Log2.debug("[CyberDigmFilter] [delete temporary file success.]", 4);
				}
			}
		}

		return sbData.toString();
	}

	private boolean getFile(long lFileID, String downPath) {
		boolean ret = true;
		System.setProperty("cyberdigm.storage.server.StorageProxy", "cyberdigm.storage.server.StorageProxy");
		try {

			download(lFileID, downPath);
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
		}
		return ret;
	}

	/*
	 * public void setStorageServer(String paramString, int paramInt) {
	 * StorageFactory.set(paramString, paramInt);
	 * }
	 * public void close() {
	 * StorageFactory.closeAll();
	 * }
	 */

	public boolean download(long paramLong, String paramString) throws Exception {
		StorageObject localStorageObject = new StorageObject(paramLong);

		RIStorage localRIStorage = StorageFactory.getServer(localStorageObject.getStorageServerID());

		if (localRIStorage != null) {
			File localFile = new File(paramString);

			IDataTransfer localIDataTransfer = localRIStorage.get(localFile, localStorageObject);

			localIDataTransfer.isComplete(true);

			return true;
		}

		Log2.debug("[CyberDigm Filter] [Invalid File ID : " + paramLong + "]", 2);

		return false;
	}
}