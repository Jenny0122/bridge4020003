/*
 * @(#)SimpleTagParser.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.util;

import kr.co.wisenut.common.logger.Log2;

import java.io.*;

/**
 *
 * SimpleTagParser
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class SimpleTagParser {
    public static String getTagParse(String sHtmltext) {
        String sParsingText = "";
        int slocation = 0;
        int gap = 0;

        for (int i = 0; i < sHtmltext.length(); i++) {
            if (sHtmltext.charAt(i) == '<') {
                for (int j = i; j < sHtmltext.length(); j++) {
                    if (sHtmltext.charAt(j) == '>') {
                        String sTag = sHtmltext.substring(i, j);
                        if (sTag.startsWith("<!--")) {
                            if (!sTag.endsWith("-->")) {
                                for (int k = i; k < sHtmltext.length() - 2; k++) {
                                    if (sHtmltext.substring(k, k + 3).equals("-->")) {
                                        j = k + 2;
                                        sTag = sHtmltext.substring(i, k + 3);
                                        break;
                                    }
                                }
                            }
                        }

                        if (slocation < i) {
                            gap = 1;
                        } else {
                            gap = 0;
                        }
                        sParsingText += sHtmltext.substring(slocation + gap, i);
                        i = j;

                        slocation = i;
                        break;
                    }
                }
            }
        }

        if (slocation == 0) {
            sParsingText = sHtmltext.toString();
        }

        return sParsingText;
    }

    /**
     * HTML Parsing Function
     * @param htmlData
     * @return Parsed Html Data
     */
    public static String htmlParser(String htmlData) {
        InputStream r;
        OutputStream o;

        r = new ByteArrayInputStream(htmlData.getBytes());
        o = new ByteArrayOutputStream();

        String str = "";

        int ch_flag = 1;
        int function_flag = 1;
        int space_flag = 1;

        int ch = 0;
        try {
            while ( (ch = r.read()) != -1 ) {
                if ( ch == '<' || ch == '/') {
                    ch_flag = 0;
                    continue;
                }
                else if ( ch == '>' ) {
                    ch_flag = 1;
                    continue;
                }
                else if ( ch == '{')  {
                    function_flag = 0;
                    continue;
                }
                else if ( ch == '}') {
                    function_flag =1;
                    continue;
                }
                else {
                    if ( ch == '\n' || ch == '\r' ) {
                        //o.write(32);
                        continue;
                    }

                    if ( ch_flag == 1 && function_flag == 1) {
                        if ( ch == 32  && space_flag == 1) {
                            o.write(ch);
                            space_flag = 0;
                            continue;
                        }
                        else if ( ch == 32 && space_flag == 0 )
                            continue;
                        else {
                            o.write(ch);
                            space_flag = 1;
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log2.error("[WiseParserUtil Exception] [" + e.getMessage()+ "]");
        }

        str = o.toString();

        try {
            r.close();
        } catch (IOException e) {
            Log2.error("[WiseParserUtil Exception] [" + e.getMessage()+ "]");
        }

        return str;
    }

}
