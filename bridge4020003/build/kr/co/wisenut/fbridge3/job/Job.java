/*
 * @(#)Job.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3.job;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import java.util.LinkedHashMap;
import kr.co.wisenut.bridge3.job.IJob;
import kr.co.wisenut.common.Exception.BridgeException;
import kr.co.wisenut.common.Exception.FilterException;
import kr.co.wisenut.common.filter.FilterFactory;
import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.filter.IFilter;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.scdreceiver.SCDTransmit;
import kr.co.wisenut.common.util.FileManager;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.SCDManager;
import kr.co.wisenut.common.util.JSONManager;
import kr.co.wisenut.common.util.SimpleTagParser;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.io.IOUtil;
import kr.co.wisenut.fbridge3.config.Config;
import kr.co.wisenut.fbridge3.config.catalogInfo.InfoSet;
import kr.co.wisenut.fbridge3.config.catalogInfo.Mapping;
import kr.co.wisenut.fbridge3.config.source.AcceptRuleInfo;
import kr.co.wisenut.fbridge3.config.source.Source;
import kr.co.wisenut.fbridge3.config.source.TargetFileInfo;
import kr.co.wisenut.fbridge3.fileinfo.FileInfo;
import kr.co.wisenut.fbridge3.fileinfo.FileInfoList;
import kr.co.wisenut.fbridge3.fileinfo.FileInfoSet;

/**
 *
 * Job
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class Job {
    private Source m_source;
    private AcceptRuleInfo dirRuleInfo;
    private AcceptRuleInfo fileRuleInfo;
    private FileManager fileManager;
    private HistoryManager histMan;
    public static final int INIT = 0;
    public static final int STATIC = 1;
    public static final int DYNAMIC = 2;
    public static final int TEST = 3;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private Mapping mapping;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DecimalFormat df = new DecimalFormat("#,##0");

    private int  mode = -1;
    private String sf1_home;
    private int totalCount = 0;
    private int maxDepth;
    private int curDepth;
    private boolean incDir;
    boolean useIndexDir;
    boolean isFilterDel;
    boolean isVerb;
    boolean isTest = false;
    boolean isTarget = false;
    private Config mConfig = null;
	private String filteredTextDir;

    public Job (String sf1_home, Config config, int mmode, boolean filterdel, boolean isVerb){
        this.mode = mmode;
        this.mConfig = config;
        this.m_source = config.getSource();
        this.dirRuleInfo = m_source.getDirAcceptRuleInfo();
        this.fileRuleInfo = m_source.getFileAcceptRuleInfo();
        this.mapping = config.getCollection() ;
        this.sf1_home =  sf1_home;
        this.isFilterDel = filterdel;
        this.isVerb = isVerb;
        this.useIndexDir  = config.getArgs().isUserIndexDir();

        File indexDir ;
        if(config.getArgs().isUserIndexDir() ){
            indexDir = new File(m_source.getScdDir().getPath(), "index");
        } else if(mode == STATIC ) {
            indexDir = new File(m_source.getScdDir().getPath(), "static");
        } else if(mode == DYNAMIC ) {
            indexDir = new File(m_source.getScdDir().getPath(), "dynamic");
        } else {
            indexDir = new File(m_source.getScdDir().getPath());
        }

        this.fileManager = new JSONManager(indexDir, m_source.getScdCharSet(), m_source.getScdDir().getIdx());

        if (m_source.getExtension().equalsIgnoreCase("scd")) {
            this.fileManager = new SCDManager(indexDir, m_source.getScdCharSet(), m_source.getScdDir().getIdx());
        }

		this.filteredTextDir = FileUtil.lastSeparator(config.getArgs().getSf1_home()) + "Filter" + FileUtil.getFileSeperator()
				+ config.getSrcid() + FileUtil.getFileSeperator();         
    }

    public boolean run()  throws IOException{
        Log2.out("[info] [Job ] [File Bridge Process: Run]");

        File histDir = new File(m_source.getHistoryfile());
        if(mConfig.getArgs().getParam() != null && !mConfig.getArgs().getParam().equals("")) {
            String paramDir = FileUtil.lastSeparator(histDir.getAbsolutePath()) + mConfig.getArgs().getParam();
            histDir = new File(paramDir);
        }
        histMan = new HistoryManager(histDir);

        if(mode == INIT) {
            boolean isSucess = false;
            Log2.out("[info] [Job Init] [Delete File]");
            if( fileManager.delete() ) {
                Log2.debug("[Job Init] [Successful]");
                histMan.clearHistory();
                isSucess = true;
            } else {
                Log2.debug("[Job Init] [Failure]");
            }
            return isSucess;
        }

        File filterChk = null;
        if ( !System.getProperty("os.name").startsWith("Windows") ) {
            filterChk = new File(sf1_home + FileUtil.getFileSeperator() + "filter" + FileUtil.getFileSeperator() + "snf_exe");
        } else {
            filterChk = new File(sf1_home + FileUtil.getFileSeperator() + "filter" + FileUtil.getFileSeperator() + "snf_exe.exe");
        }

        if (!filterChk.exists()) {
            Log2.error("[Filter] [Unable to excute the snf_exe file: Please check the snf_exe file in "
                    + sf1_home + FileUtil.getFileSeperator() + "filter directory.]");
            return false;
        }

        if (mode == TEST) {
            histMan.clearHistory();
            isTest = true;
        }

        if (mode == STATIC) {
            histMan.clearHistory();
        }

        try {
            histMan.loadHistory();
        } catch (IOException e) {
            Log2.error( "[Job ] [Unable to load the history file. " +
                    "Please check the configuration file or history directory. : "
                    +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        }

        try {
            TargetFileInfo targetInfo = m_source.getTargetFileInfo();
            printProgress();

            for (int i = 0; i < targetInfo.size(); i++) {
                String path = targetInfo.getPath(i);
                maxDepth = targetInfo.getDepth(i);
                incDir = targetInfo.getIncDir(i);
                curDepth = 0;
                isTarget = true;
                File target = new File(path);

                if (!target.exists()) {
                    Log2.error( "[Job ] [TargetFile is not exists. " + path+" Please check the dicrectory " +
                            "or the permissions of the file setting in config file.]");
                } else if (target.isDirectory()) {
                    crawlingDirectory(target);
                } else {
                    File dir = new File(target.getParent());
                    FileInfoSet infoSet = histMan.getFileInfos(dir);
                    crawlingFile(infoSet, target);
                }
            }

            crawlingRemovedDirs();

            return true;
        } catch (IOException e) {
            Log2.error( "[Job ] [File I/O error occurred. Please check the dicrectory " +
                    "or the permissions of the file setting in config file. : "
                    +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        } finally {
            histMan.saveHistory();
            fileManager.close(this.isTest);
            
            if (m_source.getRemoteinfo().length != 0) {
				int remoteSize = m_source.getRemoteinfo().length;
				String rcvMode = mConfig.getArgs().getScdRcvMode();
				String scdDirPath = m_source.getScdDir().getPath();
				String remoteDirPath = m_source.getRemoteDir();
				
				if (useIndexDir) {
					scdDirPath = scdDirPath + "/index";
					remoteDirPath = remoteDirPath + "index";
				} else if (mode == IJob.STATIC) {
					scdDirPath = scdDirPath + "/static";
					remoteDirPath = remoteDirPath + "static";
				} else if (mode == IJob.DYNAMIC) {
					scdDirPath = scdDirPath + "/dynamic";
					remoteDirPath = remoteDirPath + "dynamic";
				}
				
				File backupFile = new File(scdDirPath);
				for(int idx=0; idx<remoteSize; idx++) {
					String remoteIp = m_source.getRemoteinfo()[idx].getIp();
					int remotePort = m_source.getRemoteinfo()[idx].getPort();
					boolean isScdDelete = false;
					
					SCDTransmit trans = new SCDTransmit(remoteIp, remotePort, rcvMode);
					Log2.debug("[Job ] [SCD File TransMit" + remoteIp + "]", 3);
					
					if(idx+1 == remoteSize) {
						isScdDelete = m_source.getRemoteinfo()[idx].isDeleteSCD();
						backupFile = new File(m_source.getScdDir().getPath(), "backup");
					}
					
					trans.sendSCD(scdDirPath, remoteDirPath , backupFile.getPath(), isScdDelete); // setting path..
				}
			}
        }

        return false;
    }

    /**
     *
     * @param dir crawling directory
     */
    private void crawlingDirectory(File dir)  {
        if(!isTarget) {
            //System.out.println(">>  " + dir.getAbsolutePath());
            if (!dir.exists() || !dirRuleInfo.isAllow(dir.getAbsolutePath())) {
                Log2.debug( "[Job ] [Denied by the AcceptRule. " + dir.getAbsolutePath()
                        +" Please check the <DirAcceptRule> setting in config file.]", 2);
                return;
            }
        }
        curDepth++;
        isTarget = false;
        try {
            if (maxDepth == 0 || curDepth <= maxDepth) {
                int dirMode = histMan.getDirectoryMode(dir);
                if (mode == STATIC || mode == TEST) {
                    if (incDir) {
                        LinkedHashMap<String, String> data = getUpdateDocument(histMan.getDocid(dir), dir);
                        fileManager.insert(data);
                        totalCount++;
                        printProgress();
                    }
                    FileInfoSet infoSet = histMan.getFileInfos(dir);
                    crawlingUpdateFiles(infoSet, dir);
                    histMan.setDirectoryInfo(dir);
                    histMan.saveDirectoryInfo(dir);
                } else if (mode == DYNAMIC) {
                    FileInfoSet infoSet = histMan.getFileInfos(dir);
                    crawlingUpdateFiles(infoSet, dir);
                    crawlingRemovedFiles(infoSet);
                    histMan.setDirectoryInfo(dir);
                    histMan.saveDirectoryInfo(dir);
                }

                File[] files = dir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        crawlingDirectory( files[i] );
                    }
                }
            }

        } catch (IOException e) {
            Log2.error( "[Job ] [Exception occurred. Please check the dicrectory " +
                    "or the permissions of the file in config file. : "
                    +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        } finally {
            curDepth--;
        }
    }

    private void crawlingUpdateFiles(FileInfoSet dirInfo, File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (int i=0; i < files.length ; i++) {
            crawlingFile(dirInfo, files[i]);
        }
    }

    private boolean crawlingFile(FileInfoSet infoSet, File file) throws IOException,UnsupportedEncodingException {
        if (!file.exists() || !fileRuleInfo.isAllow(file.getName())) {
            return false;
        }

        int fileMode = histMan.getFileMode(infoSet, file);
        if (fileMode == HistoryManager.MODE_NONE) {
            return false;
        }

        LinkedHashMap<String, String> data = getUpdateDocument(histMan.getDocid(file), file);

        if (mode == TEST && totalCount >= 10) {
            fileManager.close(this.isTest);
            System.exit(0);
        }

        if (fileMode == HistoryManager.MODE_NEW) {
            fileManager.insert(data);
            Log2.debug("[Job ] [Inserted. " + file.getAbsolutePath() + "]", 3);
        } else {
            fileManager.update(data);
            Log2.debug("[Job ] [Modified. " + file.getAbsolutePath() + "]", 3);
        }

        totalCount++;
        printProgress();
        return true;
    }

    private void crawlingRemovedDirs() throws IOException {
        FileInfoList deleted = histMan.getRemovedDirs();

        for (int i=0; i<deleted.size(); i++) {
            FileInfo dirInfo = deleted.getInfo(i);
            if (incDir) {
                fileManager.delete(getDeleteDocument(dirInfo.docid));
                Log2.debug( "[Job ] [Removed. " + dirInfo.path+"]", 3);
                totalCount++;
                printProgress();
            }

            FileInfoList fileList = histMan.getFileInfoList(dirInfo.docid);
            for (int j=0; j<fileList.size(); j++) {
                FileInfo info = fileList.getInfo(j);
                File file = new File(info.path);
                if (fileRuleInfo.isAllow(file.getName())) {
                    fileManager.delete(getDeleteDocument(dirInfo.docid));
                    Log2.debug( "[Job ] [Removed. " + info.path+"]", 3);
                    totalCount++;

                    printProgress();
                }
            }
        }

        histMan.removeDirectoryInfos(deleted);
    }

    private void crawlingRemovedFiles(FileInfoSet infoSet)  throws IOException{
        FileInfoList fileList = histMan.getRemovedFiles(infoSet);
        for (int i=0; i<fileList.size(); i++) {
            FileInfo info = fileList.getInfo(i);
            File file = new File(info.path);
            if (fileRuleInfo.isAllow(file.getName())) {
                fileManager.delete(getDeleteDocument(info.docid));
                Log2.debug( "[Job ] [Removed. " + info.path+"]", 3);
                totalCount++;
                printProgress();
            }
        }
    }

    private LinkedHashMap<String, String> getUpdateDocument(String docid, File file) throws IOException {
        String date = "";
        String gubun = "";    //file or directory
        String dirPath = "";  // directory name
        String fileName = ""; // file name
        String fileSize = ""; // file size
        String fileExt = "";  // file extension
        String modifiedDate = ""; //file modified date
        String content = "";      // file content
        String absolutePath = ""; // file full path
        String[] tagValue;

        if (file.isDirectory()) {
            gubun = "DIRECTORY";
            dirPath = file.getPath();
        } else if (file.isFile()) {
            gubun = "FILE";
            fileName = file.getName();
            dirPath = file.getParentFile().getPath();
            absolutePath = file.getAbsolutePath();
            fileSize = df.format(file.length());
            fileExt =  FileUtil.getFileExt(fileName);
            content  = StringUtil.replace(getFilterData(file.getPath(), m_source.getCustomFilter()), "\n"," ");
        }
        date = dateFormat.format(new Date(file.lastModified()));
        modifiedDate = sdf.format(new Date(file.lastModified()));

        InfoSet[] catalog = mapping.getCatalog();
        int tagCnt = catalog.length;
        String convert ;
        tagValue = new String[tagCnt];

        for (int colIdx = 0; colIdx < tagCnt; colIdx++) {
            tagValue[0] = docid ;    // <DOCID>
            if (catalog[colIdx].getType().equals("content")) {
                tagValue[colIdx] = content;   //content
            }

            if (catalog[colIdx].getType().equals("filename")) {
                tagValue[colIdx] = fileName;   //url
            }

            if (catalog[colIdx].getType().equals("fileext")) {
                tagValue[colIdx] = fileExt; //base_url
            }

            if (catalog[colIdx].getType().equals("gubun")) {
                tagValue[colIdx] = gubun;  //type
            }

            if (catalog[colIdx].getType().equals("dirpath")) {
                tagValue[colIdx] = dirPath;  //section
            }

            if (catalog[colIdx].getType().equals("filesize")) {
                tagValue[colIdx] = fileSize; //source
            }

            if (catalog[colIdx].getType().equals("date")) {
                tagValue[colIdx] = date; //date
            }

            if (catalog[colIdx].getType().equals("modifieddate")) {
                tagValue[colIdx] = modifiedDate;
            }

            if (catalog[colIdx].getType().equals("fullpath")) {
                tagValue[colIdx] =absolutePath ;
            }

            if (catalog[colIdx].getType().equals("extend")) {
                tagValue[colIdx] = catalog[colIdx].getTypeValue();
            }

            if (catalog[colIdx].getType().equals("param")) {
                if (mConfig.getArgs().getParam() != null && !mConfig.getArgs().getParam().equals("")) {
                    tagValue[colIdx] = mConfig.getArgs().getParam();
                } else {
                    tagValue[colIdx] = "";
                }
            }

            if (catalog[colIdx].isSourceAlias() || catalog[colIdx].getType().equals("alias")) {
                tagValue[colIdx] = m_source.getSourceaias();
            }
        }

        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (int colIdx = 0; colIdx < tagCnt; colIdx++) {
            // tag value 媛�
            String val = tagValue[colIdx];
            if(!catalog[colIdx].getConvert().equals("")){
                convert = catalog[colIdx].getConvert();
                int idx = convert.indexOf(",");
                int len = convert.length();

                if (idx > 0 && len > idx+1) {
                    String srcEncode = convert.substring(0, idx);
                    String targetEncode = convert.substring(idx+1, len);
                    Log2.debug("[Job ] [String Convert : "+srcEncode + " -> " + targetEncode+"]", 3);
                    val = StringUtil.convert(val, srcEncode, targetEncode);
                } else {
                    Log2.error("[Job ] [Invalid convert attribute.]");
                }
            } else {
                if (catalog[colIdx].isHtml()) {
                    val = SimpleTagParser.getTagParse(val);
                }
            }

            // custom class �궗�슜 媛��뒫�븯寃� 異붽�
            if (!catalog[colIdx].getClassName().equals("0x00")) {
                try {
                    val = CFactory.getInstance(catalog[colIdx].getClassName()).customData(val);
                } catch (Exception e) {
                    Log2.error("[Job ] [ Custom class error : "+"\n"+IOUtil.StackTraceToString(e)+"\n]");
                }
            }

            result.put(catalog[colIdx].getTagName(), val);
        }

//        if(isVerb){
//            System.out.print(sbSCDBuf.toString());
//        }

        return result;
    }

    private LinkedHashMap<String, String> getDeleteDocument(String docid) {
        final String deleteDocId = new String(docid);
        return new LinkedHashMap<String, String>() {
            {
                put("DOCID", deleteDocId);
            }
        };
    }

    private String getFilterData(String filePath, String className) {
    	String filterData = "";
    	
    	try {
    		IFilter ifilter = filterQueue(className);
    		
    		String[][] filterList = new String[1][1];
    		filterList[0][0] = filePath;
    		
    		FilterSource filterSource = new FilterSource();
    		filterSource.setDir("");
    		filterSource.setRetrival("local");
    		filterSource.setCondition("chk-ext-indexof");
    		filterSource.setFilterType("sn3f");
    		filterSource.setSplit("");
    		
    		filterData = ifilter.getFilterData(filterList, filterSource, m_source.getScdCharSet());
    	} catch (Exception e) {

        }

        return filterData;
    }
    
    
    /**
	 * Filtering custom class process method
	 * @param className
	 *            custom classname
	 * @return IFilter Interface object
	 * @throws FilterException
	 *             error info
	 * @throws BridgeException
	 *             error info
	 */
	private IFilter filterQueue(String className) throws FilterException, BridgeException {
		HashMap filters = new HashMap();
		
		IFilter filterClass = (IFilter) filters.get(className);
		if (filterClass == null) {
			try {
				filterClass = new FilterFactory().getInstance(className, true, filteredTextDir);
				if (filterClass == null) {
					throw new FilterException(": Unable to load the java class. "
							+ "Please check the <Filter className>");
				} else {
                    filters.put(className, filterClass);
				}
			} catch (BridgeException e) {
				throw new BridgeException(IOUtil.StackTraceToString(e));
			}
		}

		return filterClass;
	}

    private void printProgress() {
        if (totalCount % 10 == 0) {
            if (totalCount % 100 == 0 || totalCount == 0) {
                System.out.print("[" + totalCount + "]");
            } else {
                System.out.print(".");
            }
        }
    }

}
