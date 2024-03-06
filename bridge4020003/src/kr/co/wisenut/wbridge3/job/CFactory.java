/*
 * @(#)CFactory.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.job;

import kr.co.wisenut.wbridge3.job.custom.ICustom;
import kr.co.wisenut.common.Exception.BridgeException;
/**
 *
 * CFactory
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class CFactory {
    /**
     *  Custom Class Loading
     * @param className custom class name
     * @return  ICustom
     * @throws BridgeException error
     */
    public static ICustom getInstance(String className) throws BridgeException {
        try {
            return (ICustom) ClassLoader.getSystemClassLoader().loadClass(className).newInstance();
        } catch (InstantiationException e) {
            throw new BridgeException(e);
        } catch (IllegalAccessException e) {
            throw new BridgeException(e);
        } catch (ClassNotFoundException e) {
            throw new BridgeException(e);
        }
    }
}
