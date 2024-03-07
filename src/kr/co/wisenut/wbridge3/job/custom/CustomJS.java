/*
 * @(#)CustomJS.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.job.custom;

import kr.co.wisenut.common.Exception.CustomException;
import kr.co.wisenut.wbridge3.seed.SeedItem;
import kr.co.wisenut.common.util.HtmlUtil;

/**
 *
 * CustomJS - Javascript Link to normal url convert custom class
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class CustomJS  implements ICustom{
    HtmlUtil parser = new HtmlUtil();
    public CustomJS(){}

    /**
     * JavaScript Extract URL: customData Method
     * @param item seeditem
     * @param inputURL crawling url
     * @return Generate URL
     * @throws CustomException error
     */
    public String customData(SeedItem item, String inputURL) throws CustomException{
        //Log2.debug("[CustomClass] [Input Source Name: "+item.getSource()+"]",4);
        String retURL = inputURL;
        //Use Case : javascript:jump('./board.jsp?id=Freeboard&mode=list&page=2', '')
        if (((item.getSource()).equals("koreanotes")) && (inputURL.indexOf("jump(")) != -1) {
            String url = "";
            int startNum = inputURL.indexOf("board.jsp?");
            int endNum = inputURL.indexOf("', '')");
            if(startNum > -1 && endNum > -1) {
                url = inputURL.substring(startNum + "board.jsp?".length(), endNum);
            }
            retURL = "http://www.koreanotes.com/hohoboard/board.jsp?" + url ;
        }

        //Use Case : javascript:read('./board.jsp?id=Freeboard&mode=read&num=125')
        if (((item.getSource()).equals("koreanotes")) && (inputURL.indexOf("read(")) != -1) {
            String url = "";

            int startNum = inputURL.indexOf("board.jsp?");
            int endNum = inputURL.indexOf("')");
            if(startNum > -1 && endNum > -1) {
                url = inputURL.substring(startNum + "board.jsp?".length(), endNum);
            }
            retURL = "http://www.koreanotes.com/hohoboard/board.jsp?" + url ;
        }

        /*document.BZB.idx.value= idx;
        document.BZB.action= "List.asp";
        document.BZB.submit();*/
        if (((item.getSource()).equals("TEST")) && (inputURL.indexOf("BZB4_goRead")) != -1) {
            String url = "";
            int startNum = inputURL.indexOf("BZB4_goRead(");
            int endNum = inputURL.indexOf(")");
            if(startNum > -1 && endNum > -1) {
                url = inputURL.substring(startNum + "BZB4_goRead(".length(), endNum);
            }
            retURL = "http://www.jaseng.co.kr/menu/intro/intro_7/intro_4/List.asp?idx=" + url;
        }

        return retURL;
    }

    /**
     * User Defined Data(Config File in Type Name): customUserData Method
     * @param item seeditem
     * @param content html content
     * @param userTagName defined tag name
     * @return  User Define Data Value
     * @throws CustomException error
     */
    public String customUserDefine(SeedItem item, String content, String userTagName) throws CustomException{
       String strValue="";
       String temp = "" ;
       int maxLen = 10;
        if(userTagName.indexOf("=") > -1){
            temp = userTagName.substring(userTagName.indexOf("=")+1, userTagName.length() );
        }else{
            return strValue ;
        }

        int pos = temp.indexOf(":");
        if(pos > -1){
            String StartStr = temp.substring(0, pos) ;
            String tagCnt = temp.substring(pos+1, temp.length());
            /*try {
                strValue = parser.getTagValue(content.getBytes(), StartStr, Integer.parseInt(tagCnt), false);
            } catch (Exception e) {
                Log2.error("["+"\n"+IOUtil.StackTraceToString(e)+"]");
            }*/
        } else{
            return strValue;
        }

        if (((item.getSource()).equals("koreanotes"))) {
            String startStr = "Writer:";
            int startNum = strValue.indexOf(startStr);
            int endNum = startNum +startStr.length()+4;
            if(startNum > -1 && endNum > -1) {
                strValue = strValue.substring(startNum +startStr.length(), endNum);
                if(strValue.length() > maxLen){
                    strValue = "";
                }
            }else{
                  if(strValue.length() > maxLen){
                    strValue = "";
                }
            }
        }

         // TODO add your code here

        return strValue;
    }
    
    public String customData(String str) throws CustomException{
    	return "";
    }
    
}
