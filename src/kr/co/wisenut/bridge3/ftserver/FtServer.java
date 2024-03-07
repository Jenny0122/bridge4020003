/*
 * @(#)FtServer.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.ftserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.msg.BridgeInfoMsg;
import kr.co.wisenut.common.service.SCMEvent;
import kr.co.wisenut.common.service.SCMEventListener;
import kr.co.wisenut.common.service.SCMEventManager;
import kr.co.wisenut.common.socket.Dispatcher;
import kr.co.wisenut.common.socket.ProtocolFactory;
import kr.co.wisenut.common.text.TextResultSet;
import kr.co.wisenut.common.util.BridgeCommonUtil;
import kr.co.wisenut.common.util.ExistCodeConstants;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.PidUtil;
import kr.co.wisenut.common.util.io.IOUtil;

/**
 *
 * FtServer
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class FtServer implements SCMEventListener{
    /**
     *
     */
    private int servPort = 21020;

    /**
     *
     */
    private static SCMEventManager scm = null;
    /**
     *
     */
    public FtServer(){
        super();
        scm = SCMEventManager.getInstance();
        scm.addSCMEventListener(this);
    }

    /**
     *
     * @param event
     */
    public void handleSCMEvent(SCMEvent event) {
        if(event.getID() == SCMEvent.SERVICE_STOPPED){
            Log2.out("WISEnut Filter Server Service Stopped.");
            System.exit(0);
        }
    }
    /**
     *
     * @return
     */
    public String getStatus() {
        if(scm.size() > 0) {
            return ">> running";
        } else {
            return ">> stop";
        }
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args)  {
    	
        if(args.length == 0) {
            usage();
            System.exit(-1);        	
        }
        
    	BridgeInfoMsg.header(3);
    	
        String sf1_home = "";
        try {
        	sf1_home = getSf1Home();			
		} catch (Exception e) {
            usage();
            System.exit(-1);
		}
    	
        FtServer ftServer = new FtServer();
        ftServer.setFilterExtention();

        // Server startup
        ftServer.preProcess(args);


    }

	private static String getSf1Home() throws Exception {
		String sf1_home = "";
		if(System.getProperty("wcse_home") != null){
            sf1_home = System.getProperty("wcse_home");
        } else if(System.getProperty("sf1_home") != null){
            sf1_home = System.getProperty("sf1_home");
        } else {
        	throw new Exception("sf1_home undefined.");

        }
        

		return sf1_home;
	}
    /**
     *
     * @param args
     */
    public void preProcess(String[] args) {
        if(!arguments(args)) {
			System.exit(1);
		}
        
        String sf1_home = "";
        try {
        	sf1_home = getSf1Home();			
		} catch (Exception e) {
			System.exit(1);
		}
        
        Log2.out("[FtServer] [Version : " + BridgeInfoMsg.getVersion() + "]");

		// System Exit Code
		int exit_code = ExistCodeConstants.EXIST_CODE_NORMAL;

		// Create PidUtil Object
		PidUtil pidUtil = new PidUtil("FtServer", sf1_home);

		try {
			ServerSocket servSock = new ServerSocket(servPort);
			
			Log2.out("[FtServer] [Server port : " + servPort + "]");

			try{
				pidUtil.makePID();
			}catch (IOException e) {
				Log2.error("[WiseProtocol] [Make PID IOException = "
						 +"\n"+IOUtil.StackTraceToString(e)+"\n]");
			}

			ProtocolFactory protoFactory = null;

			protoFactory = (ProtocolFactory)Class.forName(
					"kr.co.wisenut.bridge3.ftserver.WiseProtocolFactory").newInstance();

			Dispatcher dispatcher = (Dispatcher) Class.forName(
					"kr.co.wisenut.common.socket.PoolDispatcher").newInstance();

			dispatcher.startDispatching(servSock, protoFactory);
			pidUtil.deletePID();	// Normal Exit PidUtil Object

		} catch (InstantiationException e) {
			Log2.error("[FtServer] [Pre Process InstantiationException = "
					 +"\n"+IOUtil.StackTraceToString(e)+"\n]");
			pidUtil.leaveErrorPID();	// Abnormal Exit PidUtil Object
			exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
		} catch (IllegalAccessException e) {
			Log2.error("[FtServer] [Pre Process IllegalAccessException = "
					 +"\n"+IOUtil.StackTraceToString(e)+"\n]");
			pidUtil.leaveErrorPID();	// Abnormal Exit PidUtil Object
			exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
		} catch (ClassNotFoundException e) {
			Log2.error("[FtServer] [Pre Process ClassNotFoundException = "
					 +"\n"+IOUtil.StackTraceToString(e)+"\n]");
			pidUtil.leaveErrorPID();	// Abnormal Exit PidUtil Object
			exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
		}  catch (IOException e) {
			Log2.error("[FtServer] [Pre Process IOException = "
					 +"\n"+IOUtil.StackTraceToString(e)+"\n]");
			pidUtil.leaveErrorPID();	// Abnormal Exit PidUtil Object
			exit_code = ExistCodeConstants.EXIST_CODE_ABNORMAL;
		} finally {
			System.out.println("[info] [FtServer] [Process: Finished]");
			System.out.println("[info] [FtServer] [Exist Code: "+ exit_code +" (normal:"+ ExistCodeConstants.EXIST_CODE_NORMAL+", abnormal:"+ExistCodeConstants.EXIST_CODE_ABNORMAL+")]");
			System.exit(exit_code);
		}

    }

    /**
     *
     * @param args
     * @return
     */
    private boolean arguments(String args[]) {
        String sf1_home = "";

        boolean  debug = true;

        String log = "day";
        int loglevel = 2;
         String logPath = null;

        if(System.getProperty("wcse_home") != null) {
            sf1_home = System.getProperty("wcse_home");
        } else if(System.getProperty("sf1_home") != null) {
            sf1_home = System.getProperty("sf1_home");
        } else {
            System.out.println("[ERROR] sf1_home or wcse_home not set");
            System.out.println("usage) useing java -D option\n" +
                    "ex)java -Dsf1_home=/home/wisenut/sf-1" +
                    " or java -Dwcse_home=/home/wisenut/sf-1");
            return false;
        }

        if(args.length < 1) {
            usage();
            return false;
        }

        for(int i=0; i < args.length; i++) {
            if(args[i].toLowerCase().equals("-port")) {
                if(args[i+1] != null) {
                    try {
                        servPort = Integer.parseInt(args[i+1]);
                    } catch(Exception e) {
                        System.out.println("input your -port number");
                    }
                }
            } else if(args[i].toLowerCase().equals("-log")) {
                if(args[i+1] != null) {
                    if(args[i+1].equals("day")) {
                        log = "day";
                    } else if(args[i+1].equals("stdout")) {
                        log = "stdout";
                    }
                }
            } else if(args[i].toLowerCase().equals("-debug")) {
                if(args[i+1] != null) {
                    debug = true;
                    loglevel = 4;
                    if(i+1 < args.length) {
                        if ( !args[i+1].startsWith("-") ) {
                            try{
                                loglevel = Integer.parseInt(args[i+1]);
                            }catch(Exception e){}
                            i++;
                        }
                    }
                }
            }else if (args[i].toLowerCase().equals("-logpath")) {
                if(i+1 < args.length) {
                    if ( !args[i+1].startsWith("-") ) {
                        try{
                            logPath = args[i+1];
                        }catch(Exception e){}
                        i++;
                    }
                }
            }else if(args[i].equals("-help") || args[i].equals("-?")) {
                usage();
                System.exit(1);
            }
        }

        if(servPort == -1) {
            System.out.println("\ninput your \n -port <portNum>");
            usage();
            System.exit(1);
        }
        
        try {
            if( logPath != null) {
                Log2.setLogger(logPath, log, debug, loglevel, false, "FtServer");
             }else{
                Log2.setBridgeLogger(sf1_home, log, debug, loglevel, false, "FtServer");
             }			
		} catch (Exception e) {
			System.out.println("[FtServer] [Set Logger fail. "+"\n"+IOUtil.StackTraceToString(e)+"\n]");
			System.exit(-1);
		}

        return true;
    }

    /**
     *
     */
    private static void usage() {
        System.out.println("Usage)java -Dsf1_home=$SF1_HOME (-Dwcse_home=$WCSE_HOME)" +
        		"-Dsf1.var=<4|5> " + 
                "-Dthreadno=2 kr.co.wisenut.bridge3.ftserver.FtServer " +
                "-port <number> [option]");
        System.out.println("option\t-log <day|stdout>");
        System.out.println("      \t-debug <1~4>");
    }

    private void setFilterExtention() {
        try {
            String line;
            TextResultSet ts = new TextResultSet(new File(System.getProperty("sf1_home"), "config/file.allow"), "EUC-KR");

            Pattern pattern = Pattern.compile("^allow*=[A-Za-z]");
            Vector v = new Vector(50);
            while(ts.next()) {
                line = ts.getString();
                Matcher matcher = pattern.matcher(line);
                if(matcher.find()) {
                    v.add(line.substring(line.indexOf("=")+1, line.length()));
                }
            }

            String[] extAllowList = new String[v.size()];
            for(int i = 0; i < v.size(); i++) {
                extAllowList[i] = (String) v.get(i);
//                System.out.println("FilterExt=" + extAllowList[i]);
            }
            
            FileUtil.setFilterExt(extAllowList);

        } catch (IOException e) {
            System.out.println("FtServer::setFilterExtention()\nsf1_home=)"+System.getProperty("sf1_home"));
            System.out.println("FtServer::setFilterExtention()\n Filtering Extension properties notfound($SF1_HOME/config/file.allow)");
            System.out.println(e.toString());
        }
    }
}
