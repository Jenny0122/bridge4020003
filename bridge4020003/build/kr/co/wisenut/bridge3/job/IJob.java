/*
 * @(#)IJob.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.job;

import kr.co.wisenut.common.Exception.BridgeException;
import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.common.Exception.DBFactoryException;
import kr.co.wisenut.common.Exception.FilterException;
import kr.co.wisenut.bridge3.config.Config;

/**
 *
 * IJob
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public interface IJob {
    public static final int INIT = 0;
    public static final int STATIC = 1;
    public static final int DYNAMIC = 2;
    //public static final int INITSTATIC = 4;
    public static final int TEST = 5;
    public static final int REPLACE = 6;
    
    public static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    //public static final int REPLACE = 4;


    public static final String STOPPED = "STOPPED";
    public static final String STARTING = "STARTING";
    public static final String RUNNING = "RUNNING";
    public static final String STOPPING = "STOPPING";

    public static final String BRIDGE_START_DATETIME = "bridge.start.datetime";
    public static final String BRIDGE_RUNNING_DATETIME = "bridge.last.datetime";
    public static final String BRIDGE_RUNNING_STATUS = "bridge.status.code";

    public IJob getInstance();

    public String getState();

    public boolean run() throws BridgeException, DBFactoryException, FilterException;

    public boolean runScdFilter() throws BridgeException, FilterException;
    
    public void stop() throws BridgeException;

    public void setConfig(Config config) throws ConfigException;

    public Config getConfig();

    public void log(String msg);

    public void log(String msg, int level);

    public void log(Exception ex);

    public void error(String msg);

    public void error(Exception ex);
}
