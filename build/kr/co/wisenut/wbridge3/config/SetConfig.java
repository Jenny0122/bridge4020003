/*
 * @(#)SetConfig.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.config;

import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.wbridge3.config.catalogInfo.Mapping;
import kr.co.wisenut.wbridge3.config.source.GetFilterExt;
import kr.co.wisenut.wbridge3.config.source.GetSource;
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

    /**
     * Source Node Return Function
     * @param rta runtime argument
     * @return  m_config
     * @throws ConfigException error
     */
    public Config getConfig(RunTimeArgs rta) throws ConfigException {
        m_config.setArgs(rta);
        GetSource source = new GetSource(rta.getConf(), rta.getSrcid());

        m_config.setSource( source.getSource() );
        m_config.setCollection( source.getCollection());

        Mapping mapping = m_config.getCollection();

        mapping.viewInfo();

        // 2009.06.29 �븘�꽣留� 媛��뒫�븳 �솗�옣�옄瑜� �꽕�젙�뿉�꽌 �씫�뼱 留ㅽ븨�븳�떎
        new GetFilterExt(rta.getConf()).getFilterExtInfo();
        return m_config;
    }
}
