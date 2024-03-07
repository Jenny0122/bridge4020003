package kr.co.wisenut.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import kr.co.wisenut.common.logger.Log2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.*;

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
public class JSONManager extends FileManager {

    public static ObjectMapper mapper = new ObjectMapper();
    /**
     * Construct new instance with variables
     * @param dir the file directory
     * @param charSet encoding charSet
     */
    public JSONManager(File dir, String charSet, String sourceIdx) {
        super(dir, charSet, sourceIdx);
    }

    @Override
    protected void appendData(int type, LinkedHashMap<String, String> data) throws IOException {
        StringBuffer buffer = new StringBuffer();

        // If the FileWriter is null, create new json writer
        if (fileWriter[type] == null) {
            File sf = getTempFile(type);
            files[type] = sf;
            fileWriter[type] = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sf), charset), BUFFER_SIZE);
            fileWriter[type].write("[");
            fileWriter[type].write(StringUtil.newLine);

            Log2.out("[FileManager] [Json Temp File : " + sf.getPath()  + "]");
        }

        if (data.isEmpty()) {
            return;
        }

        if (fileCount[type] > 0) {
            buffer.append(",").append(StringUtil.newLine);
        }

        LinkedHashMap<String,String> jsonMap = new LinkedHashMap<>();
        Iterator<Entry<String, String>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            jsonMap.put(entry.getKey(),entry.getValue());
        }
        JsonNode jsonNode = mapper.valueToTree(jsonMap);
        buffer.append(jsonNode);
        fileWriter[type].write(buffer.toString());
        fileCount[type]++;

        if (MaxFileCount > 0) {
            if (fileCount[type] >= MaxFileCount) {
                flush(type);
            }
        } else {
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
            sf = new File(dir, "B-" + sourceIdx + "-" + scdName + "-" + TYPE_STRING[type] + "-C.JSON");
            tf = new File(dir, "B-" + sourceIdx + "-" + scdName + "-" + TYPE_STRING[type] + "-F.JSON");
        } while (sf.exists() || tf.exists());
        // Temporary & real SCD file are exists.. loop

        return tf;
    }

    @Override
    protected void flush(int type) throws IOException {
        if (fileWriter[type] == null) {
            return;
        }

        fileWriter[type].write(StringUtil.newLine);
        fileWriter[type].write("]");
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
                    System.out.println( "[FileWriter] JSON file delete error. " + tmpName);
                }
            } else {
                String name = "";
                if(isTEST){
                    name = tmpName.substring(0, 25) + "-T.JSON";
                } else if(isMKE){
                    name = tmpName.substring(0, 25) + "-M.JSON";
                }else{
                    name = tmpName.substring(0, 25) + "-C.JSON";
                }

                File file = new File(dir, name);
                if (!tmpFile.renameTo(file)) {
                    System.out.println( "[SCDWriter] SCD file rename error. " + tmpName);
                }else{
                    System.out.println();
                    Log2.out("[SCDManager] [SCD complete File : " + file.getPath()  + "]");
                }

            }
        }
    }

    @Override
    public boolean delete() {
        String path = dir.getAbsolutePath();
        boolean isRet = true;

        if( !FileUtil.deleteFile(path, ".JSON") ){
            Log2.error("[FileManager ] [Json File Delete Fail, Directory: " + path + "]");
            isRet = false;
        }

        if( !FileUtil.deleteFile(path + filterDir, ".txt") ){
            Log2.error("[FileManager ] [Filtered File Delete Fail, Directory: " + path + "]");
            isRet = false;
        }

        return isRet;
    }
}
