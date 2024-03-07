/*
 * @(#)SeedInfo.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.seed;

import java.util.HashMap;
import java.util.Vector;

/**
 *
 * SeedItem
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All RightsReserved.
 * This software is the proprietary information of WISEnut,Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class SeedInfo {
    public final static String SOURCE = "[source]";
    public final static String HOMEURL = "[homeurl]";
    public final static String URL = "[url]";
    public final static String SKIPMENU = "[skipmenu]";
    public final static String SKIPURL = "[skipurl]";
    public final static String ALLOWURL = "[allowurl]";
    public final static String ONLYVIEWURL = "[onlyviewurl]";
    public final static String TAILMENU = "[tailmenu]";
    public final static String TAILSRC = "[tailsrc]";
    public final static String MAXDEPTH = "[maxdepth]";
    public final static String MAXURL = "[maxurl]";
    public final static String CUSTOMJS = "[customjs]";
    public final static String USERDEFINE = "[userdefine]";
    public final static String SECTION = "[section]";
    public final static String SECTION1 = "[section1]";
    public final static String SECTION2 = "[section2]";
    public final static String SECTION3= "[section3]";
    public final static String LOCALE = "[locale]";
    public final static String CHARSET = "[charset]";
    public final static String DENYID = "[denyId]";
    public final static String DENYCLASS = "[denyClass]";
    public final static String ISLIST = "[islist]";
    public final static String BASICAUTHID = "[basicauthid]";
    public final static String BASICAUTHPW = "[basicauthpw]";
    
    /** Crawl div id field name*/
	public static final String COLLECT_DIVID = "[collectDivId]";
	/** crawl tag class attribute field name */
	public static final String COLLECT_CLASS = "[collectClass]";
    
    private HashMap map;
    private Vector sourceVector;
/**
 * SeedInfo Class constructor
 * HashMap - SourceVector Object HashMap
 * SourceVector - Seed info Vector
 */
    public SeedInfo() {
        map = new HashMap();
        sourceVector = new Vector();
    }

/**
 * Source Return Function
 * @param index  Seed Source index number
 * @return  Seed Vector Index Value
 */
    public String getSource(int index) {
        //if (sourceVector.capacity() <= index) {
    if (sourceVector.size() <= index) {
            return null;
        } else {
            return (String) sourceVector.get(index);
        }
    }

/**
 * Source SeedItem Return Function
 * @param source Source infomation
 * @param item   SeedItem
 * @return  success or fail
 */
    public int get(String source, SeedItem item) {
        SeedItem findItem = (SeedItem) map.get(source);
        if (findItem == null) {
            return -1;
        } else {
            return item.assign(findItem);
        }
    }

/**
 * Source SeedItem Put Function
 * @param source  Source infomation
 * @param item     SeedItem
 * @return  success or fail
 */
    public boolean put(String source, SeedItem item) {
        if (map.containsKey(source) ) {
            return false;
        } else {
            map.put(source, item);
            sourceVector.addElement(source);
            return true;
        }
    }

    public int size() {
        return sourceVector.size();
    }
}