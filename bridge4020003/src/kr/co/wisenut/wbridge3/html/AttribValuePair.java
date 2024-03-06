/*
 * @(#)AttribValuePair.java   3.8.1 2009/03/11
 *
 */

package kr.co.wisenut.wbridge3.html;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

/**
 *
 * AttribValuePair
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class AttribValuePair {
    public void setIgnoreAttribCase(boolean ignore) {
        this.ignoreAttribCase=ignore;
    }

    public boolean getIgnoreAttribCase() {
        return ignoreAttribCase;
    }

    /**
     * empty constructor that does nothing
     */
    public AttribValuePair() {
    }


    /**
     * initializes object using an attribute and its values
     */
    public AttribValuePair(String attrib, String value) {
        this.attrib=attrib;
        this.value=value;
    }

    /**
     * inializes object using attrib=value string
     */
    public AttribValuePair(String attribAndValue) {
        setAttribAndValue(attribAndValue);
    }

    /**
     * set the attrib and value using an attrib=value string
     */
    protected void setAttribAndValue(String attribAndValue) {
        int pos=0;
        pos=attribAndValue.indexOf("=");
        if (pos==-1) {
            attrib=attribAndValue;
        } else {
            attrib=attribAndValue.substring(0,pos).trim();
            value=attribAndValue.substring(pos+1).trim();
            if (value.startsWith("\"") || value.startsWith("'")) {
                value=value.substring(1);
            }
            if (value.endsWith("\"") || value.endsWith("'")) {
                value=value.substring(0,value.length()-1);
            }
        }
    }

    public String getAttrib() {
        if (ignoreAttribCase) {
            return attrib.toLowerCase();
        } else {
            return attrib;
        }
    }

    public String getValue() {
        return value;
    }

    public String toEncodedString(String enc) {
        String toString = "";
        try {
            toString = URLEncoder.encode(attrib, enc) +
                    "="+
                    URLEncoder.encode(value, enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return toString ;
    }

    public String toString() {
        return attrib+"=\""+value+"\"";
    }

    private String attrib;
    private String value;
    private boolean ignoreAttribCase=false;
}
