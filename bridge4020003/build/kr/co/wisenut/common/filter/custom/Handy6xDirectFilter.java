/*
 * @(#)Handy6xFilter.java 3.8.1 2009/03/11
 */
package kr.co.wisenut.common.filter.custom;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.common.filter.Filter;
import kr.co.wisenut.common.filter.custom.handy.HandyAttachInfo;
import kr.co.wisenut.common.filter.custom.handy.HandyAttachInfoFactory;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.StringUtil;

import com.hs.gw.service.fmanager.util.OpenFileUtil;

/**
 * 
 * 
 * @Company : WISENut
 * @Date : 2011. 1. 25.
 */
public class Handy6xDirectFilter extends Filter {
	private int MAX_FILTERED_LIMIT_SIZE = 1024 * 1024 * 3;
	private String originPathSeparator;
	final static String OPTFILE = "opt";
	final static String SRCDEL = "source-delete";

	final static int XML = 2;
	final static int BODY = 1;
	final static int ATTACH = 5;

	// private FtpDownloader ftpDownloader;

	/**
	 * FilterData Constructor
	 * @param filterDel
	 *            filtered file delete y/n
	 * @throws kr.co.wisenut.common.Exception.ConfigException
	 */
	public Handy6xDirectFilter(Boolean filterDel) throws ConfigException {
		super(filterDel);
		// this.ftpDownloader = new FtpDownloader();

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

		Log2.debug("[Handy6xDirectFilter] [Custum Filter -> kr.co.wisenut.common.filter.custom.Handy6xDirectFilter]", 4);
		debug(sourceFileInfo, preFixDir, retrieve, condition, filterType);

		if (!split.equals("\\") && !split.equals("/")) {
			Log2.error("Should be set handy document path separator in split attribute. value is '/' or '\\'");
			System.exit(1);
		}

		originPathSeparator = split;

		preFixDir = FileUtil.removeLastSeparator(preFixDir);

		StringBuffer scdBuff = new StringBuffer();
		// String filterDir = FileUtil.lastSeparator(m_wcse_home) + "Filter" + FileUtil.getFileSeperator();
		String filterDir = filteredTextDir;
		FileUtil.makeDir(filterDir);

		String approvalID = sourceFileInfo[0][0];
		if (!isRightStatus(approvalID)) {
			return "";
		}

		String orignXmlPath = preFixDir + getAttachPath(approvalID) + originPathSeparator + getFileID(approvalID, XML);
		// String orignXmlPath = "d:\\wisenut\\HANDY_ORIGN" + FileUtil.fileseperator + xmlId;
		// String downloadXmlPath = filterDir + getFileID(approvalID, XML);

		String downloadDocPath = null;
		File fileDir = null;
		try {
			// ftpDownloader.connect();
			// ftpDownloader.download(orignXmlPath, downloadXmlPath);

			File xmlFile = new File(orignXmlPath);

			// fileDir = new File(filterDir, approvalID);
			HandyAttachInfo handy = new HandyAttachInfoFactory().getInstance(xmlFile.getPath());
			// FileUtil.makeDir(fileDir.getPath());

			int docType = getDocType(approvalID);
			String filename = "";

			if (docType == BODY) {
				filename = getFileID(approvalID, BODY) + "." + getFormExt(handy.getWordType());
			} else if (docType == ATTACH) {
				filename = getAttachFile(approvalID, handy.getAttachArray());
			}

			if (FileUtil.isFiltered(filename) || filename.lastIndexOf(".hwn") != -1
					|| filename.lastIndexOf(".hwx") != -1 || filename.lastIndexOf(".hun") != -1) {

				// copy�븷 �썝蹂� �뙆�씪
				// File gwFile = new File(fileDir, filename);

				String orignDocPath = preFixDir + getAttachPath(approvalID) + originPathSeparator + approvalID;
				// downloadDocPath = gwFile.getPath();

				// ftpDownloader.download(orignDocPath, downloadDocPath);

				String filterSourceFile = remove(orignDocPath);
				if (charset.toLowerCase().equals("utf-8")) {
					MAX_FILTERED_LIMIT_SIZE = MAX_FILTERED_LIMIT_SIZE * 3;
				}
				String targetFile = filterDir + StringUtil.getTimeBasedUniqueID() + ".txt";
				if (filteringFile(filterSourceFile, targetFile, "none-chk-ext")) {
					if (!charset.toLowerCase().equals("utf-8")) {
						scdBuff.append(readTextFile(targetFile, MAX_FILTERED_LIMIT_SIZE)).append(" ");
					} else {
						scdBuff.append(readTextFile(targetFile, MAX_FILTERED_LIMIT_SIZE, "UTF-16")).append(" ");
					}
				}

			} else {
				Log2.error("Not Filtered !!:approvalID=" + approvalID + ":filename=" + filename);
			}
		} catch (Exception e) {
			Log2.error("[Handy6xDirectFilter error, approvalid:" + approvalID + ",msg:" + e.toString());
		} finally {
			if ("delete-tmp".equals(condition)) {
				if (fileDir != null) {
					File[] deleteFileList = fileDir.listFiles();
					for (int k = 0; k < deleteFileList.length; k++) {
						deleteFileList[k].delete();
					}
					fileDir.delete();
				}
				// (new File(downloadXmlPath)).delete();
			}
			/*
			 * try {
			 * ftpDownloader.disconnect();
			 * } catch (IOException e) {
			 * e.printStackTrace();
			 * }
			 */
		}

		return scdBuff.toString();
	}

	private boolean isRightStatus(String approvalID) {
		return approvalID != null && approvalID.length() == 20;
	}

	private String getAttachFile(String approvalID, String[][] attachs) {
		for (int i = 0; i < attachs.length; i++) {
			if (approvalID.equals(attachs[i][0])) {
				// attach file name
				return attachs[i][0] + "." + FileUtil.getFileExt(attachs[i][1]);
			}
		}
		return null;
	}

	private int getDocType(String approvalID) {
		if (approvalID.substring(18, 20).equals("01")) {
			return BODY;
		} else if (approvalID.substring(18, 19).equals("5")) {
			return ATTACH;
		} else {
			return -1;
		}
	}

	public void unZip(String path) {
		long len1;
		do {
			len1 = new File(path).length();
			try {
				OpenFileUtil.unzip(path);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} while (len1 != new File(path).length());
	}

	public String remove(String fullPath) throws IOException {
		String dest;
		// int rc = OpenFileUtil.unzip(fullPath);
		unZip(fullPath);
		if (fullPath.indexOf(".hwn") != -1) {
			// case hwp 97
			dest = fullPath + ".hwp";
			OpenFileUtil.unMagicHwpFile(fullPath, dest);
		} else if (fullPath.indexOf(".hwx") != -1) {
			// case hwp 2002
			dest = fullPath + ".hwp";
			OpenFileUtil.removeHWPHeader(fullPath, dest);
		} else if (fullPath.indexOf(".gux") != -1) {
			// case gul file
			dest = fullPath + ".gul";
			OpenFileUtil.removeHWPHeader(fullPath, dest);
			// } else if (fullPath.indexOf(".") != -1) {
			// // case hwp 2002
			// dest = fullPath + "hwp";
			// OpenFileUtil.removeHWPHeader(fullPath, dest);
		} else {
			dest = fullPath;
		}
		return dest;
	}

	public String getAttachPath(String apprID) {
		Calendar cal = Calendar.getInstance();
		int year = Integer.parseInt(apprID.substring(5, 7)) + 2000;
		int day = Integer.parseInt(apprID.substring(7, 10));
		int serial = Integer.parseInt(apprID.substring(15, 18));

		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_YEAR, day);

		StringBuffer sb = new StringBuffer();
		sb.append(originPathSeparator).append(cal.get(Calendar.YEAR));
		sb.append(originPathSeparator).append((cal.get(Calendar.MONTH) + 1));
		sb.append(originPathSeparator).append(cal.get(Calendar.DAY_OF_MONTH));
		sb.append(originPathSeparator).append(serial);
		Log2.debug("Handy6xDirectFilter::getAttachPath(" + apprID + ")" + sb.toString(), 3);
		return sb.toString();
	}

	private String getFileID(String apprID, int type) {
		String bodyFileID = "";
		if (type == XML) {
			bodyFileID = apprID.substring(0, 18) + "02";
		} else if (type == BODY) {
			bodyFileID = apprID.substring(0, 18) + "01";
		}
		return bodyFileID;
	}

	private String getFormExt(String type) {
		String ext;
		if ("hwp97".equals(type)) {
			ext = "hwn";
		} else if ("hwp2002".equals(type)) {
			ext = "hwx";
		} else if ("hun".equals(type)) {
			ext = "gux";
		} else {
			ext = "";
		}
		return ext;
	}

	/*
	 * public static void main(String[] args) {
	 * // setLogger(String logBase, String logType, boolean debug, int verbosity, boolean verbose, String srcID)
	 * Log2.setLogger("c:/Temp", "stdout", true, 3, false, "test");
	 * Handy6xFilter hf = new Handy6xFilter(new Boolean(true));
	 * // String path = hf.getAttachPath("00000080240000317800");
	 * String path = hf.getAttachPath("00000103200281688450"); // 00000103200281688450 00000103230283284700
	 * System.out.println("path"+path);
	 * String[][] attachFileInfo;
	 * //attachFileInfo = new String[][]{{"00000080240000317800", "java.lang.String"}};
	 * attachFileInfo = new String[][]{{"00000103230283284700", "java.lang.String"}};
	 * System.out.println(hf.getAttachPath(attachFileInfo[0][0]));
	 * String ret = hf.getFilterData(attachFileInfo, "c:\\Temp", "local", "delete-tmp", "sn3f", " ");
	 * // System.out.println(ret);
	 * // attachFileInfo = new String[][]{{"00000082140200500000", "java.lang.String"}};
	 * // ret = hf.getFilterData(attachFileInfo, "c:\\Temp", "local", "none", "sn3f", " ");
	 * // System.out.println(ret);
	 * }
	 */

	public static void main(String[] args) {
		try {
			Handy6xDirectFilter hf = new Handy6xDirectFilter(new Boolean(true));
			Log2.setLogger("c:/Temp", "stdout", true, 3, false, "test");
			File file = new File("D:\\wisenut\\HANDY_ORIGN");

			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					String dir = hf.getAttachPath(files[i].getName());
					String path = "D:\\wisenut\\HANDY_ORIGN\\sancbox\\" + dir + File.separator + files[i].getName();
					FileUtil.makeDirForPath(path);
					FileUtil.copy(files[i].getPath(), path);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
