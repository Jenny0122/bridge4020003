/*
 * @(#)SCMEvent.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.service;

/**
 *
 * SCMEvent
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class SCMEvent {
    // Corresponds to value in WINSVC.H
    public static final int SERVICE_STOPPED = 1;
    
    private int id;

    public SCMEvent(int code) {
        id = code;
    }

    public int getID() {
        return id;
    }
}