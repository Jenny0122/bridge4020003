/*
 * @(#)RichURL.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.bdb;
import com.sleepycat.je.*;

import java.io.UnsupportedEncodingException;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.io.IOUtil;

/**
 *
 * RichURL - URL DatabaseEntry  KEY and Value SET/GET
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class RichURL{
    public DatabaseEntry theKey;
    public DatabaseEntry theData;
    /**
     *RichURL Class constructor 
     */
    public RichURL() {
        theKey = new DatabaseEntry();
        theData = new DatabaseEntry();
    }

    /**
     *RichURL Class constructor
     * @param url  input url
     * @param source input source
     */
    public RichURL(String url, String source) {
        theKey = new DatabaseEntry();
        theData = new DatabaseEntry();
        setURL(url, source);
    }
    /**
     *setting URL information Function
     * @param url input URL
     * @param source input source
     */
    public void setURL(String url, String source) {
        try {

            theKey.setData(url.getBytes("utf-8"));
            theData.setData(source.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log2.error(IOUtil.StackTraceToString(e));
        }
    }

    /**
     * get url function
     * @return KEY DatabaseEntry
     */
    public String getURL() {
        String url = "";
        try {
            url =  new String(theKey.getData(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            url =  new String(theKey.getData());
        }
        return url ;
    }

    /**
     * get source function
     * @param charSet charset
     * @return theData DatabaseEntry
     */
    public String getSource(String charSet) {
        String url = "";
        try {
            url =   new String(theData.getData(),charSet);
        } catch (UnsupportedEncodingException e) {
            Log2.error(IOUtil.StackTraceToString(e));
        }
        return url ;
    }


    
}
