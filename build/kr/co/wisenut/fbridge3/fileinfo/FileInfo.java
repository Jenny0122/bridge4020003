/*
 * @(#)FileInfo.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3.fileinfo;

import java.util.StringTokenizer;

/**
 *
 * FileInfo
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class FileInfo {

    public final String docid;
    public final String path;
    public long lastModified;

    public FileInfo(String path, String docid, long lastModified) {
        this.path = path;
        this.docid = docid;
        this.lastModified = lastModified;
    }

   public  FileInfo(String info) {
        StringTokenizer token = new StringTokenizer(info, "\t");
        path = token.nextToken();
        docid = token.nextToken();
        lastModified = Long.parseLong(token.nextToken());
    }
}