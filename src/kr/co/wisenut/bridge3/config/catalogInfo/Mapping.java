/*
 * @(#)Mapping.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.config.catalogInfo;

import java.util.Iterator;
import java.util.LinkedHashMap;

import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.bridge3.config.source.MemorySelect;
import kr.co.wisenut.bridge3.config.source.SubQuery;
import kr.co.wisenut.bridge3.config.source.node.XmlQuery;
import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.common.util.FormatColumn;
import kr.co.wisenut.common.util.io.IOUtil;

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

    /**
     * Catalog Info Mapping Field Set Function
     * @param catalog InfoSet[]
     */
    public void setCatalog(InfoSet[] catalog){
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
        String line = "\n--------------------------------------------------------------------------\n";
        String msg = line;
        msg += sutil.formatColumn("colnum", 8);
        msg += sutil.formatColumn("tagname", 18);
        msg += sutil.formatColumn("html", 12);
        msg += sutil.formatColumn("alias", 12);
        msg += sutil.formatColumn("Filter", 8);
        msg += sutil.formatColumn("SubQuery", 10);
        msg += line;
        for(int i=0;i<size;i++){
            msg += sutil.formatColumn("  "+Integer.toString(m_catalog[i].getCollumn()), 8);
            msg += sutil.formatColumn(m_catalog[i].getTagName(), 18);
            msg += sutil.formatColumn("    "+getYN(m_catalog[i].isHtmlParse()), 12);
            msg += sutil.formatColumn("    "+getYN(m_catalog[i].isSourceAlias()), 12);
            if(m_catalog[i].isHtmlParse() && m_catalog[i].isSourceAlias()) {
                throw new ConfigException(": Missing <CatalogInfo> - <Mapping/> setting in configuration file."
                        + "Don't use alias and html in same column. (column order="+i+")");
            }
            if(m_catalog[i].getFilter() != null && m_catalog[i].getSubQuery() != null){
                throw new ConfigException(":Missing <CatalogInfo> - <Mapping/> setting in configuration file." +
                        "Don't use SubQuery and Filter in same column. (column order="+i+")");
            }
            if(m_catalog[i].getFilter() != null){
                msg += sutil.formatColumn(m_catalog[i].getFilter().getFilterType(), 8);
            } else {
                msg += sutil.formatColumn(" .", 8);
            }
            if(m_catalog[i].getSubQuery() != null){
                msg += sutil.formatColumn(m_catalog[i].getSubQuery().getType(), 10);
            } else {
                msg += sutil.formatColumn(" .", 10);
            }
            if( !m_catalog[i].getClassName().equals("0x00")) {
                customClass += ">> column=\""+i+"\" className=" + m_catalog[i].getClassName() + "\n";
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

    private String getYN(boolean bool) {
        String ret = "n";
        if(bool) ret = "y";
        return ret;
    }
    private void debug(String msg){
        System.out.println(msg);
    }

    public void setSubQuery(SubQuery[] subQuery) throws ConfigException {
        if(subQuery != null) {
            for(int i=0; i<subQuery.length; i++){
                try{
                    m_catalog[subQuery[i].getOrder()].setSubQuery(subQuery[i]);
                }catch(Exception e){
                    throw new ConfigException(": Missing <SubQuery/> - <Query order=\""+subQuery[i].getOrder()+"\" />" +
                            "setting in configuration file."
                             +"\n"+IOUtil.StackTraceToString(e)+"\n]");
                }
            }
        }
    }
    
    public void setXmlQuery(XmlQuery[] xmlQuery) throws ConfigException {
    	if(xmlQuery != null) {
    		for(int idx=0; idx<xmlQuery.length; idx++) {
    			try {
    				m_catalog[xmlQuery[idx].getOrder()].setXmlQuery(xmlQuery[idx]);
    			} catch(Exception e) {
    				throw new ConfigException(": Missing <XmlQuery/> - <Xml order=\""+xmlQuery[idx].getOrder()+"\" /> "
    						+ "setting in configuration file."
    						+ "\n" + IOUtil.StackTraceToString(e)+"\n]");
    			}
    		}
    	}
    }

    public void setFilter(FilterSource[] filter) throws ConfigException {
        if(filter != null) {
            for(int i=0; i<filter.length; i++){
                try {
                    m_catalog[filter[i].getOrder()].setFilter(filter[i]);
                } catch(Exception e) {
                    throw new ConfigException(": Missing <FilterInfo/> - <Filter order=\""+filter[i].getOrder()+"\" />" +
                            "setting in configuration file."
                            +"\n"+IOUtil.StackTraceToString(e)+"\n]");
                }
            }
        }
    }
    
    /**
     * 	MemorySelect 데이터를 가져와 InfoSet column 에 맞게 set 한다.
     * @param subMemory
     * @throws ConfigException
     */
	public void setMemorySelect(LinkedHashMap memorySelectMap) throws ConfigException {
		
		Iterator it = memorySelectMap.keySet().iterator();
		while (it.hasNext()) {
			String orderId = (String) it.next();
			MemorySelect memorySelect = (MemorySelect) memorySelectMap.get(orderId);
			m_catalog[Integer.parseInt(orderId)].setMemorySelect(memorySelect);

		}

	}
}