/*
 * @(#)Mapping.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.config.catalogInfo;

import kr.co.wisenut.common.util.FormatColumn;

import kr.co.wisenut.common.Exception.ConfigException;

/**
 *
 * Mapping field infomation view
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class Mapping {
    private InfoSet[] m_catalog = null;
    private boolean hasImageInfo = false;
    private boolean hasThumbnailInfo = false;
    
    public boolean hasThumbnailInfo() {
        return hasThumbnailInfo;
    }

    public void setHasThumbnailInfo(boolean hasThumbnailInfo) {
        this.hasThumbnailInfo = hasThumbnailInfo;
    }

    public boolean hasImageInfo() {
        return hasImageInfo;
    }

    public void setHasImageInfo(boolean hasImageInfo) {
        this.hasImageInfo = hasImageInfo;
    }

    /**
     * Catalog Info Mapping Field Set Function
     * @param catalog InfoSet[]
     */
    public void setCbfs(InfoSet[] catalog){
        m_catalog = catalog;
    }

    /**
     * Catalog Info Mapping Field Return Function
     * @return InfoSet Class Array
     */
    public InfoSet[] getCatalog(){
        return m_catalog;
    }

    /**
     *  Catalog Info Size Return Function
     * @return m_catalog.length
     */
    public int size(){
        if(m_catalog == null){
            return 0;
        } else {
            return m_catalog.length;
        }
    }

    /**
     *  Catalog Infomation function
     * @throws ConfigException error
     */
    public void viewInfo() throws ConfigException {
        FormatColumn sutil = new FormatColumn();
        int size = m_catalog.length;
        String customClass = "";
        String line = "\n--------------------------------------" +
                "------------------------------------\n";
        String msg = line;
        msg += sutil.formatColumn("colnum", 8);
        msg += sutil.formatColumn("tagname", 18);
        msg += sutil.formatColumn("column type", 12);
        msg += sutil.formatColumn("alias", 12);

        msg += line;
        for(int i=0;i<size;i++){
            msg += sutil.formatColumn("  "+Integer.toString(m_catalog[i].getCollumn()), 8);
            msg += sutil.formatColumn(m_catalog[i].getTagName(), 18);
            msg += sutil.formatColumn(m_catalog[i].getType(), 12);
            msg += sutil.formatColumn("  "+getYN(m_catalog[i].isSourceAlias()), 12);

            if( !m_catalog[i].getClassName().equals("0x00")) {
                customClass += ">> column=\""+i+"\" className="
                        + m_catalog[i].getClassName() + "\n";
            }
            if(i<size-1) {
                msg += "\n";
            }
        }
        debug(msg+line);
        if(!customClass.equals("")) {
            debug("[Mapping ] [ICustom Class Filed Info" + customClass+"]");
        }
    }

    /**
     * Get Y/N Function
     * @param bool true/false
     * @return y/n
     */
    private String getYN(boolean bool) {
        String ret = "n";
        if(bool) ret = "y";
        return ret;
    }

    /**
     *  Debug Message Function
     * @param msg debug message text
     */
    private void debug(String msg){
        System.out.println(msg);
    }
}