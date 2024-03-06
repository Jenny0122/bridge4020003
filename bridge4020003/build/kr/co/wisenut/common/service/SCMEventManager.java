/*
 * @(#)SCMEventManager.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.service;

import java.util.Vector;

/**
 *
 * SCMEventManager
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class SCMEventManager extends Vector {
    private static SCMEventManager m_event = null;

    private SCMEventManager() {
        super();
    }

    public static SCMEventManager getInstance() {
        if(null == m_event){
            m_event = new SCMEventManager();
        }
        return m_event;
    }

    public void dispatchSCMEvent(int eventID) {
        SCMEvent event = new SCMEvent(eventID);
        for(int i=0; i < size(); i++){
            Object obj = get(i);
            if (null == obj) {
                continue;
            }
            ((SCMEventListener)obj).handleSCMEvent(event);
        }
    }

    public void addSCMEventListener(SCMEventListener l) {
        if (!contains(l)) {
            add(l);
        }
    }

    public void removeSCMEventListener(SCMEventListener l) {
        remove(l);
    }
}