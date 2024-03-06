/*
 * @(#)CustomFileNameFilter.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.job;

import java.io.FilenameFilter;
import java.io.File;

/**
 *
 * CustomFileNameFilter
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class CustomFileNameFilter implements FilenameFilter {
    private String[] m_str;

    public CustomFileNameFilter(String[] str) {
        m_str = str;
    }

    public CustomFileNameFilter(String str){
        m_str = new String[1];
        m_str[0] = str;
    }

    public boolean accept(File dir, String name) {
        if( new File(dir, name).isDirectory()) {
            return false;
        }
        if(m_str != null) {
            name = name.toLowerCase();
            for(int i=0 ; i < m_str.length; i++) {
                if(name.startsWith(m_str[i])) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
}
