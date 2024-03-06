/*
 * @(#)PropsReader.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.util;

import java.io.*;
import java.util.Properties;

/**
 *
 * PropsReader
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class PropsReader {
    private Properties props = new Properties();
    private String filename = new String();

    public PropsReader(String filename) throws IOException {
        this.loadProperties(filename);
        this.filename = filename;
    }

    private void loadProperties(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            FileUtil.makeDir(new File(filename).getParent());
            FileWriter fwriter = new FileWriter(file);
            fwriter.close();
        }
        props = FileUtil.loadProperties(file);
    }

    public void addProperty(String akey, String value) throws IOException {
        props.put(akey, value);   // otherwise, write new key / value to fbridge
        FileOutputStream fout = new FileOutputStream(this.filename);

        props.store(fout, this.filename);
        fout.close();
    }

    public String getProperty(String akey) {
        String foobar = props.getProperty(akey);
        if (foobar == null) {
            return null;
        }
        return foobar;
    }

    public static void main(String[] args) throws IOException {
        PropsReader pr = new PropsReader("d:/Temp/test.prop");
        for (int i = 0; i < 22; i++) {
            pr.addProperty("INSERT", Integer.toString(i));
            pr.addProperty("UPDATE", Integer.toString(i));
            pr.addProperty("DELETE", Integer.toString(i));
            System.out.println(pr.getProperty("INSERT"));
            System.out.println(pr.getProperty("UPDATE"));
            System.out.println(pr.getProperty("DELETE"));
        }

    }
}
