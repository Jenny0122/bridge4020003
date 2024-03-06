/*
 * @(#)GetSource.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.config.source;

import java.util.HashMap;
import java.util.List;

import kr.co.wisenut.wbridge3.config.source.node.SCDdir;
import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.XmlUtil;
import kr.co.wisenut.wbridge3.config.catalogInfo.GetCatalogInfo;
import kr.co.wisenut.wbridge3.config.catalogInfo.Mapping;

import org.jdom.Element;

/**
 *
 * GetSource
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class GetSource extends XmlUtil{
    private String srcid = null;
    private Element element = null;

    /**
     * GetSource Class constructor
     * @param path  config file path
     * @param srcid  bridge source id
     * @throws ConfigException error
     */
    public GetSource(String path, String srcid) throws ConfigException {
        super(path);
        HashMap m_source_map = getElementHashMap("Source");
        this.srcid = srcid;
        element = (Element) m_source_map.get(srcid);
        if(element == null) {
           throw new ConfigException(": Missing <Source id=\""+srcid+"\"> " +
                    "setting in configuration file." );
        }
    }

   /**
     * GetCatalogInfo Return function
     * @return  Mapping Class
     * @throws ConfigException error
     */
    public Mapping getCollection() throws ConfigException {
        return new GetCatalogInfo(element).getCollection();
    }

    /**
     * Source Node Return Function
     * @return  source Class
     * @throws ConfigException error
     */
    public Source getSource() throws ConfigException {
        boolean isCharSet = false;
        Source source = new Source();
        if(element == null){
            throw new ConfigException(": Missing " +
                    "<Source id="+srcid+"> setting in configuration file." +
                    "Could not parse Source Config.");
        }
        // setting sourcealias
        source.setSourceaias(getElementValue(element, "sourcealias", ""));

        // setting file extension
        if (element.getAttribute("extension") != null) {
            String extension = element.getAttribute("extension").getValue();
            if (!extension.equalsIgnoreCase("scd") && !extension.equalsIgnoreCase("json")) {
                throw new ConfigException("Please Check Element extension. extension value allow scd and json");
            }
            source.setExtension(extension);
        }

        // setting SCD charset.add 2005.01.20
        if(element.getAttribute("charset") != null) {
            source.setScdCharSet(element.getAttribute("charset").getValue());
            Log2.out("SCD File Encoding: " + element.getAttribute("charset").getValue());
            isCharSet = true;
        }

        if(element.getAttribute("imgAltTag") != null) {
            if(element.getAttribute("imgAltTag").getValue().equalsIgnoreCase("y")) {
                source.setImgAltTag(true);
            }
            Log2.out("SCD File Encoding: " + element.getAttribute("imgAltTag").getValue());
        }

        // <Date format="none_time_t" dbformat="none" order="1"/>
        if(element.getChild("Date") != null) {
            int dateOrder = -1;
            try {
                dateOrder = Integer.parseInt(getElementValue(element.getChild("Date"), "order"));
            } catch(NumberFormatException ne) {
                 throw new ConfigException(": Missing " +
                        "<Date order=\"number\"/> setting in configuration file. " +
                        "Please specify the numbers 1 ~ 32. Could not parse Date Config.");
            }
            String dateFormat = getElementValue(element.getChild("Date"), "dbformat", "none");
            source.setDataFormat(dateOrder, dateFormat);
        }

        // <SCDdir path="E:\catalogInfo\mknews\scd\"/>
        if(element.getChild("SCDdir") != null) {
        	SCDdir scdDir = new SCDdir();
			String path = getElementValue(element.getChild("SCDdir"), "path");
			String sourceIdx = getElementValue(element.getChild("SCDdir"), "idx", "00");

			scdDir.setPath(path);

			if (Integer.parseInt(sourceIdx) < 100 && Integer.parseInt(sourceIdx) > -1) {
				scdDir.setIdx(sourceIdx);
			} else {
				throw new ConfigException("Please Check Attribute [idx] in SCDdir Node. idx's value allow between 00~99");
			}
			
			source.setScdDir(scdDir);
        } else {
            throw new ConfigException(": Missing " +
                    "<SCDdir path=\"SCD File Path\"/> setting in configuration file." +
                    "Could not parse SCDdir Config.");
        }
        
        if(element.getChild("RemoteSCDdir") != null) {
            source.setRemoteDir( getElementValue(element.getChild("RemoteSCDdir"), "path") );
        } else {
            source.setRemoteDir( getElementValue(element.getChild("SCDdir"), "path") );
        }

        // <RefCBFS collid="tech" prefix="#001" charset="UTF-8" />
        if(element.getChild("RefCBFS") != null) {
            source.setRefCBFS( getElementValue(element.getChild("RefCBFS"), "collid", "") );
            source.setSrcPrefix( getElementValue(element.getChild("RefCBFS"), "prefix", "") );
            if( !isCharSet ) {
                source.setScdCharSet( getElementValue(element.getChild("RefCBFS"), "charSet", "UTF-8") );
            }
        } else if(element.getChild("RefColl") != null) {
            source.setRefCBFS( getElementValue(element.getChild("RefColl"), "id", "") );
            source.setSrcPrefix( getElementValue(element.getChild("RefColl"), "prefix", "") );
            if( !isCharSet ) {
                source.setScdCharSet( getElementValue(element.getChild("RefColl"), "charSet", "UTF-8") );
            }
        }

        // <SeedUrlFile value="C:\sf-1\seedurl\mma.txt"/>
        if(element.getChild("SeedUrlFile") != null) {
            source.setSeedfile( getElementValue(element.getChild("SeedUrlFile"), "path") );
        } else {
            throw new ConfigException(": Missing <SeedUrlFile  path=\"SeedUrlFile\"/>" +
                    " setting in configuration file. Could not parse SeedUrlFile Config.");
        }


        if(element.getChild("DBFile") != null) {
            source.setDbFile( getElementValue(element.getChild("DBFile"), "path") );
        } else {
            throw new ConfigException(": Missing <DBFile  path=\"DBFile Path\"/> " +
                    "setting in configuration file. Could not parse DBFile Config.");
        }

        if(element.getChild("Thumbnail") != null) {
            int height = Integer.parseInt(getElementValue(element.getChild("Thumbnail"), "height"));
            source.setThumbnailHeight( height );
            int width = Integer.parseInt(getElementValue(element.getChild("Thumbnail"), "width"));
            source.setThumbnailWidth(width);
            String thumbnailPath = getElementValue(element.getChild("Thumbnail"), "path", "");
            String include = getElementValue(element.getChild("Thumbnail"), "include", "n");
            source.setThumbnailDir(thumbnailPath);
            source.setInclude(include);
        }
        
        if(element.getChild("FilterOption") != null) {
        	String fileOption = getElementValue(element.getChild("FilterOption"), "value", "");
        	source.setFilterOption(fileOption);
        }
        
        source.setRemoteinfo(getRemoteConf(element) );
        return source;
    }
    
    private RemoteInfo getRemoteConf(Element source) throws ConfigException {
        RemoteInfo  remoteinfo ;
        List remote ;

        if(source != null) {
            remoteinfo = new RemoteInfo();
            remote = source.getChildren("Remote");
        }else {
            return null;
        }

        if(remote != null) {
            int size = remote.size();
            for (int i = 0; i < size; i++) {
                String ip = getElementValue(((Element)remote.get(i)),"ip", "");
                int port = Integer.parseInt(getElementValue(((Element)remote.get(i)), "port", ""));
                String deleteSCD = getElementValue(((Element)remote.get(i)),"deleteSCD", "n");
                remoteinfo.setIp(ip);
                remoteinfo.setPort(port);
                if(deleteSCD.equalsIgnoreCase("y")) {
                    remoteinfo.setDeleteSCD(true);
                }
            }
        }

        return remoteinfo;
    }
}
