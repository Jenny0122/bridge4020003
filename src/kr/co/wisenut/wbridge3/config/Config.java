/*
 * @(#)Config.java   3.8.1 2009/03/11
 *
 */

package kr.co.wisenut.wbridge3.config;

import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.wbridge3.config.catalogInfo.Mapping;
import kr.co.wisenut.wbridge3.config.source.Source;

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

    /**
     * Source ID Return Function
     * @return  source id
     */
    public String getSrcid(){
        return m_args.getSrcid();
    }

    /**
     * Mapping Class Set Function
     * @param mapping Mapping object
     */
    public void setCollection(Mapping mapping){
        m_mapping = mapping;
    }

    /**
     * Mapping Class Return Function
     * @return Mapping
     */
    public Mapping getCollection(){
        return m_mapping;
    }

    /**
     * Source  Class Return Function
     * @return source
     */
    public Source getSource() {
        return source;
    }

    /**
     * Source  Class Set Function
     * @param source source object
     */
    public void setSource(Source source) {
        this.source = source;
    }

    /**
     * RunTimeArgs  Class Return Function
     * @return m_args
     */
    public RunTimeArgs getArgs() {
        return m_args;
    }

    /**
     * RunTimeArgs  Class Set Function
     * @param args input argument values
     */
    public void setArgs(RunTimeArgs args) {
        this.m_args = args;
    }

    /**
     *  WcseHome Return Function
     * @return wcse_home

    public String getWcseHome() {
        return m_args.getWcseHome();
    }
     */

    /**
     * View Mapping info Function
     * @throws ConfigException error
     */
    public void viewInfo() throws ConfigException {
        m_mapping.viewInfo();
    }

    /**
     * Debug Message Function
     * @param msg debug message
     */
    protected void debug(String msg){
        System.out.println(msg);
    }
}
