/*
 * @(#)GetURLContentsThread.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.job;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.xerces.utils.Base64;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.io.IOUtil;
import kr.co.wisenut.wbridge3.chardet.HtmlCharsetDetector;
import kr.co.wisenut.wbridge3.chardet.nsDetector;
import kr.co.wisenut.wbridge3.chardet.nsICharsetDetectionObserver;
import kr.co.wisenut.wbridge3.html.HtmlDocument;
import kr.co.wisenut.wbridge3.url.httpclient.Header;
import kr.co.wisenut.wbridge3.url.httpclient.HttpClient;
import kr.co.wisenut.wbridge3.url.httpclient.methods.GetMethod;

/**
 *
 * GetURLContentsThread  - HTML Document file stream processing Thread class
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class GetURLContentsThread extends Thread{
    private String userCharSet = "";
    private String contentsCharSet = "";
    private String url = "";
    private String authID = "";
    private String authPW = "";
    private String pageURLContents = "";

    public GetURLContentsThread (String userCharSet, String url, String authID, String authPW) {
        this.userCharSet = userCharSet;
        this.url = url;
        this.authID = authID;
        this.authPW = authPW;
    }

    public String getContentsCharSet() {
        return contentsCharSet;
    }

    public String getPageURLContents() {
        return pageURLContents;
    }

    public void run() {
        GetMethod get ;
        HttpClient m_httpClient = new  HttpClient();
        try {
            get = new GetMethod(url);

        } catch(IllegalArgumentException e) {
            Log2.error("[Job] [Get URL Method : " + e +" ]");
            return ;
        }
        if(get == null) {
            return ;
        }
        //get.setRequestHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 4.0)");
        //get.setRequestHeader("User-Agent","Mozilla/5.0 (compatible; MSIE 7.0; Windows NT 6.0)");
        
        String agent = System.getProperty("http.agent");
        if(agent == null) {
        	agent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 4.0)";
        }
        
        get.setRequestHeader("User-Agent", agent);

        try {
        	if(!authID.equals("") && !authPW.equals("")) {
				String authString = authID + ":" + authPW;
				byte[] authEncBytes = Base64.encode(authString.getBytes());
				String authStringEnc = new String(authEncBytes);
				
//				Log2.out("[info] Apache Authenticate Running...");
//				Log2.out("[info] AuthID : " + authID);
//				Log2.out("[info] AuthPW : " + authPW);
				
				get.setRequestHeader("Authorization", "Basic " + authStringEnc);
			}
            m_httpClient.executeMethod(get);
        } catch (IOException e) {
            get.releaseConnection();
            return ;
        }

        Header h = get.getResponseHeader("Set-Cookie");
        if(h != null) { //Cookie set
            get.setRequestHeader("Set-Cookie", h.getValue());
            try {
                m_httpClient.executeMethod(get);
            } catch (IOException e) {
                get.releaseConnection();
                return ;
            }
        }
        
        h = get.getResponseHeader("Content-Type");
        if( h.getValue().startsWith("text/") == false ) {
        	pageURLContents = null; // mark binary
            get.releaseConnection();
            return ;
        }

        int retCode = get.getStatusCode();
        if(retCode > 200) return ;
        String content = "";
        try {
        	String contentEncoding = getContentEncoding(url, authID, authPW);
        	
            content = get.getResponseBodyAsString(contentEncoding);
            if(content.getBytes().length > (1024*1024)){  //1MB over.
                return ;
            }

            String charset = HtmlDocument.getMetaContetCharset(content.getBytes()).toLowerCase().trim();
            charset = charset.replaceAll("'", "");
            charset = charset.replaceAll("\"", "");
            this.contentsCharSet = charset.toLowerCase();
            if(charset.equals("")) {
                if(this.userCharSet.equals("")){
                    byte[] contentByte = get.getResponseBody(contentEncoding);
                    setHtmlCharSet(contentByte);
                    this.contentsCharSet = getRetCharSet().toLowerCase();
                    charset = getRetCharSet().toLowerCase();

                }else{
                    this.contentsCharSet = this.userCharSet;
                    charset = this.userCharSet;
                }
            }
            if(charset.equals("")) {
                this.contentsCharSet = get.getResponseCharSet().toLowerCase();
                charset = get.getResponseCharSet().toLowerCase();
            }
            if( charset !=null && !charset.equals("") ){
                byte[] contentByte = get.getResponseBody(contentEncoding);
                charset = charset.trim();
                pageURLContents = new String(contentByte, charset);
            }
            
            // check binary or text
            for(int p = 0; p < pageURLContents.length() && p < 1024; p++) {
            	switch(pageURLContents.charAt(p)) {
            	case 0:
            		pageURLContents = null;
                    get.releaseConnection();
                    return;
            	}
            }
        } catch (IOException e) {
            get.releaseConnection();
            return ;
        }

        Log2.debug("[Job] [Get URL Contents : Get HTML input stream working done]", 3);
        get.releaseConnection();
    }

    /**
     * Returns http content-encoding header field.
     * @param url
     * @return
     */
	private String getContentEncoding(String url, String authID, String authPW) {
		String contentEncoding = null;
		HttpURLConnection con = null;
		try {
			URL sourceUrl = new URL(url);
			con = (HttpURLConnection)sourceUrl.openConnection();
			/*
			if(!authID.equals("") && !authPW.equals("")) {
				String authString = authID + ":" + authPW;
				byte[] authEncBytes = Base64.encode(authString.getBytes());
				String authStringEnc = new String(authEncBytes);
				
				Log2.out("[info] Apache Authenticate Running...");
				Log2.out("[info] AuthID : " + authID);
				Log2.out("[info] AuthPW : " + authPW);
				
				con.setRequestProperty("Authorization", "Basic " + authStringEnc);
			}
			*/
			contentEncoding = con.getContentEncoding();			
		} catch (Exception e) {
			Log2.error("[Job] [getContentEncoding() error.\n" + IOUtil.StackTraceToString(e));
		} finally {
			if(con != null) {
				con.disconnect();
			}
		}
		return contentEncoding;
	}

    private String getRetCharSet() {
        return retCharSet;
    }

    private void setRetCharSet(String retCharSet) {
        this.retCharSet = retCharSet;
    }

    private String retCharSet = "";

    private void setHtmlCharSet(byte[] htmlData) {
        nsDetector det = new nsDetector(6) ; //default
        det.Init(new nsICharsetDetectionObserver() {
            public void Notify(String charset) {
                HtmlCharsetDetector.found = true ;
                setRetCharSet(charset);
            }
        });

        ByteArrayInputStream str = new ByteArrayInputStream(htmlData);
        BufferedInputStream imp = new BufferedInputStream(str);

        byte[] buf = new byte[1024] ;
        int len;
        boolean done = false ;
        boolean isAscii = true ;
        try{
            while( (len=imp.read(buf,0,buf.length)) != -1) {
                // Check if the stream is only ascii.
                if (isAscii)
                    isAscii = det.isAscii(buf,len);
                // DoIt if non-ascii and not done yet.
                if (!isAscii && !done)
                    done = det.DoIt(buf,len, false);
            }
            det.DataEnd();
        }catch(IOException e) {}
    }
}
