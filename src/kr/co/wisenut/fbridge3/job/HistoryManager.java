/*
 * @(#)HistoryManager.java   3.8.1 2009/03/11
 *
 */

package kr.co.wisenut.fbridge3.job;

import java.io.*;
import java.util.Enumeration;
import java.util.List;

import kr.co.wisenut.common.util.EncryptUtil;
import kr.co.wisenut.fbridge3.fileinfo.FileInfoSet;
import kr.co.wisenut.fbridge3.fileinfo.FileInfoList;
import kr.co.wisenut.fbridge3.fileinfo.FileInfo;
import kr.co.wisenut.common.logger.Log2;

/**
 *
 * HistoryManager
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class HistoryManager {
    public static final int MODE_NONE = 0;
    public static final int MODE_NEW = 1;
    public static final int MODE_UPD = 2;
    public static final int MODE_DEL = 3;

    protected  String dirListFile = "dirlist";
    protected  File histDir;
    protected  FileInfoSet dirInfoSet;

    public HistoryManager(String histDir) {
        this(new File(histDir));
    }

    public HistoryManager(File histDir) {
        this.dirInfoSet = new FileInfoSet();
        this.histDir = histDir;

        // Create the history directory, if not exists
        if (!histDir.exists()) {
            histDir.mkdirs();
            Log2.debug( "[HistoryManager] [Make directory for history.]", 3);
        }
    }

    public void loadHistory() throws IOException {
        Log2.debug( "[HistoryManager] [History fileinfo loading...]", 3);

        File dirList = new File(histDir, dirListFile);
        if (!dirList.exists()) {
            Log2.debug( "[HistoryManager] [No exists history fileinfo.]", 3);
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(dirList),
                "UTF-8" )
        );

        String strLine;
        while ( (strLine = reader.readLine()) != null) {
            try {
                FileInfo info = new FileInfo(strLine);
                dirInfoSet.putInfo(info);
            }
            catch (Exception e) {}
        }
        reader.close();
        Log2.debug( "[HistoryManager] [History fileinfo loaded.]", 3);
    }

    public void saveHistory() throws IOException {
        Log2.debug(  "[HistoryManager] [History fileinfo saving...]", 3);

        File dirList = new File(histDir, dirListFile);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dirList),
                "UTF-8"
        )
        );

        Enumeration enumer = dirInfoSet.keys();
        while (enumer.hasMoreElements()) {
            String path = (String) enumer.nextElement();
            FileInfo info = dirInfoSet.getInfo(path);

            writer.write(info.path);
            writer.write("\t");
            writer.write(info.docid);
            writer.write("\t");
            writer.write(Long.toString(info.lastModified));
            writer.newLine();
        }
        writer.close();
        Log2.debug( "[HistoryManager] [History fileinfo saved.]", 3);
    }

    public void clearHistory() {
        Log2.debug(  "[HistoryManager] [History fileinfo clearing...]", 3);

        File dirList = new File(histDir, dirListFile);
        if (dirList.exists() && !dirList.delete()) {
            Log2.debug(  "[HistoryManager] [History fileinfo delete error. " + dirListFile+"]" , 3);
        }

        String[] files = histDir.list();
        for (int i = 0; i < files.length; i++) {
            if (files[i].length() == 32) {
                File f = new File(histDir, files[i]);
                if (f.isFile() && !f.delete()) {
                    Log2.debug( "[HistoryManager] [History fileinfo delete error.  "+ files[i]+"]" , 3);
                }
            }
        }
        dirInfoSet.clear();
        Log2.debug(  "[HistoryManager] [History fileinfo cleared.]" ,3);
    }

    public String getDocid(File file) {
        String path = file.getAbsolutePath();
        return EncryptUtil.MD5(path);
    }

    public int getDirectoryMode(File dir) {
        String path = dir.getAbsolutePath();
        FileInfo info = dirInfoSet.getInfo(path);

        if (info == null) {
            Log2.debug(  "[HistoryManager] [Added. " + path+"]" ,3);
            return MODE_NEW;
        }
        else if (dir.lastModified() != info.lastModified) {
            Log2.debug( "[HistoryManager] [Modified. " + path+"]", 3);
            return MODE_UPD;
        }
        return MODE_NONE;
    }

    public void setDirectoryInfo(File dir) {
        String path = dir.getAbsolutePath();
        FileInfo info = dirInfoSet.getInfo(path);
        if (info != null) {
            info.lastModified = dir.lastModified();
        }
        else {
            FileInfo fi = new FileInfo(path, getDocid(dir), dir.lastModified());
            dirInfoSet.putInfo(fi);
        }
    }

    public FileInfo getDirectoryInfo(File dir) {
        String path = dir.getAbsolutePath();
        return dirInfoSet.getInfo(path);
    }

    public FileInfoList getRemovedDirs() {
        FileInfoList list = new FileInfoList();
        Enumeration enumer = dirInfoSet.keys();

        while (enumer.hasMoreElements()) {
            String path = (String) enumer.nextElement();
            File dir = new File(path);

            if (!dir.exists()) {
                FileInfo info = dirInfoSet.getInfo(path);
                list.addInfo(info);
                Log2.debug( "[HistoryManager] [Removed. " + path+"]", 3);
            }
        }
        return list;
    }

    public void removeDirectoryInfos(FileInfoList infoList) {
        for (int i = 0; i < infoList.size(); i++) {
            FileInfo info = infoList.getInfo(i);
            dirInfoSet.removeInfo(info);

            File file = new File(histDir, info.docid);
            if (file.exists() && !file.delete()) {
                Log2.debug(  "[HistoryManager] [History fileinfo delete error. " + info.docid+"]" ,3);
            }
            else {
                Log2.debug(  "[HistoryManager] [History fileinfo deleted. " + info.docid+"]", 3);
            }
        }
    }

    public int getFileMode(FileInfoSet infoSet, File file) {
        String path = file.getAbsolutePath();
        FileInfo info = infoSet.getInfo(path);

        if (info == null) {
            Log2.debug(  "[HistoryManager] [Added. " + path+"]", 3);
            return MODE_NEW;
        }
        else if (file.lastModified() != info.lastModified) {
            Log2.debug( "[HistoryManager] [Added. " + path+"]", 3);
            return MODE_UPD;
        }
        return MODE_NONE;
    }

    public void saveDirectoryInfo(File dir) throws IOException {
        String docid = getDocid(dir);
        File histFile = new File(histDir, docid);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(histFile),
                "UTF-8"
        )
        );
        
        if(dir.isDirectory() && dir.exists()) {
        	File[] files = dir.listFiles();
      
            if(files != null) {
    	        for (int i = 0; i < files.length; i++) {
    	            if (files[i].isFile()) {
    	                writer.write(files[i].getAbsolutePath());
    	                writer.write("\t");
    	                writer.write(getDocid(files[i]));
    	                writer.write("\t");
    	                writer.write(Long.toString(files[i].lastModified()));
    	                writer.newLine();
    	            }
    	        }
            }
        }
        
        writer.flush();
        writer.close();

        Log2.debug( "[HistoryManager] [History rebuild. " + docid+"]", 3);
    }

    public FileInfoSet getFileInfos(File dir) throws IOException {
        return getFileInfos(getDocid(dir));
    }

    public FileInfoSet getFileInfos(String docid) throws IOException {
        FileInfoSet fileInfoSet = new FileInfoSet();
        File histFile = new File(histDir, docid);

        if (!histFile.exists()) {
            return fileInfoSet;
        }

        BufferedReader reader = new BufferedReader((new InputStreamReader(
                new FileInputStream(histFile),
                "UTF-8")
        )
        );
        String strLine;
        while ( (strLine = reader.readLine()) != null) {
            FileInfo info = new FileInfo(strLine);
            fileInfoSet.putInfo(info);
        }
        reader.close();

        return fileInfoSet;
    }

    public FileInfoList getFileInfoList(File dir) throws IOException {
        return getFileInfoList(getDocid(dir));
    }

    public FileInfoList getFileInfoList(String docid) throws IOException {
        FileInfoList fileList = new FileInfoList();
        File histFile = new File(histDir, docid);

        if (!histFile.exists()) {
            return fileList;
        }

        BufferedReader reader = new BufferedReader((new InputStreamReader(
                new FileInputStream(histFile),
                "UTF-8")
        )
        );
        String strLine;
        while ( (strLine = reader.readLine()) != null) {
            FileInfo info = new FileInfo(strLine);
            fileList.addInfo(info);
        }
        reader.close();

        return fileList;
    }

    public FileInfoList getRemovedFiles(FileInfoSet infoSet) {
        FileInfoList list = new FileInfoList();
        Enumeration enumer = infoSet.keys();

        while (enumer.hasMoreElements()) {
            String path = (String) enumer.nextElement();
            File file = new File(path);

            if (!file.exists()) {
                FileInfo info = infoSet.getInfo(path);
                list.addInfo(info);
                Log2.debug( "[HistoryManager] [Removed. " + path+"]", 3);
            }
        }
        return list;
    }
}