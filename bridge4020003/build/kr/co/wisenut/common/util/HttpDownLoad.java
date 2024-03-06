/*
 * @(#)HttpDownLoad.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.util;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.io.IOUtil;
import kr.co.wisenut.common.util.punycode.Punycode;
import kr.co.wisenut.common.util.punycode.PunycodeException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * HttpDownLoad
 *
 * Copyright 2001-2012 WISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 16 Apr 2012
 *
 * @author  WISEnut
 * @version 3.8,5. 2012/04/16 Bridge Release
 *
 */
public class HttpDownLoad {
    public  int FileDownLoad(String TgUrl, String DwFileName){
        URL SourceURL = null;
        InputStream is;
        int data=0;
        try
        {
            if(!TgUrl.equals("")){
                String Temp[] = StringUtil.split(TgUrl, " ");
                TgUrl = Temp[0];
                for(int i=1; i<Temp.length; i++){
                    TgUrl = TgUrl + "%20" + Temp[i] ;
                }
                Log2.debug("[HttpDownLoad] [Target URL" + TgUrl + "]", 3);
                Log2.debug("[HttpDownLoad] [DownLoad File Name" + DwFileName + "]", 3);
                try {
                    TgUrl = getCheckToPunyCodeURL(TgUrl);
                } catch (PunycodeException e) {
                    Log2.error("[HttpDownLoad] [PunycodeException URL, " + TgUrl + "]");
                    Log2.error("[HttpDownLoad] [PunycodeException, " + IOUtil.StackTraceToString(e) + "]");
                }
                SourceURL = new URL(TgUrl);
            } else{
                Log2.debug("[HttpDownLoad] [Url Path Nothing!!!.]", 2);
                return -1;
            }

            //file download
            File OutFilterFile = new File(DwFileName);
            FileOutputStream fos = new FileOutputStream(OutFilterFile);

            try {
                is = SourceURL.openStream();
            } catch(MalformedURLException e) {
                Log2.error("[HttpDownLoad] [MalformedURLException, URL Format Info Error!!!]");
                fos.close();
                return -1;
            } catch( IOException e ) {
                Log2.error("[HttpDownLoad] [URL IO Error!!! " + IOUtil.StackTraceToString(e) + "]");
                fos.close();
                return -1;
            }
            byte[] _tmp = new byte[1024*2];

            while ((data = is.read(_tmp)) != -1) {
                fos.write(_tmp, 0, data);
            }
            
            fos.close();
        }catch( IOException e){
            Log2.error("[HttpDownLoad] [URL IO Error!!! " + IOUtil.StackTraceToString(e) + "]");
        }
        return 0;

    }

    /**
     * CHECK HANGUL DOMAIN ADDRESS
     * create 2012.04.16 by nocode
     * @param strURL INPUT URL
     * @return TARGET URL
     * @throws kr.co.wisenut.common.util.punycode.PunycodeException exception
     */
    private String getCheckToPunyCodeURL(String strURL) throws PunycodeException {
        String targetURL = "";
        //step 1. domain extract
        String pattern = "^(?:[^/]+://)?([^/:]+)";
        Matcher matcher = Pattern.compile(pattern).matcher(strURL);
        int domainStart = 0;
        int domainEnd = 0;
        String strTempURL = "";
        if (matcher.find()) {
            domainStart = matcher.start(1);
            domainEnd = matcher.end(1);
            strTempURL = strURL.substring(domainStart, domainEnd);
        }
        //step 2. check hangule
        if(!checkEng(strTempURL)) {
            String[] hanGulDomain = strTempURL.split("\\.");
            int length = hanGulDomain.length;
            String strHanguleDomain = "";
            for(int i=0; i < length; i++) {
                if(isKorean(hanGulDomain[i])) {   //step 3. convert hangul to punycode
                    strHanguleDomain += "xn--"+ Punycode.encode(hanGulDomain[i]);
                } else{
                    strHanguleDomain += hanGulDomain[i];
                }
                if( i < (length-1) ){
                    strHanguleDomain += ".";
                }
            }

            //merge target URL
            if(domainStart > 0 && domainEnd > 0) {
                targetURL = strURL.substring(0, domainStart) + strHanguleDomain + strURL.substring(domainEnd, strURL.length());
            }else{
                targetURL = strURL;
            }
        }else{
            targetURL = strURL;
        }

        return targetURL;
    }
    
    private boolean isKorean(String str) {
        int inputLength = str.length();
        for(int i=0; i < inputLength; i++) {
            char c = str.charAt(i);
            if((c >= '\uAC00' & c <= '\uD7AF') ||
                    (c >= '\u1100' && c <='\u11ff') ||
                    (c >= '\u3130' && c <='\u318F')) {
                return true;
            }
        }
        return false;
    }

    private boolean checkEng(String str){
        for ( int d = 0 ; d < str.length() ; d++){
            char chr = str.charAt(d) ;
            String hex = Integer.toHexString(chr) ;

            //is ASCII ? -- convert to hex String
            if(Integer.parseInt(hex, 16) < 0x80){

                continue;
            }else{

                return false;

            }// of if

        }// of for

        return true;

    }// of checkEng();
}
