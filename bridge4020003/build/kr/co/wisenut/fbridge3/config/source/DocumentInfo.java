/*
 * @(#)DocumentInfo.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3.config.source;

/**
 *
 * DocumentInfo
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class DocumentInfo {
    public static final int MAX_FIELD_COUNT = 128;


    private int size = 0;
    
    private String[] maps;

    public DocumentInfo() {
        maps = new String[MAX_FIELD_COUNT];
    }

    public void setMapping(int colnum, String map) {
        if (colnum >= size) {
            size = colnum + 1;
        }
        maps[colnum] = map;
    }
    
    public String getMapping(int colnum) {
        return maps[colnum];
    }

    public int size() {
        return size;
    }
}
