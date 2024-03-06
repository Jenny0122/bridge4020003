/*
 * @(#)GetFtServer.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.config.source;

import kr.co.wisenut.common.util.XmlUtil;
import kr.co.wisenut.common.Exception.ConfigException;

import java.util.HashMap;
import java.util.List;

import org.jdom.Element;

/**
 *
 * GetFtServer
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class GetFtServer extends XmlUtil{
    private Element rootElement;

    public GetFtServer(String path) throws ConfigException {
        super(path);
        rootElement = getRootElement();
    }

    public HashMap getFtServer() throws ConfigException {
        List lds = null;
        if(rootElement.getChild("FtServer") != null){
            lds = rootElement.getChild("FtServer").getChildren("server");
        } else {
            return null;
        }

        int size = lds.size();
        HashMap ftServerMap = new HashMap(size);
        String[] duplecate = new String[size];
        String id = "";
        FtServerConfig ftConf = null;
        for(int i=0; i<size;i++){
            ftConf = new FtServerConfig();
            Element element = (Element) lds.get(i);
            //List db = element.getChildren();
            //debug(Integer.toString(db.size()));

            id = element.getChildText("Id");

            // DataSource ID 以묐났 泥댄겕
            for(int j=0; j<size;j++){
                //debug(duplecate[j]);
                if(duplecate[j] != null && duplecate[j].equals(id)){
                    throw new ConfigException(":Duplicated FtServer ID - " +
                            "Please check the <FtServer> - <server> in configuration file.");
                }
            }
            duplecate[i] = id;
            ftConf.setId(id);
            String svr_port = getElementChildText(element, "PortNumber");
            if(svr_port != null && !svr_port.equals("") ) {
                try{
                    ftConf.setPortNumber(Integer.parseInt(getElementChildText(element, "PortNumber")));
                }catch(NumberFormatException ne) {
                    System.out.println(ne);
                }
            }
            List slist=element.getChildren("ServerName");

            int num = slist.size();
            String[] serverList = new String[num];
            for(int n=0; n < num; n++){
                Element eleServer = (Element)slist.get(n);
                serverList[n] =eleServer.getText();
            }
            ftConf.setServerName(serverList);
            ftServerMap.put(id, ftConf);
        }
        return ftServerMap;
    }
}
