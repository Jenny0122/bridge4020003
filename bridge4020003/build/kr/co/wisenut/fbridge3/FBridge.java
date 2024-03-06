/*
 * @(#)FBridge.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.fbridge3;

import java.io.IOException;

import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.msg.BridgeInfoMsg;
import kr.co.wisenut.common.util.BridgeCommonUtil;
import kr.co.wisenut.common.util.ExistCodeConstants;
import kr.co.wisenut.common.util.PidUtil;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.io.IOUtil;
import kr.co.wisenut.fbridge3.config.Config;
import kr.co.wisenut.fbridge3.config.RunTimeArgs;
import kr.co.wisenut.fbridge3.config.SetConfig;
import kr.co.wisenut.fbridge3.job.Job;

/**
 *
 * FBridge
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class FBridge {

    public static void main(String[] args) {
        if(args.length ==0) {
            BridgeInfoMsg.header(2);
            BridgeInfoMsg.usage(2);
            System.exit(-1);
        }

        BridgeInfoMsg.header(2);
        String sf1_home = "";
        if(System.getProperty("wcse_home") != null){
        	sf1_home = System.getProperty("wcse_home");
        } else if(System.getProperty("sf1_home") != null){
        	sf1_home = System.getProperty("sf1_home");
        } else {
            BridgeInfoMsg.usageSF1_HOME();
            System.exit(-1);
        }

        RunTimeArgs rta = new RunTimeArgs();
        if( !rta.readargs(sf1_home, args) ) {
            System.exit(-1);
        }
        
        try {
            // Create Log Object
            if( rta.getLogPath() != null) {
                Log2.setLogger(rta.getLogPath(), rta.getLog(), rta.isDebug(), rta.getLoglevel(), rta.isVerbose(), rta.getSrcid());
             }else{
                Log2.setBridgeLogger(sf1_home, rta.getLog(), rta.isDebug(), rta.getLoglevel(), rta.isVerbose(), rta.getSrcid());
             }		
		} catch (Exception e) {
			System.out.println("[FBridge] [Set Logger fail. "+"\n"+IOUtil.StackTraceToString(e)+"\n]");
			System.exit(-1);
		}


        // System Exit Code
		int exit_code = ExistCodeConstants.EXIST_CODE_NORMAL;

        // Create PidUtil Object
        PidUtil pidUtil = new PidUtil(rta.getSrcid(), sf1_home);
        try {
            if(pidUtil.existsPidFile()) {
                Log2.error("[Bridge] [Crawling failed. Is already running in source.)]"+StringUtil.newLine) ;
                exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
                System.exit(exit_code);
            }
            pidUtil.makePID();
        } catch (IOException e) {
            Log2.error("[FileBridge] [Make pid file fail "+IOUtil.StackTraceToString(e)+"]");
        }
        try {
            Log2.out("[info] [FileBridge] [Source ID: "+rta.getSrcid()+" START]");
            long start = System.currentTimeMillis();
            Config config = new SetConfig().getConfig(rta);
            Job curl = new Job(sf1_home, config, rta.getMode(), rta.isFilterFileDelete(), rta.isVerbose());
            if(curl.run()){
                long end = System.currentTimeMillis() ;
                double div = ((double)(end-start)/1000) ;
                Log2.out("[info] [FileBridge] [Source ID: "+rta.getSrcid()+" run time: "+div+" sec]");
                Log2.out("[info] [FileBridge] [END: Successful]"+StringUtil.newLine);
            }else{
                Log2.error("[FileBridge] [Crawling failed. see log messages.]"+StringUtil.newLine) ;
            }
			pidUtil.deletePID();	// Normal Exit PidUtil Object
        } catch (ConfigException e) {
            Log2.error("[FileBridge] [Crawling failed. see log messages.]"+StringUtil.newLine) ;
            Log2.error("[FileBridge] [ConfigException: "+IOUtil.StackTraceToString(e)+StringUtil.newLine+"]");
			pidUtil.leaveErrorPID();	// Abnormal Exit PidUtil Object
			exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
        } catch(IOException e){
            Log2.out("[info] [FileBridge] [END: Fail Please Check Error Log File]"+StringUtil.newLine) ;
            Log2.error("[FileBridge] [IOException "+IOUtil.StackTraceToString(e)+StringUtil.newLine + "]");
			pidUtil.leaveErrorPID();	// Abnormal Exit PidUtil Object
			exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
        }catch (Throwable e) {
            Log2.error("[JBridge] [Crawling failed. see log messages.]"+StringUtil.newLine) ;
            Log2.error("[JBridge] [Throwable message]" + "[" + IOUtil.StackTraceToString(e) + "]");
            pidUtil.leaveErrorPID();	// Abnormal Exit PidUtil Object
            exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;        			
        } finally {
            System.out.println("[info] [FileBridge] [Process: Finished]");
			System.out.println("[info] [FileBridge] [Exist Code: "+exit_code+" (normal:"+ ExistCodeConstants.EXIST_CODE_NORMAL+", abnormal:"+ExistCodeConstants.EXIST_CODE_ABNORMAL+")]");
			System.exit(exit_code);
        }
    }
}

