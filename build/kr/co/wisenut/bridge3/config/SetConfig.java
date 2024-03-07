/*
 * @(#)SetConfig.java   3.8.1 2009/03/11
 *
 */

package kr.co.wisenut.bridge3.config;

import kr.co.wisenut.bridge3.config.catalogInfo.Mapping;
import kr.co.wisenut.bridge3.config.datasource.GetDataSource;
import kr.co.wisenut.bridge3.config.source.GetFilterExt;
import kr.co.wisenut.bridge3.config.source.GetFtServer;
import kr.co.wisenut.bridge3.config.source.GetSource;
import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.common.Exception.DBFactoryException;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;

/**
 *
 * SetConfig
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class SetConfig {
    private Config m_config = new Config();
    private final String dataSourceFile = "DataSource.xml".toLowerCase() ;

    public Config getConfig(RunTimeArgs rta) throws ConfigException,DBFactoryException {
        m_config.setArgs(rta);
        GetSource source = new GetSource(rta.getConf(), rta.getSrcid());

        m_config.setSource( source.getSource() );
        m_config.setCollection(source.getCollection());
        m_config.setFtServerConfMap(new GetFtServer(rta.getConf()).getFtServer());
        
        String dataSourcePath = getDataSourcePath(rta);
        m_config.setDataSource(new GetDataSource(dataSourcePath).getDataSource());

        Mapping mapping = m_config.getCollection();
        mapping.setSubQuery(m_config.getSource().getSubQuery());
        mapping.setFilter(m_config.getSource().getFilterSource());
        mapping.setXmlQuery(m_config.getSource().getXmlQuery());
        
        if(m_config.getSource().getSubMemory() != null) {
        	mapping.setMemorySelect(m_config.getSource().getSubMemory().getMemorySelectMap());
        }

        mapping.viewInfo();

        // 2009.06.29 filtering file extension mapping
        new GetFilterExt(rta.getConf()).getFilterExtInfo();
        return m_config;
    }
    

    /**
     * datasource.xml �씠 �뾾怨� �씤�옄�뿉 datasource �뙆�씪�씠 �꽕�젙�맂 寃쎌슦 �꽕�젙�맂 �뙆�씪 �젙蹂대�� 諛섑솚�븳�떎.
     * @param rta
     * @return
     */
	private String getDataSourcePath(RunTimeArgs rta) {
		
		String dataSourcePath = rta.getSf1_home() + FileUtil.fileseperator + "config" + FileUtil.fileseperator
				+ dataSourceFile;
		
		if(rta.getDataSourcePath() != null) {
			dataSourcePath = rta.getDataSourcePath();
		}
		
		Log2.out("[info] [SetConfig][datasource xml path : " + dataSourcePath + "]");
		
		return dataSourcePath;

	}    
}
