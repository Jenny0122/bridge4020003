/*
 * @(#)JobInit.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.job;

import kr.co.wisenut.common.Exception.BridgeException;
import kr.co.wisenut.common.Exception.DBFactoryException;
import kr.co.wisenut.bridge3.config.Config;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.SCDManager;
import kr.co.wisenut.common.util.JSONManager;
import kr.co.wisenut.common.util.io.IOUtil;

import java.sql.SQLException;
import java.io.File;

/**
 *
 * JobInit
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class JobInit extends Job{
    public JobInit(Config config, int mode) throws DBFactoryException {
        super(config, mode);

        File indexDir ;
        if(config.getArgs().isUserIndexDir() ){
            indexDir = new File(m_source.getScdDir().getPath(), "index");
        } else if(mode == IJob.STATIC ) {
            indexDir = new File(m_source.getScdDir().getPath(), "static");
        } else if(mode == IJob.DYNAMIC || mode == IJob.REPLACE) {
            indexDir = new File(m_source.getScdDir().getPath(), "dynamic");
        } else {
            indexDir = new File(m_source.getScdDir().getPath());
        }
        // set default for json
        this.fileManager = new JSONManager(indexDir, m_source.getScdCharSet(), m_source.getScdDir().getIdx());

        if (m_source.getExtension().equalsIgnoreCase("scd")) {
            this.fileManager = new SCDManager(indexDir, m_source.getScdCharSet(), m_source.getScdDir().getIdx());
        }
    }

    public boolean run() throws BridgeException, DBFactoryException {
        // Delete SCD File
        log("[info] [Job Init] [Delete SCD File]");
        if( fileManager.delete() ) {
            debug("[Job Init] [Successful]");
        } else {
            debug("[Job Init] [Failure]");
        }

        String[] sqlInitExec = m_query.getInitExecute();
        m_dbjob.setTargetConnection();
        if(sqlInitExec != null) {
            for(int i=0; i<sqlInitExec.length; i++) {
                log("[info] [JobInit] [Query Execute]");
                try {
                    int count = m_dbjob.execPQuery(sqlInitExec[i]);
                    debug("[info] [ Successful ("+count+") ]", 3);
                } catch (SQLException e) {
                    Log2.error("[JobInit] [execute error "+e.getMessage()+" ]");
                    Log2.error("[JobInit] [execute error query "+sqlInitExec[i]+" ]");
                }
               log("[info] [JobInit] ["+(i+1)+"th query execute successful]");
            }
        }
        return true;
    }
    
    public boolean runScdFilter() throws BridgeException { 
    	return true;
    }
}
