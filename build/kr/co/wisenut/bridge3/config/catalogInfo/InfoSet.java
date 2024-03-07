/*
 * @(#)InfoSet.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.config.catalogInfo;

import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.bridge3.config.source.MemorySelect;
import kr.co.wisenut.bridge3.config.source.SubQuery;
import kr.co.wisenut.bridge3.config.source.node.XmlQuery;

/**
 *
 * InfoSet
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class InfoSet {
    private int collumn = 0;
    private String fieldName = "";
    private boolean isHtmlParse = false;
    private boolean isSourceAlias = false;

    private SubQuery subQuery;
    private XmlQuery xmlQuery;
	private MemorySelect memorySelect;
    private FilterSource filter; 

    private String className = "0x00";
    private String type="";
    private int maxLen = 0;
    private String convert = "";
    private int append = 1;
    private int refnum = -1;

    private boolean isReplace = false;  //Add 2006/01/10
    private boolean isMd5 = false;  //Add 2008/06/18
    
    private boolean isScdFilter = false;

    public int getCollumn() {
        return collumn;
    }

    public void setCollumn(int collumn) {
        this.collumn = collumn;
    }

    public String getTagName() {
        return fieldName;
    }

    public void setTagName(String fieldName) {
        this.fieldName = fieldName;
    }

     public FilterSource getFilter() {
        return filter;
    }

    public void setFilter(FilterSource filter) {
        this.filter = filter;
    }

    public boolean isHtmlParse() {
        return isHtmlParse;
    }

    public void setHtmlParse(String htmlParse) {
        if(htmlParse.equalsIgnoreCase("y"))
            isHtmlParse = true;
    }

    public boolean isSourceAlias() {
        return isSourceAlias;
    }

    public void setSourceAlias(String sourceAlias) {
        if(sourceAlias.equalsIgnoreCase("y"))
            isSourceAlias = true;
    }

    public SubQuery getSubQuery() {
        return subQuery;
    }

    public void setSubQuery(SubQuery subQuery) {
        this.subQuery = subQuery;
    }
    
    public XmlQuery getXmlQuery() {
    	return xmlQuery;
    }
    
    public void setXmlQuery(XmlQuery xmlQuery) {
    	this.xmlQuery = xmlQuery;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(int maxLen) {
        this.maxLen = maxLen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConvert() {
        return convert;
    }

    public void setConvert(String convert) {
        this.convert = convert;
    }

     public int getAppend() {
        return append;
    }

    public void setAppend(int append) {
        this.append = append;
    }

    public boolean isReplace() {   //Add 2006/01/10
		return isReplace;
	}

	public void setReplace(String replace) {        //Add 2006/01/10
		if (replace.equalsIgnoreCase("y"))
			isReplace = true;
	}

    public boolean isMd5() {  //Add 2008/06/18
        return isMd5;
    }

    public void setMd5(String md5) {   //Add 2008/06/18
        if(md5.equalsIgnoreCase("y"))
            isMd5 = true;
    }

	public MemorySelect getMemorySelect() {
		return memorySelect;
	}

	public void setMemorySelect(MemorySelect memorySelect) {
		this.memorySelect = memorySelect;
	}
    
	public boolean isSCDFilter() {
		return isScdFilter;
	}

	public void setSCDFilter(String scdfilter) {
		if (scdfilter.equalsIgnoreCase("y")) {
			this.isScdFilter = true;
		}
	}
	
	public int getRefnum() {
		return refnum;
	}
	public void setRefnum(int refnum) {
		this.refnum = refnum;
	}
    
}
