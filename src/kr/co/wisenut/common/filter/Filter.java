/*
 * @(#)Filter.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.filter;


import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.HttpDownLoad;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.http.HttpDownLoader;
import kr.co.wisenut.common.util.http.HttpThreadDownLoader;
import kr.co.wisenut.common.util.io.IOUtil;

import java.io.*;

/**
 *
 * Filter
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public abstract class Filter implements IFilter {
    protected boolean useDocFilter = false;
    protected String FILTER_APP = null;
    protected String m_wcse_home = "";
    protected String FILTER_ROOT = "";
    protected boolean isFilterDelete = true;
    private StringBuffer buffer = new StringBuffer();
    protected String filteredTextDir = "";
    protected String filterOption = "";
    
    /**
     * FilterData Constructor
     * @param filterDel filtered file delete y/n
     */
    public Filter(Boolean filterDel) {
        this.FILTER_APP = System.getProperty("filter.app");
        this.isFilterDelete = filterDel.booleanValue();
        if(System.getProperty("wcse_home") != null){
            m_wcse_home = System.getProperty("wcse_home");
        }else if(System.getProperty("sf1_home") != null){
            m_wcse_home = System.getProperty("sf1_home");
        }

        FILTER_ROOT = FileUtil.lastSeparator(m_wcse_home) + "filter" + FileUtil.getFileSeperator();
    }
    
	public void setFilterOption(String filterOption) {
		this.filterOption = filterOption;
	}

    /**
     *
     * @param sourceFile attach binary file
     * @param targetFile filtered text file
     * @param condition filtered text file delete y/n
     * @return  filtering success / fail
     */
    public boolean filteringFile(String sourceFile, String targetFile, String condition) {
        return filteringFile(sourceFile, targetFile, condition, "sn3f", "", "utf-8");
    }

    /**
     * Filtering processing method
     * @param sourceFile attach binary file
     * @param targetFile filtered text file
     * @param condition filtered text file delete y/n
     * @param filter filter execute binary name
     * @return  filtering success / fail
     */
    public boolean filteringFile(String sourceFile, String targetFile, String condition,
                                 String filter, String jungumKey, String charset) {
        Log2.debug("[Filter] [Filtering File : sourceFile(" + sourceFile + ") Filter.java]", 4);

        if(!condition.equals("none-chk-ext") ) {
            if(condition.equals("chk-ext-indexof")) {
                if(!FileUtil.isFilteredIndexOf(sourceFile)) {
                    return false;
                }
            } else {
                if(!FileUtil.isFiltered(sourceFile)) {
                    return false;
                }
            }
        }

        File srcFile = new File(sourceFile);
        if( !srcFile.exists() ) {
            Log2.debug("[Filter] [Attach file not found (" + sourceFile + ") : Please check attach file location]", 2);
            return false;
        }

        String[] cmd ;
        if(isGul(sourceFile)) { //use docfilter
            if(filter.toLowerCase().equals("docfilter")) {
                cmd = new String[] {FILTER_ROOT+"docfilter", "-f", sourceFile, targetFile};
            }else{
                cmd = new String[] {FILTER_ROOT+"brconvjug2txt", jungumKey, "-f", "-a", sourceFile, targetFile};
            }
        } else {  //use synap filter
            if(filter.equalsIgnoreCase("all")) filter = "sn3f";
            String[] filterargs = filter.split(" ");
            if(filterargs != null && filterargs.length > 1) {
            	filterargs[0] = FILTER_ROOT+filterargs[0];
	        	cmd = new String[filterargs.length + 2];
	        	for(int i=0;i<filterargs.length;i++){
	        		cmd[i] = filterargs[i];
	        	}
	        	cmd[filterargs.length] = sourceFile;
	        	cmd[filterargs.length+1] = targetFile;
            } else {
	        	if(!charset.toLowerCase().equals("utf-8")){
	            	cmd = new String[] {FILTER_ROOT+filter, sourceFile, targetFile};
	        	}else {
	        		cmd = new String[] {FILTER_ROOT+filter, "-U", sourceFile, targetFile};
	        	}
            }
        }
        
        if( !"".equals(filterOption)) {
        	String [] fopts = filterOption.split(" ");
        	String [] refine = new String[cmd.length + fopts.length];
        	
        	int i, x = 0;
        	refine[x++] = cmd[0];
        	for( i = 0; i < fopts.length; i++ ) {
        		refine[x++] = fopts[i];
        	}
        	for( i = 1; i < cmd.length; i++ ) {
        		refine[x++] = cmd[i];
        	}
        	
        	cmd = refine;
        }

        /*
        if(isGul(sourceFile)) { //use docfilter
        	cmd = new String[] {FILTER_ROOT+"brconvjug2txt", "br_ssfn20090929", "-f", "-a", sourceFile, targetFile};
        } else if(isBwp(sourceFile)) { // 보라워드 사용
        	cmd = new String[] {FILTER_ROOT+"bwp2txt", sourceFile, targetFile};
        } else {  //use synap filter
            if(condition.equalsIgnoreCase("unicode")){
                cmd = new String[] {FILTER_ROOT+"sn3f", "-U" ,sourceFile, targetFile};
            }else{
                if(filter.equalsIgnoreCase("all")) {
                    cmd = new String[] {FILTER_ROOT+"sn3f", sourceFile, targetFile};
                } else {
                    cmd = new String[] {FILTER_ROOT+filter,  sourceFile, targetFile};
                }
            }
        }
        */

        FilterThread thread = new FilterThread(cmd);
        thread.start();

        try {
            thread.join(MAX_WATE_TIME);
        } catch (InterruptedException e) {
        	Log2.debug("[Filter] [filter thread timeout.. source file : " + sourceFile + "]");
        }
        // if (thread.isAlive() && thread.procs) {
        thread.ForceEnd();
        //  }
        return true;
    }


    /**
     * Reading filtered text file
     * @param sourceFile filtered text file name
     * @param readSize file read size
     * @param encoding file encoding
     * @return filtered text data
     */
    public String readTextFile(String sourceFile, int maxReadSize, String encoding) {
        Log2.debug("[Filter] ReadTextFile -> SourceFile(" + sourceFile + ") Filter.java]", 4);
        File srcFile = new File(sourceFile);
        if( !srcFile.exists() ) {
            Log2.debug("[Filter] [Source file not found. (" + sourceFile + ")]", 2);
            return "";
        }
        long fileLen = srcFile.length();
        String str ="";
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader in = null;
        try{
        	
        	if (encoding!=null && encoding.equals("UTF-16") && fileLen < 3) { //Unicode 3byte over
        		return "";
        	}
        	
            fis = new FileInputStream(srcFile);
            if(encoding == null) {
            	isr = new InputStreamReader(fis);          
            }else{
            	isr = new InputStreamReader(fis, encoding);  	
            }
            in = new BufferedReader(isr);
            long checkSize = 0L;
            if(fileLen > maxReadSize){
                String strLine = "";
                while ( (strLine = in.readLine()) != null &&  (maxReadSize > checkSize ) ) {
                    checkSize = checkSize + strLine.getBytes().length;
                    buffer.append(strLine).append(StringUtil.newLine);
                }
            }else{
                String strLine = "";
                while ((strLine = in.readLine()) != null) {
                    buffer.append(strLine).append(StringUtil.newLine);
                }
            }
            str = buffer.toString() ;
            buffer.setLength(0);

        } catch (IOException e) {
            Log2.error("[Filter] ["
                    + "\n" + IOUtil.StackTraceToString(e) + "\n]");
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
                if(isr != null) {
                    isr.close();
                }
                if(fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                Log2.error("[Filter] [Finally "
                        + "\n" + IOUtil.StackTraceToString(e) + "\n]");
            }
            
            if (isFilterDelete) {
                boolean deleteStatus = srcFile.delete();
                if(deleteStatus) {
                	Log2.debug("[Filter] [filtered text file delete successed : " + srcFile.getPath()  + "]");
                }else{
                	Log2.debug("[Filter] [filtered text file delete failed : " + srcFile.getPath()  + "]");
                }
            }
        }
        


        return str;
    }

    /**
     * Reading filtered text file
     * @param sourceFile filtered text file name
     * @param readSize file read size
     * @return filtered text data
     */
    public String readTextFile(String sourceFile, int readSize) {
    	return readTextFile(sourceFile,  readSize, null);
    }


    /**
     * check gul file
     * @param fileName file name
     * @return y/n
     */
    protected boolean isGul(String fileName) {
        boolean isRet = false;
        int idx = fileName.lastIndexOf(".");
        if(idx > 0 && idx < fileName.length()) {
            if(fileName.substring(idx+1, fileName.length()).toLowerCase().equals("gul")) {
                isRet = true;
            }
        }
        return isRet;
    }

    public String getExtractHttpToFileName(String dir, String url){
        String str = "";
        HttpDownLoad HttpDown = null;
        str = url;

        int spos = str.lastIndexOf("/");
        if(spos > -1){
            HttpDown = new HttpDownLoad();
            str = dir + str.substring(spos+1, str.length());
            str = StringUtil.spaceReplace(str);

            if(HttpDown.FileDownLoad(url, str) < 0 ){
                Log2.debug("[Filter] [File  Download Fail]", 2);
                str = "";
                return str;
            }

        }

        return str;
    }

    public void saveHttpContent(String distFileName, String url){
        HttpDownLoader httpDownLoader = new HttpDownLoader();

        if(httpDownLoader.saveHttpContent(url, distFileName) < 0 ){
            Log2.debug("[Filter] [File  Download Fail]", 2);
        }
    }

    public void saveHttpThreadContent(String dir, String url){
        HttpThreadDownLoader threadDownLoader = null;

        threadDownLoader = new HttpThreadDownLoader(url, dir);
        threadDownLoader.start();
        try {
            threadDownLoader.join(1000 * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public  boolean isHttp(String fileName){
        boolean isRet = false;
        if(fileName.toLowerCase().startsWith("http://") || fileName.toLowerCase().startsWith("https://")){
            isRet = true;
        }
        return isRet;
    }

    protected boolean isBwp(String fileName) {
        boolean isRet = false;
        int idx = fileName.lastIndexOf(".");
        if(idx > 0 && idx < fileName.length()) {
            if(fileName.substring(idx+1, fileName.length()).toLowerCase().equals("bwp")) {
                isRet = true;
            }
        }
        return isRet;
    }

    protected void debug(String[][] sourceFileInfo, String preFixDir, String retrive, String condition, String filter) {
        debug(sourceFileInfo, preFixDir, retrive, condition, filter, "");
    }
    /**
     * print debug message method
     * @param sourceFileInfo
     * @param preFixDir
     * @param retrive
     * @param condition
     */
    protected void debug(String[][] sourceFileInfo, String preFixDir, String retrive, String condition, String filter, String jungumKey) {
        StringBuffer info = new StringBuffer("[FilterData Debug Info> ");

        for(int i=0;i<sourceFileInfo.length; i++) {
            for(int j=0; j<sourceFileInfo[i].length; j++) {
                info.append( "sourceFileInfo["+i+"]["+j+"]=").append(sourceFileInfo[i][j]).append(", ");
            }
        }
        info.append( " : preFixDir=").append(preFixDir)
                .append( ", retrive=").append(retrive).append(", condition=")
                .append( condition).append(", filter=").append(filter);
        if(!"".equals(jungumKey)) {
            info.append(", jungumKey=").append(jungumKey);
        }
        info.append("]");
        Log2.debug("[Filter] " + info, 3);
    }
    
    public static void main(String[] args) {
    	String arg = "D:\\sf-1v5.3_mall\\bin\\sn3f.exe ";
    	String[] filterargs = arg.split(" ");
    	String[] cmd = null;
    	 if(filterargs != null && filterargs.length > 1) {
	//    	String[] cmd = new String[] {arg, "C:\\KM_DOWN\\PT Q-Library 착수 검토 결과v1.0.pptx","C:\\oletest.txt"};
	    	cmd = new String[filterargs.length + 2];
	    	for(int i=0;i<filterargs.length;i++){
	    		cmd[i] = filterargs[i];
	    	}
	    	cmd[filterargs.length] = "C:\\KM_DOWN\\PT Q-Library 착수 검토 결과v1.0.pptx";
	    	cmd[filterargs.length+1] ="C:\\oletest.txt";
    	 }
    	try {
			Process process = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
}


