/*
 * @(#)FtServerConfig.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.config.source;

/**
 *
 * FtServerConfig
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class FtServerConfig {
    private String id;
    private int portNumber = -1;
    private String[] serverName;

    public String[] getServerName() {
        return serverName;
    }

    public void setServerName(String[] serverName) {
        this.serverName = serverName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }


}
