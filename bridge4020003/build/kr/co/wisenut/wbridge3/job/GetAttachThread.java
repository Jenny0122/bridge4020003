/*
 * @(#)GetAttachThread.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.job;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;

import kr.co.wisenut.common.logger.Log2;

import org.apache.xerces.utils.Base64;

/**
 *
 * CustomFileNameFilter - Binary file download Thread class
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class GetAttachThread extends Thread{
    private String seed = "";
    private String charset = "";
    private String filterRoot = "";
    private boolean isFiltered = true;
    private String authID = "";
    private String authPW = "";

    public GetAttachThread(String seed, String charset, String filterRoot, String authID, String authPW){
        this.seed = seed;
        this.charset = charset;
        this.filterRoot = filterRoot;
        this.authID = authID;
        this.authPW = authPW;
    }

    public void run() {
        int data ;
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            //seed = seed.replaceAll(" ", "%20");
            URL myURL = new URL(seed);

            HttpURLConnection objURLConnection = (HttpURLConnection)myURL.openConnection();
            objURLConnection.setDoOutput(true);
            objURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            objURLConnection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0)");
            objURLConnection.setRequestProperty("Accept-Language", charset);
            
            if(!authID.equals("") && !authPW.equals("")) {
				String authString = authID + ":" + authPW;
				byte[] authEncBytes = Base64.encode(authString.getBytes());
				String authStringEnc = new String(authEncBytes);
				
//				Log2.out("[info] Apache Authenticate Running...");
//				Log2.out("[info] AuthID : " + authID);
//				Log2.out("[info] AuthPW : " + authPW);
				
				objURLConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			}

            int response = objURLConnection.getResponseCode();
            if(response > 200 ) {
                return;
            }

            is = objURLConnection.getInputStream();
            fos = new FileOutputStream(filterRoot);
            byte[] _tmp = new byte[4096];
            while ((data = is.read(_tmp)) != -1) {
                fos.write(_tmp, 0, data);
            }
        }catch(Exception e) {
            isFiltered = false;
        }finally{
            if(fos != null) {
                try {
                    fos.close();
                    fos.flush();
                } catch (IOException e) {}

            }
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) { }
            }
        }
    }

    public boolean isFiltered() {
        return isFiltered;
    }
}
