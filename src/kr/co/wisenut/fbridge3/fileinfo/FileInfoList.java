/*
 * @(#)FileInfoList.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3.fileinfo;

import java.util.Vector;

/**
 *
 * FileInfoList
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class FileInfoList extends Vector {

    public FileInfoList() {
        super();
    }

    public FileInfoList(int size) {
        super(size);
    }

    public FileInfo getInfo(int num) {
        return (FileInfo) get(num);
    }

    public void addInfo(FileInfo info) {
        add(info);
    }

    public FileInfo removeInfo(int num) {
        return (FileInfo) remove(num);
    }
}
