/*
 * @(#)TargetFileInfo.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3.config.source;


/**
 *
 * TargetFileInfo
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class TargetFileInfo {
    private int capacity = 10;
    private int size = 0;

    private String[] path;
    private int[] depth;
    private boolean[] incDir;


    public TargetFileInfo() {
        path = new String[capacity];
        depth = new int[capacity];
        incDir = new boolean[capacity];
    }

    public void addTarget(String path, int depth, boolean incDir) {
        if (size >= capacity) {
            capacity *= 2;
            String[] ts = new String[capacity];
            int[] td = new int[capacity];
            boolean[] ti = new boolean[capacity];

            System.arraycopy(this.path, 0, ts, 0, size);
            System.arraycopy(this.depth, 0, td, 0, size);
            System.arraycopy(this.incDir, 0, ti, 0, size);

            this.path = ts;
            this.depth = td;
            this.incDir = ti;

        }

        this.path[size] = path;
        this.depth[size] = depth;
        this.incDir[size] = incDir;

        size++;
    }

    public int size() {
        return size;
    }

    public String getPath(int num) {
        return path[num];
    }

    public int getDepth(int num) {
        return depth[num];
    }

    public boolean getIncDir(int num) {
        return incDir[num];
    }

}
