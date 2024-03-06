/*
 * @(#)Packets.java   3.8.1 2009/03/11
 *
 */

package kr.co.wisenut.common.socket;

import java.io.Serializable;

/**
 *
 * Packets
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class Packets  implements Serializable {
    private String msg = "";
    /**
     * Filtered data
     */
    private String attachData = "";
    /**
     *
     */
    private String[][] sourceFileInfo = null;
    /**
     *
     */
    private String preFixDir = "";
    /**
     *
     */
    private String retrive = "";
    /**
     *
     */
    private String condition = "";
    /**
     *
     */
    private String className = "";
    /**
     *
     */
    private String filter = "";


    private String charset = "";

    /**
     *
     */

    private String jungumKey = "";

    private boolean isFilterDel = false;
    
    private String srcId = "";
    /**
     *
     */
    private String Split = "";
    
    private String seperator = "";
    
    public String getSeperator() {
    	return seperator;
    }
    public void setSeperator(String seperator) {
    	this.seperator = seperator;
    }
    
    public String getSplit() {
        return Split;
    }

    public void setSplit(String split) {
        Split = split;
    }

    public String getAttachData() {
        return attachData;
    }

    public void setAttachData(String attachData) {
        this.attachData = attachData;
    }

    public boolean isFilterDel() {
        return isFilterDel;
    }

    public void setFilterDel(boolean filterDel) {
        isFilterDel = filterDel;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String[][] getSourceFileInfo() {
        return sourceFileInfo;
    }

    public void setSourceFileInfo(String[][] sourceFileInfo) {
        this.sourceFileInfo = sourceFileInfo;
    }

    public String getPreFixDir() {
        return preFixDir;
    }

    public void setPreFixDir(String preFixDir) {
        this.preFixDir = preFixDir;
    }

    public String getRetrive() {
        return retrive;
    }

    public void setRetrive(String retrive) {
        this.retrive = retrive;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getJungumKey() {
        return jungumKey;
    }

    public void setJungumKey(String jungumKey) {
        this.jungumKey = jungumKey;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

	public String getSrcId() {
		return srcId;
	}

	public void setSrcId(String srcId) {
		this.srcId = srcId;
	}
    
    
}
