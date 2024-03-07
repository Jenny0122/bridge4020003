/*
 * @(#)GetSource.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3.config.source;

import org.jdom.Element;

import java.util.HashMap;
import java.util.List;

import kr.co.wisenut.fbridge3.config.source.node.SCDdir;
import kr.co.wisenut.fbridge3.config.source.RemoteInfo;
import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.fbridge3.config.catalogInfo.Mapping;
import kr.co.wisenut.fbridge3.config.catalogInfo.GetCatalogInfo;
import kr.co.wisenut.common.util.XmlUtil;
import kr.co.wisenut.common.logger.Log2;

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
     * Setting InfoSet
     * @return Mapping
     * @throws ConfigException TEXT
     */
    public Mapping getCollection() throws ConfigException {
        return new GetCatalogInfo(element).getCollection();
    }

    public Source getSource() throws ConfigException {
        boolean isCharSet = false;
        Source source = new Source();
        if(element == null){
            throw new ConfigException(": Missing " +
                    "<Source id="+srcid+"> setting in configuration file." +
                    "Could not parse Source Config.");
        }
        //Setting sourcealias.
        source.setSourceaias(getElementValue(element, "sourcealias", ""));

        // setting file extension
        if (element.getAttribute("extension") != null) {
            String extension = element.getAttribute("extension").getValue();
            if (!extension.equalsIgnoreCase("scd") && !extension.equalsIgnoreCase("json")) {
                throw new ConfigException("Please Check Element extension. extension value allow scd and json");
            }
            source.setExtension(extension);
        }

        //Setting SCD charset.Add 2005.01.20
        if(element.getAttribute("charset") != null) {
            source.setScdCharSet(element.getAttribute("charset").getValue());
            Log2.out("SCD File Encoding: " + element.getAttribute("charset").getValue());
            isCharSet = true;
        }

        // <Date format="none_time_t" dbformat="none" order="1"/>
        if(element.getChild("Date") != null) { //missing date node
            int dateOrder ;
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


        if(element.getChild("History") != null) {
            source.setHistoryfile( getElementValue(element.getChild("History"), "path") );
        } else {
            throw new ConfigException(": Missing " +
                    "<History path=\"History File Path\"/> setting in configuration file." +
                    "Could not parse History Config.");
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
        
        if(element.getChild("CustomFilter") != null) {
        	source.setCustomFilter( getElementValue(element.getChild("CustomFilter"), "className") );
        } else {
        	source.setCustomFilter("");
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
            if( !isCharSet ) {      // <Source charset="UTF-8" />
                source.setScdCharSet( getElementValue(element.getChild("RefCBFS"), "charSet", "UTF-8") );
            }
        }else if(element.getChild("RefColl") != null) {
            source.setRefCBFS( getElementValue(element.getChild("RefColl"), "id", "") );
            source.setSrcPrefix( getElementValue(element.getChild("RefColl"), "prefix", "") );
            if( !isCharSet ) {      // <Source charset="UTF-8" /> 
                source.setScdCharSet( getElementValue(element.getChild("RefColl"), "charSet", "UTF-8") );
            }
        }

        // <TargetFile>
        source.setTargetFileInfo(getTargetFileConf(element));
        source.setDirAcceptRuleInfo(getAcceptRuleInfo(element,  "DirAcceptRule"));
        source.setFileAcceptRuleInfo(getAcceptRuleInfo(element, "FileAcceptRule"));
        source.setRemoteinfo(getRemoteConf(element) );

        return source;
    }

    private RemoteInfo[] getRemoteConf(Element source) throws ConfigException {
		RemoteInfo[] remoteinfos = null;
		RemoteInfo remoteinfo;
		List remote;

		if (source != null) {
			remote = source.getChildren("Remote");
		} else {
			return null;
		}

		if (remote != null) {
			int size = remote.size();
			remoteinfos = new RemoteInfo[size];
			
			for (int i = 0; i < size; i++) {
				remoteinfo = new RemoteInfo();
				
				String ip = getElementValue(((Element) remote.get(i)), "ip", "");
				int port = Integer.parseInt(getElementValue(((Element) remote.get(i)), "port", ""));
				String deleteSCD = getElementValue(((Element) remote.get(i)), "deleteSCD", "n");
				remoteinfo.setIp(ip);
				remoteinfo.setPort(port);
				if (deleteSCD.equalsIgnoreCase("y")) {
					remoteinfo.setDeleteSCD(true);
				}
				
				remoteinfos[i] = remoteinfo;
			}
		}

		return remoteinfos;
	}

    private TargetFileInfo getTargetFileConf(Element source) throws ConfigException {
        TargetFileInfo targetFile ;
        List targets ;

        if(source != null) {
            targetFile = new TargetFileInfo();
            targets = source.getChildren("TargetFile");
        }else {
            return null;
        }

        if(targets != null) {
            int size = targets.size();
            for (int i = 0; i < size; i++) {
                String path = getElementValue(((Element)targets.get(i)),"path");
                String depth = getElementValue(((Element)targets.get(i)), "depth");
                String incDir = getElementValue(((Element)targets.get(i)), "dir");

                int dep ;
                try {
                    dep = Integer.parseInt(depth);
                } catch (Exception e) {
                    throw new ConfigException(": Missing <TargetFile depth=\"\"> setting in configuration file.", e);
                }

                boolean inc = incDir.equals("include");
                targetFile.addTarget(path, dep, inc);
            }
        }

        return targetFile;
    }

    private AcceptRuleInfo getAcceptRuleInfo(Element source, String ruleType) throws ConfigException {
        Element rules ;
        List rule ;
        AcceptRuleInfo acceptInfo ;

        if(source != null) {
            acceptInfo = new AcceptRuleInfo();
            rules = source.getChild(ruleType);
            rule = rules.getChildren();
        }else {
            return null;
        }

        if(rule != null) {
            for (int i = 0; i < rule.size(); i++) {
                Element r = (Element) rule.get(i);
                String el = r.getName();
                String attr = getElementValue(r, "rule");

                if (el.equalsIgnoreCase("Allow")) {
                    acceptInfo.addRule(AcceptRule.MATCH_ALLOW, attr);
                    Log2.debug("[XmlConfig] [Allow rule added. " + attr+"]", 3);
                }
                else if (el.equalsIgnoreCase("Deny")) {
                    acceptInfo.addRule(AcceptRule.MATCH_DENY, attr);
                    Log2.debug("[XmlConfig] [Deny  rule added. " + attr+"]", 3);
                }
            }
        }
        return acceptInfo;
    }

}
