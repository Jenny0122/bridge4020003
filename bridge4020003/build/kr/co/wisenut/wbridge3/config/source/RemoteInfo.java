/*
 * @(#)RemoteInfo.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.config.source;

/**
 *
 * RemoteInfo
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 * scd �쟾�넚�븯湲� �쐞�븳 <Remote> �끂�뱶 �젙蹂�
 */
public class RemoteInfo {
    private String ip ;
    private int  port ;
    private boolean isDeleteSCD = false;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isDeleteSCD() {
        return isDeleteSCD;
    }

    public void setDeleteSCD(boolean deleteSCD) {
        isDeleteSCD = deleteSCD;
    }
}
