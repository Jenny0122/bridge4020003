/*
 * @(#)Source.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3.config.source;

import kr.co.wisenut.fbridge3.config.source.node.SCDdir;

/**
 *
 * Source
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class Source {
	private SCDdir scdDir = new SCDdir();
    private DateFormat dateFormat = new DateFormat();
    private DocumentInfo docInfo;
    private AcceptRuleInfo dirRule;
    private AcceptRuleInfo fileRule;
    private TargetFileInfo targetFile;
    private RemoteInfo[] remoteinfo;

    private String historyfile = "";
    private String sourceaias = "";

    private String RefCBFS = "";
    private String srcPrefix = "";
    private String scdCharSet = "UTF-8";
    private String remoteDir;
    
    private String customFilter;

    private String extension = "json";

    /**
     * Getting SCDDir Node's Info
     * @return
     */
    public SCDdir getScdDir() {
		return scdDir;
	}
    /**
     * Setting SCDDir Node's Info
     * @param scdDir
     */
	public void setScdDir(SCDdir scdDir) {
		this.scdDir = scdDir;
	}
    
    public String getHistoryfile() {
        return historyfile;
    }

    public void setHistoryfile(String historyfile) {
        this.historyfile = historyfile;
    }
     public String getSourceaias() {
        return sourceaias;
    }

    public void setSourceaias(String sourceaias) {
        this.sourceaias = sourceaias;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDataFormat(int order, String dateFormat) {
        this.dateFormat.setOrder(order);
        this.dateFormat.setFormat(dateFormat);
    }

    public String getRefCBFS() {
        return RefCBFS;
    }

    public void setRefCBFS(String refCBFS) {
        RefCBFS = refCBFS;
    }

    public String getSrcPrefix() {
        return srcPrefix;
    }

    public void setSrcPrefix(String srcPrefix) {
        this.srcPrefix = srcPrefix;
    }

    public String getScdCharSet() {
        return scdCharSet;
    }

    public void setScdCharSet(String scdCharSet) {
        this.scdCharSet = scdCharSet;
    }

     /*
     * Setting File Bridge  Source
    */
      public void setDocumentInfo(DocumentInfo doc) {
        this.docInfo = doc;
    }

    public DocumentInfo getDocumentInfo() {
        return this.docInfo;
    }

    public void setDirAcceptRuleInfo(AcceptRuleInfo rule) {
        this.dirRule = rule;
    }

    public AcceptRuleInfo getDirAcceptRuleInfo() {
        return this.dirRule;
    }

    public void setFileAcceptRuleInfo(AcceptRuleInfo rule) {
        this.fileRule = rule;
    }

    public AcceptRuleInfo getFileAcceptRuleInfo() {
        return this.fileRule;
    }

    public void setTargetFileInfo(TargetFileInfo target) {
        this.targetFile = target;
    }

    public TargetFileInfo getTargetFileInfo() {
        return this.targetFile;
    }

     public RemoteInfo[] getRemoteinfo() {
        return remoteinfo;
    }

    public void setRemoteinfo(RemoteInfo[] remoteinfo) {
        this.remoteinfo = remoteinfo;
    }

    public String getRemoteDir() {
        return remoteDir;
    }

    public void setRemoteDir(String remoteDir) {
        this.remoteDir = remoteDir;
    }
    
    public void setCustomFilter(String customFilter) {
    	this.customFilter = customFilter;
    }
    
    public String getCustomFilter() {
    	return customFilter;
    }
}
