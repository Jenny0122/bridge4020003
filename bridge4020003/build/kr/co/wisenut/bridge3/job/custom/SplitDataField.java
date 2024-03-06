/*
 * @(#)WSParseXML.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.job.custom;

import kr.co.wisenut.common.Exception.CustomException;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.io.IOUtil;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * WSParseXML
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class SplitDataField implements ICustom {
    private String xslPath;
    private File xslFile;


    public String customData(String str) throws CustomException{
        String regx = "\\.";
        String[] array = split(str, regx, false);
        
        if(array.length > 10) {
            System.out.println("Len("+array.length+") =" + str);
        }
        StringBuffer buffer = new StringBuffer();
        for(int i=1; i <= 5; i++) {
            if(array.length >= i) {
                buffer.append("<sub_0"+i+">").append(array[i-1]).append("\n");
            } else {
                buffer.append("<sub_0"+i+">").append("\n");
            }
        }
//        System.out.println("Len=" + array.length);
//        System.out.println(buffer.toString()+"\n");
        return str;
    }

    public static String[] split(String text, String rex, boolean keepDelim) {
        if(text == null) {
            return null;
        }
        boolean isFirst = true;
        int lastIdx = 0;
        LinkedList splitted = new LinkedList();
        Pattern pattern = Pattern.compile(rex);
        Matcher m = pattern.matcher(text);
        while(m.find()) {
            String str = text.substring(lastIdx, m.start()).trim();

            if(splitted.contains(str)) {
                continue;
            }
            if(str.length() > 0) {
                splitted.add(str);
            }

            if(keepDelim) {
                splitted.add(m.group());
            }
            lastIdx = m.end();
            isFirst = false;
        }
        splitted.add(text.substring(lastIdx).trim());

        return (String[]) splitted.toArray(new String[splitted.size()]);
    }
}
