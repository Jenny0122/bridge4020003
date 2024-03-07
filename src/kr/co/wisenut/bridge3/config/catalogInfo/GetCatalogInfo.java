/*
 * @(#)GetCatalogInfo.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.config.catalogInfo;

import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.StringUtil;
import org.jdom.Element;

import java.util.Iterator;
import java.util.List;

/**
 *
 * GetCatalogInfo
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class GetCatalogInfo {
    private Element element = null;
    /**
     * GetCatalogInfo constructor
     * @param element xml
     */
    public GetCatalogInfo(Element element) {
        this.element = element;
    }
    /**
     * Get Mapping node infomation method
     * @return Mapping object
     * @throws ConfigException error info
     */
    public Mapping getCatalogInfo() throws ConfigException {
        Log2.debug("[GetCatalogInfo] [Read CatalogInfo]", 4);
        InfoSet[] catalog = null;
        Mapping mapping = new Mapping();
        if(element == null){
            throw new ConfigException(": Missing <Source> - <CatalogInfo> setting in configuration file.");
        }

        List list = element.getChildren("CatalogInfo");
        Iterator irc = list.iterator();
        while (irc.hasNext()) {
            Element elementCatalog = (Element) irc.next();
            List initmapping = elementCatalog.getChildren("Mapping");

            int size = initmapping.size();
            catalog = new InfoSet[size];
            for (int k = 0; k < size; k++) {
                catalog[k] = new InfoSet();
                Element nextmpping = (Element) initmapping.get(k);
                if (getElementValue(nextmpping, "colnum").equals(Integer.toString(k))) {
                    catalog[k].setCollumn(k);
                } else {
                    throw new ConfigException(": Missing Mapping Colunnum: "
                            + nextmpping.getAttribute("colnum").getValue() + " != "+ k+" setting in configuration file.");
                }

                if(getElementValue(nextmpping, "colnum").equals("")){
                    throw new ConfigException(": Missing <Mapping> - <tagname> setting in configuration file.");
                }
                catalog[k].setTagName(getElementValue(nextmpping, "tagname"));
                catalog[k].setSourceAlias(getElementValue(nextmpping, "alias", "n"));
                catalog[k].setHtmlParse(getElementValue(nextmpping, "html", "n"));
                catalog[k].setClassName(getElementValue(nextmpping, "className", "0x00"));
                catalog[k].setType(getElementValue(nextmpping, "type", ""));       //Add 2005.01.20
                catalog[k].setMaxLen(StringUtil.parseInt(getElementValue(nextmpping, "maxLen", "0"), 0));//Add 2005.01.20
                catalog[k].setConvert(getElementValue(nextmpping, "convert", ""));
                catalog[k].setAppend(StringUtil.parseInt(getElementValue(nextmpping, "append", "1"), 1));
                catalog[k].setReplace(getElementValue(nextmpping, "replace", "n"));	//Add 2006/01/10
                catalog[k].setMd5(getElementValue(nextmpping, "md5", "n"));	//Add 2006/01/10
                catalog[k].setSCDFilter(getElementValue(nextmpping, "scdfilter", "n"));
                catalog[k].setRefnum(StringUtil.parseInt(getElementValue(nextmpping, "refnum", "-1"), -1));
            }
        }
        mapping.setCatalog(catalog);
        return mapping;
    }

    /**
     * Element의 Value Return Function
     * @param element Element
     * @param name lement name
     * @return element
     * @throws ConfigException error info
     */
    protected String getElementValue(Element element, String name) throws ConfigException {
        if(element.getAttribute(name) != null){
            return element.getAttribute(name).getValue().trim();
        } else {
            throw new ConfigException(": Missing <"+element.getName()+" /> "+name+" setting in configuration file.");
        }
    }

    /**
     * Element의 Value Return Function
     * @param element Element
     * @param name element name
     * @param def initialize value
     * @return  element xml
     * @throws ConfigException error info
     */
    protected String getElementValue(Element element, String name, String def) throws ConfigException {
        if(element.getAttribute(name) != null){
            return element.getAttribute(name).getValue().trim();
        } else {
            return def;
        }
    }

    /**
     * Element의 List Return Function
     * @param element Element
     * @param childName element name
     * @return  List
     */
    protected List getChildrenElementList(Element element, String childName){
        if(element != null && element.getChildren(childName) != null){
            return element.getChildren(childName);
        } else {
            return null;
        }
    }
}
