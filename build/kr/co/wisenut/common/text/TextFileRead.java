/*
 * @(#)TextFileRead.java   3.8.1 2009/03/11
 *
 */

package kr.co.wisenut.common.text;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.io.IOUtil;

import java.io.*;

/**
 *
 * TextFileRead
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class TextFileRead {
    private boolean isSourceDel = false;
    public TextFileRead(boolean isSourceDel) {
        this.isSourceDel = isSourceDel;
    }
    public String readTextFile(String sourceFile, int readSize) {
        Log2.debug("[TextFileRead ] [ReadTextFile -> sourceFile("+sourceFile+")]", 4);
        File srcFile = new File(sourceFile);
        if( !srcFile.exists() ) {
            Log2.debug("[TextFileRead ] [Source Filtered Text File  not found ("+sourceFile+")]", 2);
            return "";
        }

        byte[] buf = new byte[0];
        int fileLen = (int)srcFile.length();
        try{
            if (fileLen > 0) {
                FileInputStream fis = new FileInputStream(srcFile);
                if(fileLen > readSize){
                    buf = new byte[readSize];
                    fis.read(buf, 0, readSize);
                    if( (new String(buf)).length() == 0 ) {  
                        buf = new byte[readSize-1];
                        fis.read(buf, 0, readSize-1);
                    }
                } else {
                    buf = new byte[fileLen];
                    fis.read(buf);
                }
                fis.close();
            }
   
            if (isSourceDel) {
                srcFile.delete();
            }
        } catch (IOException e) {
            Log2.error("[TextFileRead ] [ReadTextFile "
                         +IOUtil.StackTraceToString(e)+"\n]");
        }
        return new String(buf);
    }



    public static void main(String[] args) {
        TextFileRead reader = new TextFileRead(false);
        reader.readTextFile("d:/collection/B-00-200412301501-23225-I-C.SCD", 2048);

    }
}
