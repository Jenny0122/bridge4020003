/*
 * @(#)FilterFactory.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.filter;

import kr.co.wisenut.common.Exception.BridgeException;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.io.IOUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * FilterFactory
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class FilterFactory {
    //private ;

    public IFilter getInstance(String className, Boolean isFilterDel, String filteredTextDir) throws BridgeException {
        IFilter m_ifd ;
        if(className.equals("0x00") || className.equals("")) {
            m_ifd = new FilterData(isFilterDel);
        } else {
            try {
                //m_ifd = (IFilter) ClassLoader.getSystemClassLoader().loadClass(className).newInstance();
                Class customClass = ClassLoader.getSystemClassLoader().loadClass(className);
                Class[] params = new Class[] {isFilterDel.getClass()};
                Constructor c = customClass.getConstructor(params);
                m_ifd = (IFilter) c.newInstance(new Object[] { isFilterDel});

                
            } catch (InstantiationException e) {
                throw new BridgeException("FilterFactory Class, GetInstance, " +
                        "InstantiationException\n"+IOUtil.StackTraceToString(e)+"\n]");
            } catch (IllegalAccessException e) {
                throw new BridgeException("FilterFactory Class, GetInstance," +
                        " IllegalAccessException\n"+IOUtil.StackTraceToString(e)+"\n]");
            } catch (ClassNotFoundException e) {
                throw new BridgeException("FilterFactory Class, GetInstance, " +
                        "ClassNotFoundException\n"+IOUtil.StackTraceToString(e)+"\n]");
            } catch (NoSuchMethodException e) {
                throw new BridgeException("FilterFactory Class, GetInstance, " +
                        "NoSuchMethodException\n"+IOUtil.StackTraceToString(e)+"\n]");
            } catch (InvocationTargetException e) {
                throw new BridgeException("FilterFactory Class, GetInstance, " +
                        "InvocationTargetException\n"+IOUtil.StackTraceToString(e)+"\n]");
            }
        }
        
        // filtered text ���옣 �쐞移� �꽕�젙
        m_ifd.setFilteredTextDir(filteredTextDir);
        Log2.debug("[FilterFactory] [Filtered text directory : " + filteredTextDir + "]", 4);
        
        return m_ifd;
    }
}