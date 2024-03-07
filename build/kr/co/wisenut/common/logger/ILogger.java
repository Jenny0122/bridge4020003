/*
 * @(#)ILogger.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.logger;
/**
 *
 * ILogger
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public interface ILogger {
	
	/**
	 * 濡쒓렇 �뙣�뒪媛� /log/bridge/yyyy/mm/yymmdd_info.log �삎�깭濡� �깮�꽦�븯�뒗 ���엯
	 * sf-1 5.0 �씠�쟾 踰꾩쟾怨� �룞�씪�븳 濡쒓렇 �삎�깭
	 */
	public static final String SF1_VERSION_TYPE_4 = "4";
	/**
	 * 濡쒓렇 �뙣�뒪媛� /log/bridge/brg-{source}.info.yyyymmdd.log.log �삎�깭濡� �깮�꽦�븯�뒗 ���엯
	 * sf-1 5.0 怨� �룞�씪�븳 濡쒓렇 �삎�깭
	 */	
	public static final String SF1_VERSION_TYPE_5 = "5";
	/**
	 * 濡쒓렇 �뙣�뒪媛� /log/bridge/brg-{source}.info.yyyymmdd.log.log �삎�깭濡� �깮�꽦�븯�뒗 ���엯
	 * sf-1 5.0 怨� �룞�씪�븳 濡쒓렇 �삎�깭
	 */	
	public static final String SF1_VERSION_TYPE_6 = "6";

    public static final String SF1_VERSION_TYPE_7 = "7";
	
    public static final int CRIT = Integer.MIN_VALUE;
    public static final int ERROR = 1;
    public static final int WARNING = 2;
    public static final int INFO = 3;
    public static final int DEBUG = 4;
    public static final String STDOUT = "SDTOUT";
    public static final String ERROUT = "ERROUT";
    public static final String DAILY = "DAILY";
    
    public void log(String message);

    public void log(Exception ex);

    public void log(String message, int verbosity);

    public void log(Exception exception, String msg);

    public void log(String message, Throwable throwable);

    public void log(String message, Throwable throwable, int verbosity);

    public void error(String message);

    public void error(Exception ex);

    public void verbose(String message);

    public void finalize();
}
