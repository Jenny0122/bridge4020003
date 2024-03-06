/*
 * @(#)TextResultSet.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.text;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.StringUtil;

import java.io.*;

/**
 *
 * TextResultSet
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class TextResultSet {
     private BufferedReader m_br;
    private boolean readAble;

    public TextResultSet(File file, String encoding)  throws IOException{
        readAble = true;
        if(encoding.equalsIgnoreCase("utf-8")) {
            m_br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        } else if(encoding!=null && encoding.length()>0) {
            m_br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
        } else {
            m_br = new BufferedReader(new FileReader(file));
        }
    }

    public boolean next() throws IOException{
        if(readAble && m_br.ready()){
            return true;
        } else{
            close();
            return false;
        }
    }

    public String getString()  throws IOException {
        return StringUtil.checkNull(m_br.readLine());
    }

    public String getString(String charset) throws IOException {
        return StringUtil.checkNull(m_br.readLine());
    }

    public void close()  throws IOException {
        if(m_br != null)
            m_br.close();
        readAble = false;
    }
}
