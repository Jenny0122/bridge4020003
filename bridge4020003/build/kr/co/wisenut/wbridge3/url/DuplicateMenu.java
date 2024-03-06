/*
 * @(#)DuplicateMenu.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.url;

import java.util.List;

/**
 *
 * DuplicateMenu
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class DuplicateMenu {
    /**
     * Crawl URL Cotent - extract title Function
     * @param str HTML Cotent
     * @param delmenu menu name
     * @return  title
     */
    public String getTitle(String str, String delmenu) {
        int idx = 0;
        String retStr = "";
        //CrawlJob curl = new CrawlJob();
        if ( (idx = str.indexOf(delmenu)) != -1) {
            retStr = str.substring(idx + delmenu.length());
            if ( retStr.length() > 40 )
                return retStr.substring(0,40);
            else
                return retStr;
        } else {
            if ( str.length() > 40 )
                return str.substring(0, 40);
            else
                return str;
        }
    }
    /**
     * top string delete function
     * @param str HTML Cotent
     * @param delmenu menu name
     * @return  menu
     */
    public String deleteMenu(String str, List delmenu) {
        int idx = 0;

        if ( delmenu.size() < 1) return str;
        for(int i=0; i < delmenu.size(); i++){
            if ( (idx = str.indexOf(delmenu.get(i).toString())) >-1 && !delmenu.get(i).toString().equals("")) {
                str = str.substring(idx+delmenu.get(i).toString().length(), str.length());
            }
        }

        return str;
    }
    /**
     * bottom string delete function
     * @param str HTML Cotent
     * @param delmenu menu name
     * @return menu
     */
    public String deleteTailMenu(String str, List delmenu) {
        int idx = 0;
        if ( delmenu.size() < 1) return str;
        for(int i=0; i < delmenu.size(); i++){
            if ( (idx = str.indexOf(delmenu.get(i).toString())) >-1 && !delmenu.get(i).toString().equals("")) {
                str = str.substring(0, idx);
                return str;
            }else {
                return str;
            }
        }
        return str;
    }

    /**
     * Tag delete Function
     * @param contStr HTML Cotent
     * @param startTag tag name
     * @param endTag  tag name
     * @return  result string
     */
    public String deleteTag(String contStr, String startTag, String endTag){
        int beginIndex = 0;
        int aa = 0;
        int bb = 0;
        String retStr = "";

        while ( true ) {
            aa = (contStr.toLowerCase()).indexOf(startTag, beginIndex);
            bb = (contStr.toLowerCase()).indexOf(endTag, aa + startTag.length());

            if ( aa == -1 || bb == -1 ) {
                if ( beginIndex == 0 )   {
                    retStr = contStr;
                    break;
                }
                else {
                    retStr = retStr + contStr.substring(beginIndex);
                    break;
                }
            }

            if ( aa != 0 )
                retStr = retStr + contStr.substring(beginIndex, aa);

            beginIndex = bb + endTag.length();
        }

        return retStr;
    }
    /**
     * HTML Content Top Menu Skip Function
     * @param contStr  HTML Cotent
     * @param startTag  tag name
     * @param endTag tag name
     * @return result string
     */
    public String skipTopMenu(String contStr, String startTag, String endTag){
        int beginIndex = 0;
        int aa = 0;
        int bb = 0;
        String retStr = "";

        aa = contStr.indexOf(startTag, beginIndex);
        bb = contStr.indexOf(endTag, aa + startTag.length());

        if ( aa != -1 && bb != -1)
            retStr = contStr.substring(bb + 30);
        else
            retStr = contStr;

        return retStr;
    }
}
