/*
 * @(#)Source.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.config.source;

import kr.co.wisenut.bridge3.config.source.node.*;
import kr.co.wisenut.common.filter.FilterSource;

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
	private RefColl refColl = new RefColl();	
    private DateFormat dateFormat = new DateFormat();
    private String dbms = "";
    private String targetDBSrc = "";
    private String logDBSrc = "";
    private FilterSource[] filterSource;
    private SubQuery[] subQuery;
    private SubMemory subMemory;
    private XmlQuery[] xmlQuery;
    private Query query;
    private TableSchema tblSchema;
    private String sourceaias = "";
    private String scdCharSet = "UTF-8";

    // SCD file send 2010.06.14 by ikcho
    private RemoteInfo[] remoteinfo;
    private String remoteDir;

    private String extension = "scd";
    
    
    // 개인 정보 추출 정규식 태그
    private DetectPI detectPI;
    
    
    /**
     * Getting SCDDir Node's Info
     * 
     * @return SCDDir
     */
    public SCDdir getScdDir() {
		return scdDir;
	}
    /**
     * Setting SCDDir Node's Info
     * 
     * @param SCDDir
     */
	public void setScdDir(SCDdir scdDir) {
		this.scdDir = scdDir;
	}

	/**
	 * Getting RefColl Node's Info
	 * 
	 * @return RefColl
	 */
	public RefColl getRefColl() {
		return refColl;
	}
	/**
	 * Setting RefColl Node's Info
	 * 
	 * @param refColl RefColl
	 */
	public void setRefColl(RefColl refColl) {
		this.refColl = refColl;
	}

	public CustomServerInfo getCustomServerInfo() {
        return customServerInfo;
    }

    public void setCustomServerInfo(CustomServerInfo customServerInfo) {
        this.customServerInfo = customServerInfo;
    }

    private CustomServerInfo customServerInfo;

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

    public String getScdCharSet() {
        return scdCharSet;
    }

    public void setScdCharSet(String scdCharSet) {
        this.scdCharSet = scdCharSet;
    }

    /*
    * Setting DB Bridge  Source
    */
    public String getDbms() {
        return dbms;
    }

    public void setDbms(String dbms) {
        this.dbms = dbms;
    }

    public String getTargetDSN() {

        return targetDBSrc;
    }

    public void setTargetDSN(String targetDBSrc) {

        this.targetDBSrc = targetDBSrc;
    }

    public String getLogDSN() {
        return logDBSrc;
    }

    public void setLogDSN(String logDBSrc) {
        this.logDBSrc = logDBSrc;
    }

    public FilterSource[] getFilterSource() {
        return filterSource;
    }

    public void setFilterSource(FilterSource[] filterSource) {
        this.filterSource = filterSource;
    }

    public SubQuery[] getSubQuery() {
        return subQuery;
    }

    public void setSubQuery(SubQuery[] subQuery) {
        this.subQuery = subQuery;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public TableSchema getTblSchema() {
        return tblSchema;
    }

    public void setTblSchema(TableSchema tblSchema) {
        this.tblSchema = tblSchema;
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

	public SubMemory getSubMemory() {
		return subMemory;
	}

	public void setSubMemory(SubMemory subMemory) {
		this.subMemory = subMemory;
	}
    
    public XmlQuery[] getXmlQuery() {
    	return xmlQuery;
    }
    
    public void setXmlQuery(XmlQuery[] xmlQuery) {
    	this.xmlQuery = xmlQuery;
    }
    
	public DetectPI getDetectPI() {
		return detectPI;
	}
	public void setDetectPI(DetectPI detectPI) {
		this.detectPI = detectPI;
	}
}
