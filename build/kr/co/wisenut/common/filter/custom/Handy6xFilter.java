/*
 * @(#)Handy6xFilter.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.filter.custom;

import com.hs.gw.service.fmanager.util.OpenFileUtil;

import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.filter.Filter;
import kr.co.wisenut.common.filter.custom.handy.HandyAttachInfo;
import kr.co.wisenut.common.filter.custom.handy.HandyAttachInfoFactory;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

/**
 *
 * Handy6xFilter
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class Handy6xFilter extends Filter {
    private static StringBuffer m_sb = new StringBuffer();
    private final static String FILE_SEPARATOR = "/";

    private static StringBuffer bodyBuf = new StringBuffer();
    private static StringBuffer attachBuf = new StringBuffer();

    final static String OPTFILE = "opt";
    final static String SRCDEL = "source-delete";

    final static int XML = 2;
    final static int BODY = 1;

    private int MAX_FILTERED_LIMIT_SIZE = 1024 * 1024 * 3;
    /**
     * FilterData Constructor
     * @param filterDel filtered file delete y/n
     */
    public Handy6xFilter(Boolean filterDel) {
        super(filterDel);
    }
    
    public void setFilteredTextDir(String filteredTextDir) {
    	super.filteredTextDir = filteredTextDir;
    	
    }

    
    
    public String getFilterData
    (String[][] sourceFileInfo, FilterSource filter ,String charset) {
    	String preFixDir = filter.getDir();
    	String retrieve = filter.getRetrival();
    	String condition = filter.getCondition();
    	String jungumKey = filter.getJungumKey();
    	String split = filter.getSplit();
    	String filterType = filter.getFilterType();
    	
    	Log2.debug("[Handy6xFilter] [Custum Filter -> kr.co.wisenut.common.filter.custom.Handy6xFilter]", 4);
        // print debug message
        debug(sourceFileInfo, preFixDir, retrieve, condition, filterType);

        String targetFile;
        //String filterDir = FileUtil.lastSeparator(m_wcse_home) + "Filter" + FileUtil.getFileSeperator();
        String filterDir = filteredTextDir;

        // Buffer initialize
        bodyBuf.setLength(0);
        attachBuf.setLength(0);


        HandyAttachInfo handy;
        String approvalID = sourceFileInfo[0][0];
        //String gwAttachPath = getAttachPath(approvalID);
        String bodyFullPath = preFixDir + getAttachPath(approvalID);
        File xmlFile = new File(bodyFullPath, getFileID(approvalID, XML));
        // check body content file location
        if(new File(bodyFullPath).exists() && xmlFile.exists()) {
            File gwFile = null;
            File fileDir = new File(filterDir, approvalID);
            handy = new HandyAttachInfoFactory().getInstance(xmlFile.getPath());
            FileUtil.makeDir(fileDir.getPath());
            try {
                Vector vtAttach = new Vector(10);
                // 1. body file copy to temporary directory
                String bodyFile = getFileID(approvalID, BODY);
                gwFile = new File(fileDir, bodyFile+ "." + getFormExt(handy.getWordType()));
                FileUtil.copy(new File(bodyFullPath, bodyFile).getPath(), gwFile.getPath());
                // uncompress and header info separator
                vtAttach.add(remove(gwFile.getPath()));

                // 2. filtering file copy to temporary directory
                String[][] attachArray = handy.getAttachArray();
                for(int i=0 ; attachArray != null && i < attachArray.length; i++) {
                    if(FileUtil.isFiltered(attachArray[i][1])
                            || attachArray[i][1].lastIndexOf(".hwn") != -1
                            || attachArray[i][1].lastIndexOf(".hwx") != -1
                            || attachArray[i][1].lastIndexOf(".hun") != -1) {
                        gwFile = new File(bodyFullPath, attachArray[i][0]);
                        if(gwFile.exists()) {
                            String tmpFile = new File(fileDir, attachArray[i][0] + "."+ FileUtil.getFileExt(attachArray[i][1])).getPath();
                            FileUtil.copy(gwFile.getPath(), tmpFile);
                            // uncompress and header info separator
                            vtAttach.add(remove(tmpFile));
                        } else {
                            File gwAttachFile;
                            if(!preFixDir.equals("")) {
                                gwAttachFile = new File(preFixDir, getAttachPath(attachArray[i][0]));
                                gwAttachFile = new File(gwAttachFile, attachArray[i][0]);
                            } else {
                                gwAttachFile = new File(getAttachPath(attachArray[i][0]), attachArray[i][0]);
                            }
                            if(gwAttachFile.exists()) {
                                String tmpFile = new File(fileDir, attachArray[i][0] + "."+ FileUtil.getFileExt(attachArray[i][1])).getPath();
                                FileUtil.copy(gwAttachFile.getPath(), tmpFile);
                                // uncompress and header info separator
                                vtAttach.add(remove(tmpFile));
                            }else{
                                Log2.error("File NotFound !! : " + gwAttachFile.getPath());
                            }
                        }
                    } else {
                        Log2.error("Not Filtered !! =" + attachArray[i][1]);
                    }
                }

                // filtering ...
                File deleteFile = null;
                if( charset.toLowerCase().equals("utf-8") ) {
                    MAX_FILTERED_LIMIT_SIZE = MAX_FILTERED_LIMIT_SIZE * 3 ;
                }
                int maxLen = MAX_FILTERED_LIMIT_SIZE / vtAttach.size();

                for(int k =0 ; k < vtAttach.size(); k++) {
                    targetFile = filterDir + StringUtil.getTimeBasedUniqueID() + ".txt";
                    if( filteringFile((String)vtAttach.get(k), targetFile, "none-chk-ext") ) {
                        if(k==0) {
                            deleteFile = new File((String)vtAttach.get(k));
                            if(!charset.toLowerCase().equals("utf-8")){
                                bodyBuf.append(readTextFile(targetFile, maxLen)).append(" ");
                            }else{
                                bodyBuf.append(readTextFile(targetFile, maxLen, "UTF-16")).append(" ");
                            }
                        } else {
                            if(!charset.toLowerCase().equals("utf-8")){
                                attachBuf.append(readTextFile(targetFile, maxLen)).append(" ");
                            }else{
                                attachBuf.append(readTextFile(targetFile, maxLen, "UTF-16")).append(" ");
                            }
                        }
                    }
                }
                if(deleteFile != null && "delete-tmp".equals(condition)) {
                    File[] deleteFileList = deleteFile.getParentFile().listFiles();
                    for(int k=0; k < deleteFileList.length; k++) {
//                        System.out.println(deleteFileList[k].getPath());
                        deleteFileList[k].delete();
                    }
//                    System.out.println(deleteFile.getParentFile().getPath());
                    deleteFile.getParentFile().delete();
                }
            } catch (IOException e) {
//                System.out.println("---------- error ----" + gwFile.getPath());
                if(gwFile != null) gwFile.getParentFile().deleteOnExit();
                Log2.error(e);
            }
        } else {
            Log2.error("Unable to read the attach directory. (" + bodyFullPath + ")");
        }

        StringBuffer retBuf = new StringBuffer();
        retBuf.append(bodyBuf).append(StringUtil.newLine).append("<attach>").append(attachBuf);
        return retBuf.toString();
    }

    public void unZip(String path) {
        long len1;
        do {
            len1 = new File(path).length();
            OpenFileUtil.unzip(path);
        } while(len1 != new File(path).length());
    }

    public String remove(String fullPath) throws IOException {
        String dest;
//        int rc = OpenFileUtil.unzip(fullPath);
        unZip(fullPath);
        if (fullPath.indexOf(".hwn") != -1) {
            //case hwp 97
            dest = fullPath + ".hwp" ;
            OpenFileUtil.unMagicHwpFile(fullPath, dest);
        } else if (fullPath.indexOf(".hwx") != -1) {
            // case hwp 2002
            dest = fullPath + ".hwp";
            OpenFileUtil.removeHWPHeader(fullPath, dest);
        } else if (fullPath.indexOf(".gux") != -1) {
            // case gul file
            dest = fullPath + ".gul";
            OpenFileUtil.removeHWPHeader(fullPath, dest);
//        } else if (fullPath.indexOf(".") != -1) {
//            // case hwp 2002
//            dest = fullPath + "hwp";
//            OpenFileUtil.removeHWPHeader(fullPath, dest);
        } else {
            dest = fullPath;
        }
        return dest;
    }

    private String getAttachPath(String apprID) {
        if(apprID.length() != 20) {
            return "";
        } else {
            Calendar cal = Calendar.getInstance();
            int year = Integer.parseInt(apprID.substring(5, 7)) + 2000;
            int day = Integer.parseInt(apprID.substring(7, 10));
            int serial = Integer.parseInt(apprID.substring(15, 18));

            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.DAY_OF_YEAR, day);

            m_sb.setLength(0);
            m_sb.append(FILE_SEPARATOR).append(cal.get(Calendar.YEAR));
            m_sb.append(FILE_SEPARATOR).append((cal.get(Calendar.MONTH)+1));
            m_sb.append(FILE_SEPARATOR).append(cal.get(Calendar.DAY_OF_MONTH));
            m_sb.append(FILE_SEPARATOR).append(serial);
            Log2.debug("Handy6xFilter::getAttachPath(" + apprID + ")" + m_sb.toString(), 3);
            return m_sb.toString();
        }
    }

    private String getFileID(String apprID, int type) {
        String bodyFileID = "";
        if(apprID != null && apprID.length() == 20) {
            if(type == XML) {
                bodyFileID = apprID.substring(0, 19) + "2";
            } else if(type == BODY) {
                bodyFileID = apprID.substring(0, 19) + "1";
            }
        }
        return bodyFileID;
    }

    private String getFormExt(String type) {
        String ext;
        if("hwp97".equals(type)) {
            ext = "hwn";
        } else if("hwp2002".equals(type)) {
            ext = "hwx";
        } else if("hun".equals(type)) {
            ext = "gux";
        } else {
            ext = "";
        }
        return ext;
    }

    public static void main(String[] args) {
    	try {
            String homeDir = System.getProperty("user.dir");
            System.out.println("homedir = " + homeDir);
            // setLogger(String logBase, String logType, boolean debug, int verbosity, boolean verbose, String srcID)
            Log2.setLogger(homeDir, "stdout", true, 3, false, "test");
            Handy6xFilter hf = new Handy6xFilter(new Boolean(true));
//            String path = hf.getAttachPath("00000080240000317800");
//            String path = hf.getAttachPath("00000103200281688450"); // 00000103200281688450 00000103230283284700
            String prefixDir = args[0];
            String appId = args[1];
            String path = hf.getAttachPath(appId); // 00000103200281688450 00000103230283284700
            System.out.println("path"+path);

            String[][] attachFileInfo;
            //attachFileInfo = new String[][]{{"00000080240000317800", "java.lang.String"}};
//            attachFileInfo = new String[][]{{"00000103230283284700", "java.lang.String"}};
            attachFileInfo = new String[][]{{appId, "java.lang.String"}};
            System.out.println(hf.getAttachPath(attachFileInfo[0][0]));
            //String ret = hf.getFilterData(attachFileInfo, prefixDir, "local", "delete-tmp2", "sn3f", " ");
//            System.out.println(ret);

//            attachFileInfo = new String[][]{{"00000082140200500000", "java.lang.String"}};
//            ret = hf.getFilterData(attachFileInfo, "c:\\Temp", "local", "none", "sn3f", " ");
//            System.out.println(ret);
		} catch (Exception e) {
			e.getStackTrace();
		}

    }
}
