/*
 * @(#)JBridge.java 3.8.1 2009/03/11
 */
package kr.co.wisenut.bridge3;

import java.io.IOException;
import java.util.List;

import kr.co.wisenut.bridge3.config.Config;
import kr.co.wisenut.bridge3.config.RunTimeArgs;
import kr.co.wisenut.bridge3.config.SetConfig;
import kr.co.wisenut.bridge3.job.IJob;
import kr.co.wisenut.bridge3.job.JobFactory;
import kr.co.wisenut.common.Exception.BridgeException;
import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.common.Exception.DBFactoryException;
import kr.co.wisenut.common.Exception.FilterException;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.msg.BridgeInfoMsg;
import kr.co.wisenut.common.util.BridgeCommonUtil;
import kr.co.wisenut.common.util.ExistCodeConstants;
import kr.co.wisenut.common.util.PidUtil;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.io.IOUtil;

/**
 * 
 * JBridge
 * 
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 * 
 * @author WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 * 
 */
public class JBridge {

	public static void main(String[] args) {
		if (args.length == 0) {
			BridgeInfoMsg.header(0);
			BridgeInfoMsg.usage(0);
			System.exit(-1);
		}

		BridgeInfoMsg.header(0);
		String sf1_home = "";
		if (System.getProperty("wcse_home") != null) {
			sf1_home = System.getProperty("wcse_home");
		} else if (System.getProperty("sf1_home") != null) {
			sf1_home = System.getProperty("sf1_home");
		} else {
			BridgeInfoMsg.usageSF1_HOME();
			System.exit(-1);
		}

		RunTimeArgs rta = new RunTimeArgs();
		if (!rta.readargs(sf1_home, args)) {
			System.exit(-1);
		}
		// System Exit Code
		int exit_code = ExistCodeConstants.EXIST_CODE_NORMAL;

		// Create Log Object
		try {
			if (rta.getLogPath() != null) {
				Log2.setLogger(rta.getLogPath(), rta.getLog(), rta.isDebug(), rta.getLoglevel(),
						rta.isVerbose(), rta.getSrcid());
			} else {
				Log2.setBridgeLogger(sf1_home, rta.getLog(), rta.isDebug(), rta.getLoglevel(),
						rta.isVerbose(), rta.getSrcid());
			}
		} catch (Exception e) {
			System.out.println("[JBridge] [Set Logger fail. " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
			System.exit(-1);
		}

		// Create PidUtil Object
		List<String> argument_values = rta.getParamValue();
		String pidname = rta.getSrcid();
		if( argument_values != null) {
			for(int i=0;i<argument_values.size();i++) {
				pidname += "_" + argument_values.get(i);
			}
		}
		
		PidUtil pidUtil = new PidUtil(pidname, sf1_home);
		try {
			if (pidUtil.existsPidFile()) {
				Log2.error("[JBridge] [Crawling failed. Is already running in source.)]" + StringUtil.newLine);
				exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
				System.exit(exit_code);
			}
			pidUtil.makePID();
		} catch (IOException e) {
			Log2.error("[JBridge] [Make PID file fail " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
		}

		Config config = null;
		double div = 0;
		long start = 0, end = 0;
		try {
			config = new SetConfig().getConfig(rta);
			IJob job = JobFactory.getInstance(config, rta.getMode());
			Log2.out("[info] [JBridge] [Source ID: " + rta.getSrcid() + " START]");
			start = System.currentTimeMillis();

			if (rta.isOnlyScdFilter()) {
				Log2.out("[info] [JBridge] [OnlyScdFilter Start]");

				start = System.currentTimeMillis();
				if (job.runScdFilter()) {
					end = System.currentTimeMillis();
					div = ((double) (end - start) / 1000);
					Log2.out("[info] [JBridge] [OnlyScdFilter : Source ID: " + rta.getSrcid() + " run time: " + div
							+ " sec]");
					Log2.out("[info] [JBridge] [OnlyScdFilter : END: Successful]" + StringUtil.newLine);
				} else {
					Log2.error("[JBridge] [OnlyScdFilter : Crawling failed. see log messages.]" + StringUtil.newLine);
				}
			} else {
				if (job.run()) {
					end = System.currentTimeMillis();
					div = ((double) (end - start) / 1000);
					Log2.out("[info] [JBridge] [Source ID: " + rta.getSrcid() + " run time: " + div + " sec]");
					Log2.out("[info] [JBridge] [END: Successful]" + StringUtil.newLine);
				} else {
					Log2.error("[JBridge] [Crawling failed. see log messages.]" + StringUtil.newLine);
				}

				if (rta.isScdFilter()) {
					Log2.out("[info] [JBridge] [ScdFilter Start]");
					start = System.currentTimeMillis();
					if (job.runScdFilter()) {
						end = System.currentTimeMillis();
						div = ((double) (end - start) / 1000);
						Log2.out("[info] [JBridge] [ScdFilter : Source ID: " + rta.getSrcid() + " run time: " + div
								+ " sec]");
						Log2.out("[info] [JBridge] [ScdFilter : END: Successful]" + StringUtil.newLine);
					} else {
						Log2.error("[JBridge] [ScdFilter : Crawling failed. see log messages.]" + StringUtil.newLine);
					}
				}
			}

			pidUtil.deletePID(); // Normal Exit PidUtil Object
		} catch (ConfigException e) {
			Log2.error("[JBridge] [Crawling failed. see log messages.]" + StringUtil.newLine);
			Log2.error("[JBridge] [ConfigException: " + IOUtil.StackTraceToString(e) + StringUtil.newLine + "]");
			pidUtil.leaveErrorPID(); // Abnormal Exit PidUtil Object
			exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
		} catch (DBFactoryException e) {
			Log2.error("[JBridge] [Crawling failed. see log messages.]" + StringUtil.newLine);
			Log2.error("[JBridge] [DBFactory Exception " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
			pidUtil.leaveErrorPID(); // Abnormal Exit PidUtil Object
			exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
		} catch (FilterException e) {
			Log2.error("[JBridge] [Crawling failed. see log messages.]" + StringUtil.newLine);
			Log2.error("[JBridge] [FilterException, Please Check to Config File " + "\n" + IOUtil.StackTraceToString(e)
					+ "\n]");
			pidUtil.leaveErrorPID(); // Abnormal Exit PidUtil Object
			exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
		} catch (BridgeException e) {
			Log2.error("[JBridge] [Crawling failed. see log messages.]" + StringUtil.newLine);
			Log2.error("[JBridge] [BridgeException " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
			pidUtil.leaveErrorPID(); // Abnormal Exit PidUtil Object
			exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
		} catch (Throwable e) {
			Log2.error("[JBridge] [Crawling failed. see log messages.]" + StringUtil.newLine);
			Log2.error("[JBridge] [Throwable message]" + "[" + IOUtil.StackTraceToString(e) + "]");
			pidUtil.leaveErrorPID(); // Abnormal Exit PidUtil Object
			exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
		} finally {
			System.out.println("[info] [JBridge] [Process: Finished]");
			System.out.println("[info] [JBridge] [Exist Code: " + exit_code + " (normal:"
					+ ExistCodeConstants.EXIST_CODE_NORMAL + ", abnormal:" + ExistCodeConstants.EXIST_CODE_ABNORMAL
					+ ")]");
			System.exit(exit_code);
		}
	}
}
