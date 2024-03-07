/*
 * @(#)Config.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3.config;

import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.fbridge3.config.catalogInfo.Mapping;
import kr.co.wisenut.fbridge3.config.source.Source;

/**
 *
 * Config
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class Config {
    private Mapping m_mapping = null;
    private Source source = null;
    private RunTimeArgs m_args;


   public String getSrcid(){
        return m_args.getSrcid();
    }

    public void setCollection(Mapping mapping){
        m_mapping = mapping;
    }

    public Mapping getCollection(){
        return m_mapping; 
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public RunTimeArgs getArgs() {
        return m_args;
    }

    public void setArgs(RunTimeArgs args) {
        this.m_args = args;
    }
}
