/*
 * @(#)WSParseXML.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.job.custom;

import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.io.IOUtil;
import kr.co.wisenut.common.Exception.CustomException;

import java.io.*;

/**
 *
 * WSParseXML
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class WSParseXML implements ICustom {
    private String xslPath;
    private File xslFile;

    public WSParseXML() {
        String sf1_home = "" ;
        if(System.getProperty("wcse_home") != null) {
        	sf1_home = System.getProperty("wcse_home") ;
        } else if(System.getProperty("sf1_home") != null) {
        	sf1_home = System.getProperty("sf1_home") ;
        }
        xslPath = new StringBuffer().append(sf1_home).append(FileUtil.getFileSeperator()).append("config").append(FileUtil.getFileSeperator()).append("kldg_view.xsl").toString();
        xslFile = new File(xslPath);
        if( !xslFile.exists() ) {
            xslFile = null;
        }
    }

    public String customData(String str) throws CustomException{
        try {
            if(str.indexOf("<?xml") != -1) {
                str = transform(str);
            }
        } catch (Exception e) {
            throw new CustomException( IOUtil.StackTraceToString(e)+"\n]");
        }
        return str;
    }

    protected String transform(String data) throws Exception {
        String xmlStr = data;
        StringReader reader = new StringReader(xmlStr);

        String htmlStr = "";
        if(xslFile != null) {
            htmlStr = transformOnJaxp(reader, xslFile);
        } else {
            throw new CustomException("Not Found XSL File\n" + xslPath);
        }
        return htmlStr;
    }

    protected String transformOnJaxp(Reader xmlReader, File xsltFile) throws Exception {
        StringWriter sw = new StringWriter();
        /*
        javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(xmlReader);
        javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(xsltFile);
        javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(sw);
        javax.xml.transform.TransformerFactory transFact = javax.xml.transform.TransformerFactory.newInstance();
        javax.xml.transform.Transformer trans = transFact.newTransformer(xsltSource);
        trans.transform(xmlSource, result);
        */
        return sw.toString();
    }
}
