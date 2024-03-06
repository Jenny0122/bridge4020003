/*
 * @(#)FileInfoSet.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3.fileinfo;

import java.util.Hashtable;

/**
 *
 * FileInfoSet
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class FileInfoSet extends Hashtable {
    
    public FileInfoSet() {
        super();
    }

    public FileInfoSet(int size) {
        super(size);
    }

    public FileInfo getInfo(String path) {
        return (FileInfo) get(path);
    }

    public void putInfo(FileInfo info) {
        put(info.path, info);
    }

    public FileInfo removeInfo(FileInfo info) {
        return (FileInfo) remove(info.path);
    }
}
