/*
 * @(#)URLDB.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.bdb;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.io.IOUtil;
import com.sleepycat.je.*;
import com.sleepycat.bind.tuple.TupleBinding;
import java.io.File;

/**
 *
 * URLDB - URL Database KEY and Value SAVE, LOAD
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class URLDB{
    private Environment urlDbEnvironment;
    private Database urlDatabase;
    private Database urlQ;
    private Cursor qCursor;

    /**
     * URLDB init class
     * @param dbEnvPath environment file path
     * @param clean initialize
     * @return success or fail
     */
    public boolean init(String dbEnvPath, boolean clean) {
        try {
            if(clean) {
                String separator = System.getProperty("file.separator");
                String[] dir = new java.io.File(dbEnvPath).list();
                File dbFile;
                java.util.Arrays.sort(dir);
                for(int i = 0; i < dir.length; i ++){
                    dbFile = new File(dbEnvPath + separator + dir[i]);
                    dbFile.delete();
                }
            }

            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setAllowCreate(true);
            urlDbEnvironment = new Environment(new File(dbEnvPath), envConfig);

            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            urlDatabase = urlDbEnvironment.openDatabase(null, "URLDB", dbConfig);
            urlQ = urlDbEnvironment.openDatabase(null, "URLQ", dbConfig);
            qCursor = urlQ.openCursor(null, null);

        } catch(Throwable e) { // throwable 로 잡히는 에러가 존재함. je.jar 버전 문제로
            Log2.error("[URLDB] [DB initialize fail : " +"\n"+IOUtil.StackTraceToString(e)+"]");
            return false;
        }

        return true;
    }
    /**
     * Berkely DB Cloase
     * @return success or fail
     */
    public boolean deinit(){
        try {
            if(qCursor != null){
                qCursor.close();
            }
            if(urlDatabase != null) {
                urlDatabase.close();
            }
            if(urlQ != null) {
                urlQ.close();
            }
            if(urlDbEnvironment != null) {
                urlDbEnvironment.close();
            }
        } catch(DatabaseException e) {
            Log2.error("[URLDB] [DB Deinit Fail : "
                    +"\n"+IOUtil.StackTraceToString(e)+"]");
            return false;
        }
        return true;
    }

    /**
     *
     * @param url url address
     * @return : success or fail
     */
    public boolean isExist(RichURL url) {
        try {
            return urlDatabase.get(null, url.theKey, url.theData, LockMode.DEFAULT)
                    == OperationStatus.SUCCESS;
        }catch(DatabaseException e) {
            Log2.error("[URLDB] [DB isExist Exception : "
                    +"\n"+IOUtil.StackTraceToString(e)+"]");
            return false;
        }
    }

    /**
     *
     * @param url url address
     * @return : success or fail
     */
    public boolean isQExist(RichURL url) {
        try {
            return urlQ.get(null, url.theKey, url.theData, LockMode.DEFAULT)
                    == OperationStatus.SUCCESS;
        }catch(DatabaseException e) {
            Log2.error("[URLDB] [DB isExist Exception : "
                    +"\n"+IOUtil.StackTraceToString(e)+"]");
            return false;
        }
    }

    /**
     * urlQ Put url method
     * @param url url address
     * @return : success or fail
     */
    public boolean putQ(RichURL url) {
        try {
            urlQ.putNoOverwrite(null, url.theKey, url.theData);
        } catch(DatabaseException e) {
            Log2.error("[URLDB] [DB URL Queue Insert  Fail : "
                    +"\n"+IOUtil.StackTraceToString(e)+"]");
            return false;
        }
        return true;
    }
    /**
     * urlDatabase insert url method
     * @param url url address
     * @return : success or fail
     */
    public boolean insert(RichURL url) {
        try  {
            urlDatabase.putNoOverwrite(null, url.theKey, url.theData);
        } catch(DatabaseException e) {
            Log2.error("[URLDB] [DB InSert Fail: "
                    +"\n"+IOUtil.StackTraceToString(e)+"]");
            return false;
        }
        return true;
    }

    /**
     * @param url url address
     * @return : success or fail
     */
    public boolean getQ(RichURL url) {
        try {
            if(qCursor.getFirst(url.theKey, url.theData, LockMode.DEFAULT)
                    ==OperationStatus.SUCCESS)  {
                qCursor.delete();
                return true;
            }  else{
                return false;
            }
        } catch(DatabaseException e){
            Log2.error("[URLDB] [DB Get Queue URL Fail : "
                    +"\n"+IOUtil.StackTraceToString(e)+"]");
            return false;
        }
    }

    /**
     * @param url url address
     * @return : success or fail
     */
    public String searchQ(RichURL url) {
        String strLinkUrl = "";
        try {
            OperationStatus retVal = urlQ.get(null, url.theKey, url.theData, LockMode.DEFAULT);
            if(retVal != OperationStatus.SUCCESS){
                return "";
            }
            TupleBinding linkUrlBinding = new LinkNameBinding();
            RichURL theLinkUrl =
                    (RichURL)linkUrlBinding.entryToObject(url.theData);
            strLinkUrl = theLinkUrl.getSource("UTF-8");
        } catch(DatabaseException e){
            Log2.error("[URLDB] [DB Get Queue URL Fail : "
                    +"\n"+IOUtil.StackTraceToString(e)+"]");
        }
        return  strLinkUrl;
    }

    public boolean Sync() {
        try {
            urlDbEnvironment.sync();
        } catch(DatabaseException e) {
            Log2.error("[URLDB] [DB URL Queue Insert  Fail : "
                    +"\n"+IOUtil.StackTraceToString(e)+"]");
            return false;
        }
        return true;
    }

    /**
     * @param url url address
     * @return : success or fail
     */
    public boolean deleteQ(RichURL url)  {
        try {
            urlQ.delete(null, url.theKey);
            //urlDbEnvironment.sync();
        }catch(DatabaseException e) {
            Log2.error("[URLDB] [DB Delete URL Fail :  "+ e.getMessage()+"]");
            return false;
        }
        return true;
    }

    /**
     * @param url url address
     * @return : success or fail
     */
    public boolean delete(RichURL url)  {
        try {
            urlDatabase.delete(null, url.theKey);
            //urlDbEnvironment.sync();
        }catch(DatabaseException e) {
            Log2.error("[URLDB] [DB Delete URL Fail :  "+ e.getMessage()+"]");
            return false;
        }
        return true;
    }
}
