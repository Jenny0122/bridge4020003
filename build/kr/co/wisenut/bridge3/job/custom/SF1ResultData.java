/*
 * @(#)CustomData.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.job.custom;

import kr.co.wisenut.common.Exception.CustomException;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.sf1api.*;
import kr.co.wisenut.common.util.PropsReader;
import kr.co.wisenut.common.util.StringUtil;

import java.io.File;
import java.io.IOException;

/**
 *
 * CustomData
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class SF1ResultData implements ICustom 
{
	private static String sf1ver = "";
    private static String ip = "127.0.0.1";
    private static int port = 0;
    private static int timeout = 10000;
    private static String charSet = null;
    private static String searchField = null;
    private static boolean READ_PROP = false;

    private int RESULT_COUNT = 500;
    private String collectionName = null;
    private String exqueryField = null;

    private StringBuffer docidBuf = new StringBuffer(1024);

    public SF1ResultData() 
    {
        try 
        {
            setProperty();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    public String customData(String str) throws CustomException {
        int idx = 0;
        String[] condition = str.split(",");

        if(condition.length == 2) {
            this.collectionName = condition[0];
            docidBuf.setLength(0);
            while(searchResult(condition[1], idx*RESULT_COUNT) != 0) {
                idx++;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }

        } else {
            throw new CustomException("ExQuery String Error : " + str);
        }
        return docidBuf.toString();
    }

    private int searchResult(String str, int startIdx) throws CustomException 
    {
    	SF1ApiSearch api = new SF1ApiSearch(this.sf1ver);
    	
    	int ret = 0;
    	
    	ret = api.setCharSet(charSet);
    	ret = api.setCommonQuery("", 0);
    	ret = api.setCollection(collectionName);
    	ret = api.setQueryAnalyzer(collectionName, 1, 1, 1, 0);
    	ret = api.setPageInfo(collectionName, startIdx, RESULT_COUNT);
    	ret = api.setSortField(collectionName, "DATE", 0);
    	ret = api.setSearchField(collectionName, searchField);
    	
    	//*****prefix field �꽕�젙
    	Log2.debug("[SF1ResultData] [searchResult] ExQuery is \"" + str + "\"", 3);
    	ret = api.setExQuery(collectionName, str, 1);
    	//*****prefix field �꽕�젙
    	
    	ret = api.setDocumentField(collectionName, "DOCID", 0);
    	ret = api.getConnection(this.ip, this.port, this.timeout);

        if(ret!=0) 
        {
            throw new CustomException("Connect Server: " + api.getErrorInfo() );
        }
    	
        ret = api.getSearchResult(3);
        
        if(ret!=0) 
        {
            throw new CustomException("Connect Server: " + api.getErrorInfo() );
        }
    	
        int totalCount 	= api.getTotalCount(collectionName);
        int count 		= api.getResultCount(collectionName);
        
        for(int idx=0 ; idx < count ; idx++) 
        {
            docidBuf.append("<DOCID>").append(api.getField(collectionName, "DOCID", idx)).append(StringUtil.newLine);
        }
        
        Log2.debug("ResultCount/TotalResultCount("+count+"/"+totalCount+"), PageInfo("+startIdx + ","+RESULT_COUNT+")", 3);
        
        return count;
    }

    private void setProperty() throws IOException {
        if( !READ_PROP ) {
            String sf1_home = "" ;
            String sf1Properties = "";
            if(System.getProperty("wcse_home") != null){
            	sf1_home = System.getProperty("wcse_home");
                sf1Properties = "config/sf1.properties";
            }else if(System.getProperty("sf1_home") != null){
            	sf1_home = System.getProperty("sf1_home");
                sf1Properties = "config/sf1.properties";
            }else {
                throw new IOException("sf1_home or wcse_home not set !!");
            }

            File propFile = new File(sf1_home, sf1Properties);
            PropsReader prop = new PropsReader(propFile.getPath());

            this.sf1ver = prop.getProperty("sf1.ver");
            this.ip = prop.getProperty("server.ip");
            this.port = StringUtil.parseInt(prop.getProperty("server.port"), 0);
            this.timeout = StringUtil.parseInt(prop.getProperty("server.timeout"), 1000);
            this.charSet = prop.getProperty("search.charset");
            this.collectionName = prop.getProperty("collection.name");
            this.searchField = prop.getProperty("search.field");
            this.exqueryField = prop.getProperty("exquery.field");
            Log2.debug("server.ip=" + this.ip, 3);
            Log2.debug("server.port="+this.port, 3);
            Log2.debug("server.charset="+this.charSet, 3);

            READ_PROP = true;
        }
    }

    public static void main(String[] args) throws CustomException {
        SF1ResultData sf1 = new SF1ResultData();
        sf1.customData("");
    }
}
