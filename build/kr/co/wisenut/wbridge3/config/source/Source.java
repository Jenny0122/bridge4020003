/*
 * @(#)Source.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.config.source;

import kr.co.wisenut.wbridge3.config.source.node.SCDdir;

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
    private String sourceaias = "";
    private SCDdir scdDir = new SCDdir();
    private DateFormat dateFormat = new DateFormat();
    private String RefCBFS = "";
    private String srcPrefix = "";
    private String scdCharSet = "UTF-8";
    private String seedFile = "";
    private String dbFile = "" ;
    private String thumbnailDir = "" ;
    private int thumbnailWidth = 70 ;
    private int thumbnailHeight = 70 ;
    private boolean isInclude = false ;
    private boolean imgAltTag = false;
    
    private RemoteInfo remoteinfo;
    /** scd receiver 濡� �쟾�넚�븷 remote path �젙蹂� : <RemoteSCDdir> �끂�뱶�쓽 path �냽�꽦 */ 
    private String remoteDir;
    
    private String filterOption = "";

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

    public boolean isInclude() {
        return isInclude;
    }

    public void setInclude(String include) {
        if(include.toLowerCase().equals("y")) {
            isInclude = true;
        }
    }

    public String getThumbnailDir() {
        return thumbnailDir;
    }

    public void setThumbnailDir(String thumbnailDir) {
        this.thumbnailDir = thumbnailDir;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    
    /**
     * Bridge Seed File Return Fucntion
     * @return seedFile
     */
    public String getSeedfile() {
        return seedFile;
    }

    /**
     *  Set Seed File Fucntion
     * @param seedfile crawling seed file
     */
    public void setSeedfile(String seedfile) {
        this.seedFile = seedfile;
    }

    /**
     * Berkey DB File Path Return Fucntion
     * @return dbFile
     */
    public String getDbFile() {
        return dbFile;
    }

    /**
     * Set Berkey DB File Path Fucntion
     * @param dbFile file path
     */
    public void setDbFile(String dbFile) {
        this.dbFile = dbFile;
    }

    /**
     * Source Alias Value Return Function
     * @return sourceaias
     */
    public String getSourceaias() {
        return sourceaias;
    }

    /**
     * Set Source Alias Fucntion
     * @param sourceaias alias
     */
    public void setSourceaias(String sourceaias) {
        this.sourceaias = sourceaias;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

   /**
    *  DateFormat Class Return Function
    * @return dateFormat  class
    */
    public DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * Set  DateFormat Class  Function
     * @param order column order
     * @param dateFormat date value format
     */
    public void setDataFormat(int order, String dateFormat) {
        this.dateFormat.setOrder(order);
        this.dateFormat.setFormat(dateFormat);
    }

    /**
     *
     * @return RefCBFS
     */
    public String getRefCBFS() {
        return RefCBFS;
    }

    /**
     * SET RefCBFS Value Function
     * @param refCBFS cbfs value
     */
    public void setRefCBFS(String refCBFS) {
        RefCBFS = refCBFS;
    }

    /**
     * RefCBFS Prefix Value Return Function
     * @return  srcPrefix
     */
    public String getSrcPrefix() {
        return srcPrefix;
    }

    /**
     * Set  RefCBFS's Prefix Function
     * @param srcPrefix prefix value
     */
    public void setSrcPrefix(String srcPrefix) {
        this.srcPrefix = srcPrefix;
    }

    /**
     * ScdCharSet Value Return Function
     * @return scdCharSet
     */
    public String getScdCharSet() {
		return scdCharSet;
    }

    /**
     * Set SCD Char Set Function
     * @param scdCharSet charset
     */
    public void setScdCharSet(String scdCharSet) {
        this.scdCharSet = scdCharSet;
    }

    /**
     * 2009.05.28 by in-koo cho
     * HTML �뿉�꽌 <IMG SRC="" ALT=""> ALT Tag �뿉 �엳�뒗 �궡�슜�쓣 蹂몃Ц�뿉 湲곕줉 �븷吏� �뿬遺�瑜� 寃곗젙
     * @return
     */
    public boolean isImgAltTag() {
        return imgAltTag;
    }

    public void setImgAltTag(boolean imgAltTag) {
        this.imgAltTag = imgAltTag;
    }
    
    public RemoteInfo getRemoteinfo() {
        return remoteinfo;
    }

    public void setRemoteinfo(RemoteInfo remoteinfo) {
        this.remoteinfo = remoteinfo;
    }

	public String getRemoteDir() {
		return remoteDir;
	}

	public void setRemoteDir(String remoteDir) {
		this.remoteDir = remoteDir;
	}
	
	public void setFilterOption(String filterOption) {
		this.filterOption = filterOption;
	}
    
	public String getFilterOption() {
		return this.filterOption;
	}
    
}
