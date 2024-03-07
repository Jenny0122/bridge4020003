/*
 * @(#)JobFactory.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.job;

import kr.co.wisenut.bridge3.config.Config;
import kr.co.wisenut.common.Exception.DBFactoryException;

/**
 *
 * JobFactory
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class JobFactory {
    private static IJob m_job;

    public static IJob getInstance(Config config, int mode) throws DBFactoryException {
        if(mode == IJob.INIT){
            m_job = new JobInit(config, IJob.INIT);
        } else if(mode == IJob.STATIC) {
            m_job = new JobStcDyn(config, IJob.STATIC);
        } else if(mode == IJob.DYNAMIC) {
            m_job = new JobStcDyn(config, IJob.DYNAMIC);
        } else if(mode == IJob.DELETE) {
            m_job = new JobInit(config, IJob.DELETE);
       /* }else if(mode == IJob.INITSTATIC) {
            m_job = new JobStcDyn(config, IJob.INITSTATIC);*/
        } else if(mode == IJob.TEST) {
            m_job = new JobStcDyn(config, IJob.TEST);
        } else if(mode == IJob.REPLACE) {         // ADD 2006/01/10
            m_job = new JobStcDyn(config, IJob.REPLACE);
        }

        return m_job;
    }
}
