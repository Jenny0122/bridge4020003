package kr.co.wisenut.common.filter.custom;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.filter.Filter;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.time.StopWatch;
import rms.com.orign.api.RmsOrignClient;

/**
 * 援�媛�湲곕줉�썝 而ㅼ뒪�� �븘�꽣
 * @author JoonSeok
 * 
 */
public class KoreaArchives extends Filter {
	private StringBuffer sbData = new StringBuffer(8192);
	private int MAX_FILTERED_LIMIT_SIZE = 1024 * 1024 * 3;

	public KoreaArchives(Boolean filterDel) {
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

		/*
		 * TODO: 援�媛�湲곕줉�썝 �븘�꽣 API瑜� �궗�슜�븯湲� �쐞�빐�꽌�뒗 properties �뙆�씪�씠 �븘�슂�븯�떎.
		 * 1. bridge �뤃�뜑 �븘�옒�뿉 rms-stg-orign.properties �뙆�씪 �깮�꽦
		 * 2. properties �뙆�씪�뿉 Remote IP �� Remote PORT瑜� �젙�쓽�븳�떎.
		 * for example)
		 * RMS_ORIGN_IP = xxx.xx.xxx.xxx
		 * RMS_ORIGN_PORT = xxxx
		 */
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

				if (size == 2) {
					copyRecordFile(srcFile[0], srcFile[1]);
				} else {
					break;
				}

				// Receive Remote File Path
				File dir = new File(filterDir + "/Temp/");
				File[] fileList = dir.listFiles();

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

					for (int n = 0; n < fileList.length; n++) {
						File srcFileTmp;
						targetFile = filterDir + StringUtil.getTimeBasedUniqueID() + ".txt";
						if (fileList[n] == null || fileList[n].equals("")) {
							continue;
						}

						srcFileTmp = fileList[n];

						Log2.debug("[FilterData] [filtering...]");
						StopWatch stopWatch = new StopWatch();
						stopWatch.start();
						if (filteringFile(srcFileTmp.getPath(), targetFile, chk_ext_condition, filterType, jungumKey,
								charset)) {

							if (!charset.toLowerCase().equals("utf-8")) {
								sbData.append(readTextFile(targetFile, maxLen)).append(" ");
							} else {
								sbData.append(readTextFile(targetFile, maxLen, "UTF-16")).append(" ");
							}
						} else {
							Log2.debug("[FilterData] [filtering failed. " + srcFileTmp.getPath() + "]");
						}
						stopWatch.stop();
						Log2.debug("[FilterData] [filtering completed. elapsed time : " + stopWatch.getTime()
								+ " msec]");
					}

					// Temp File Delete
					for (int idx = 0; idx < fileList.length; idx++) {
						FileUtil.delete(fileList[idx]);
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

	private static void copyRecordFile(String pRecordCenterId, String pRecordId) {
		RmsOrignClient test = new RmsOrignClient();

		if (test.aliveOrign()) {
			HashMap map = new HashMap();

			ArrayList lst = new ArrayList();

			test.copyRecordOrignFileList(pRecordCenterId, pRecordId, "D:\\Test\\");
		}
	}

	public static void main(String[] args) {
		RmsOrignClient test = new RmsOrignClient();

		if (test.aliveOrign()) {
			HashMap map = new HashMap();

			ArrayList lst = new ArrayList();

			lst = test.copyRecordOrignFileList("0000001", "20130000000241", "D:\\Test\\");
		}
	}

}
