/*
 * @(#)Job.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.job;

import java.util.LinkedHashMap;
import kr.co.wisenut.common.Exception.BridgeException;
import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.common.Exception.DBFactoryException;
import kr.co.wisenut.common.Exception.FilterException;
import kr.co.wisenut.bridge3.config.Config;
import kr.co.wisenut.bridge3.config.catalogInfo.Mapping;
import kr.co.wisenut.bridge3.config.source.Source;
import kr.co.wisenut.bridge3.config.source.Query;
import kr.co.wisenut.bridge3.db.DBConnFactory;
import kr.co.wisenut.bridge3.db.QueryGenerator;
import kr.co.wisenut.bridge3.db.DBJob;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileManager;

/**
 *
 * Job
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public abstract class Job implements IJob{
    protected int m_mode = -1;
    protected Config m_config;
    protected IJob m_job;
    protected Source m_source;
    protected Query m_query;
    protected Mapping m_collection;
    protected FileManager fileManager;
    protected DBConnFactory m_dbFactory;
    protected QueryGenerator m_queryGen;
    protected Detector m_detector;
    protected DBJob m_dbjob;
    protected LinkedHashMap<String, String> data = new LinkedHashMap<>();

    public Job(Config config, int mode) throws DBFactoryException {
        m_config = config;
        m_mode = mode;
        m_job = this;
        m_source = config.getSource();
        m_query = m_source.getQuery();
        m_collection = config.getCollection();
        m_dbFactory = new DBConnFactory(m_config.getSf1Home(), m_config.getDataSource());
        m_dbjob = new DBJob(m_config, m_dbFactory.getDbmsType( m_source.getTargetDSN()), mode);
        m_queryGen = new QueryGenerator();
        if(m_source.getDetectPI() != null) m_detector = new Detector(m_source.getDetectPI().getRgxMap());
    }

    public IJob getInstance(){
        return m_job;
    }

    public String getState() {
        return null; 
    }

    public abstract boolean run() throws BridgeException, DBFactoryException, FilterException;

    //public abstract void destroy();

    public void stop() throws BridgeException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setConfig(Config config) throws ConfigException {
        m_config = config;
    }

    public Config getConfig() {
        return m_config;
    }

    public void log(String msg){
        Log2.out(msg);
    }

    public void log(String msg, int level){
        Log2.debug(msg, level);
    }

    public void log(Exception ex){
        Log2.error(ex);
    }

    public void error(String msg){
        Log2.error(msg);
    }

    public void error(Exception ex){
        Log2.error(ex);
    }

    public void debug(String msg, int level){
        Log2.debug(msg, level);
    }

    public void debug(String msg) {
        Log2.debug(msg, 4);
    }
}
