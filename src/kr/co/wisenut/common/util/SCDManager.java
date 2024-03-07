package kr.co.wisenut.common.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import kr.co.wisenut.common.logger.Log2;

import java.io.*;
import java.nio.charset.Charset;

/**
 *
 * SCDManager
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class SCDManager extends FileManager {
    /**
     * Construct new instance with variables
     * @param dir SCD file directory
     *                Max count of documents are written to a file
     * @param charSet scd encoding charSet
     */
    public SCDManager(File dir, String charSet, String sourceIdx) {
        super(dir, charSet, sourceIdx);
    }

    @Override
    protected void appendData(int type, LinkedHashMap<String, String> data) throws IOException {
        // If the SCDWriter is null, create new SCDWrit
        if (fileWriter[type] == null) {
            File sf = getTempFile(type);
            files[type] = sf;
            fileWriter[type] = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(sf), charset), BUFFER_SIZE);

            Log2.out("[FileManager] [SCD Temp File : " + sf.getPath()  + "]");
        }

        StringBuffer buffer = new StringBuffer();
        Iterator<Entry<String, String>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            buffer.append("<").append(entry.getKey()).append(">").append(entry.getValue()).append(StringUtil.newLine);
        }

        fileWriter[type].write(buffer.toString());
        fileCount[type]++;

        //if(scdCount[type] == 10) scdWriter[type].flush();

        if (MaxFileCount > 0) {
            if (fileCount[type] >= MaxFileCount) {
                flush(type);
            }
        } else {
            long size = files[type].length();
            if (files[type].length() >= MaxBufferSize) {
                flush(type);
            }
        }
    }

    @Override
    protected File getTempFile(int type) {
        File sf, tf;

        do {
            String scdName = DateUtil.getScdFileTime();
            sf = new File(dir, "B-" + sourceIdx + "-" + scdName + "-" + TYPE_STRING[type] + "-C.SCD");
            tf = new File(dir, "B-" + sourceIdx + "-" + scdName + "-" + TYPE_STRING[type] + "-F.SCD");
        } while (sf.exists() || tf.exists());
        // Temporary & real SCD file are exists.. loop

        return tf;
    }

    @Override
    protected void flush(int type) throws IOException {
        if (fileWriter[type] == null) {
            return;
        }

        fileWriter[type].flush();
        fileWriter[type].close();

        // Set SCDWriter to null
        fileWriter[type] = null;
        fileCount[type] = 0;

        // Rename temporary SCD file to real SCD file
        File tmpFile = files[type];
        String tmpName = tmpFile.getName();

        if (tmpFile.exists()) {
            // Temporary SCD file is blank
            if (tmpFile.length() == 0) {
                if (!tmpFile.delete()) {
                    System.out.println( "[SCDFileWriter] SCD file delete error. " + tmpName);
                }
            } else {
                String scdName = "";
                if(isTEST){
                    scdName = tmpName.substring(0, 25) + "-T.SCD";
                } else if(isMKE){
                    scdName = tmpName.substring(0, 25) + "-M.SCD";
                }else{
                    scdName = tmpName.substring(0, 25) + "-C.SCD";
                }
                File scdFile = new File(dir, scdName);
                if (!tmpFile.renameTo(scdFile)) {
                    System.out.println( "[SCDWriter] SCD file rename error. " + tmpName);
                }else{
                    System.out.println();
                    Log2.out("[SCDManager] [SCD complete File : " + scdFile.getPath()  + "]");
                }

            }
        }
    }

    @Override
    public boolean delete() {
        String path = dir.getAbsolutePath();
        boolean isRet = true;

        if( !FileUtil.deleteFile(path, ".SCD") ){
            Log2.error("[FileManager ] [SCD File Delete Fail, Directory: " + path + "]");
            isRet = false;
        }

        if( !FileUtil.deleteFile(path + filterDir, ".txt") ){
            Log2.error("[SCDManager ] [Filtered File Delete Fail, Directory: " + path + "]");
            isRet = false;
        }

        return isRet;
    }
}
