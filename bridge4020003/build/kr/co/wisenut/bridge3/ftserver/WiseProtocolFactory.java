/*
 * @(#)WiseProtocolFactory.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.ftserver;
import kr.co.wisenut.common.socket.ProtocolFactory;

import java.net.*;

/**
 *
 * WiseProtocolFactory
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class WiseProtocolFactory implements ProtocolFactory {
    /**
     * 
     * @param clntSock
     * @return
     */
    public Runnable createProtocol(Socket clntSock) {
        return new WiseProtocol(clntSock);
    }
}
