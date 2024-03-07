/*
 * @(#)ICustom.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.job.custom;

import kr.co.wisenut.common.Exception.CustomException;
import kr.co.wisenut.wbridge3.seed.SeedItem;

/**
 *
 * ICustom - CustomJS class interface
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public interface ICustom {
    /**
     *
     * @param item  seeditem
     * @param str input url
     * @return String
     * @throws CustomException error
     */
    public String customData(SeedItem item, String str) throws CustomException;

     public String customUserDefine(SeedItem item, String content, String userTagName) throws CustomException;
     
     public String customData(String str) throws CustomException;
}
