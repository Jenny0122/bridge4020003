/*
 * @(#)RunTimeArgs.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.config;

import kr.co.wisenut.common.msg.BridgeInfoMsg;

/**
 *
 * RunTimeArgs
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class RunTimeArgs {
    private String conf = "";
    private String srcid = "";
    private String log = "week";
    private String sf1_home = "";
    private int loglevel = 1;
    private boolean verbose = false;
    private int mode = -1;
    private boolean filterFileDelete = true;
    private boolean debug = false;
    private boolean userIndexDir = false;
    private String logPath = null;
    private String scdRcvMode = "all";
    
    /**
     * 
     * @param args input Argument
     * @return success or fail
     */
    public boolean readargs(String sf1_home, String[] args) {
        boolean isConf = false;
        boolean isSrc = false;
        boolean isMode = false;
        boolean isRet = true;
        int length = args.length;
        if(length == 0){
            BridgeInfoMsg.usage(1);
            return false;
        }

        this.sf1_home = sf1_home;

        for (int i = 0; i < length; i++) {
            if (!args[i].startsWith("-")) { //Argument - text check
                BridgeInfoMsg.usage(1);
                isRet = false;
            }
            if (args[i].equalsIgnoreCase("-help")) {
                BridgeInfoMsg.usage(1);
                isRet = false;
            }
            if (args[i].equalsIgnoreCase("-conf")) {
                if (i < length-1 && !args[i+1].startsWith("-")) {
                    isConf = true;
                    this.conf = args[i+1];
                    i++;
                }else{
                    isRet = false;
                    break;
                }
            } else if (args[i].equalsIgnoreCase("-srcid")) {
                if (i<length-1 && !args[i+1].startsWith("-")) {
                    isSrc = true;
                    this.srcid = args[i+1];
                    i++;
                }else{
                    isRet = false;
                    break;
                }
            } else if (args[i].equalsIgnoreCase("-mode")) {
                if (i<length-1 && !args[i+1].startsWith("-")) {
                    String tMode = args[i+1];
                    if (tMode.equalsIgnoreCase("init")) {
                        isMode = true;
                        this.mode = 0;
                    }else if (tMode.equalsIgnoreCase("static")) {
                        isMode = true;
                        this.mode = 1;
                    }else if (tMode.equalsIgnoreCase("test")) {
                        isMode = true;
                        this.mode = 2;
                    }else {
                        error("*** Web Bridge RunTime Mode Error !! -mode "+tMode+" ***");
                        isRet = false;
                    }
                    i++;
                } else {
                    isRet = false;
                    break;
                }
            } else if (args[i].equalsIgnoreCase("-log")) {
                if(i+1 < length) {
                    if ( !args[i+1].startsWith("-") ) {
                        if( args[i+1].equalsIgnoreCase("stdout")){
                            this.log = "stdout";
                        } else if( args[i+1].equalsIgnoreCase("day")){
                            this.log = "day";
                        } else {
                            this.log = "stdout";
                        }
                        i++;
                    }
                }
            } else if (args[i].equalsIgnoreCase("-verbose")) {
                this.verbose = true;
            } else if (args[i].equalsIgnoreCase("-filterdel")) {
                if (!args[i+1].startsWith("-")) {
                    if(args[i+1].equals("false"))
                        filterFileDelete = false;
                    i++;
                }
            } else if (args[i].equalsIgnoreCase("-debug")) {
                debug = true;
                loglevel = 1;
                if(i+1 < length) {
                    if ( !args[i+1].startsWith("-") ) {
                        try{
                            loglevel = Integer.parseInt(args[i+1]);
                        }catch(Exception e){}
                        i++;
                    }
                }
            } else if (args[i].equalsIgnoreCase("-useindexdir")
                    || args[i].equalsIgnoreCase("-indexdir")) {
                this.userIndexDir = true;
            } else if (args[i].equalsIgnoreCase("-logpath")) {
                if(i+1 < length) {
                    if ( !args[i+1].startsWith("-") ) {
                        try{
                            logPath = args[i+1];
                        }catch(Exception e){}
                        i++;
                    }
                }
            } else if(args[i].equalsIgnoreCase("-rcvmode")) {
            	if( i+1 <length) {
            		if ( !args[i+1].startsWith("-") ){
            			try{
            				scdRcvMode = args[i+1];
            			}catch(Exception e) {}
            			i++;
            		}
            	}
            } else if (args[i].toLowerCase().startsWith("-p")) {
//                	TO DO 
            	i++;
            } else {
                error("Unknown RunTime Args :"+args[i]);
                isRet = false;
            }
        }

        if(!isRet || !isConf || !isSrc || !isMode){
            error("*** Runtime Arg Error ***");
            isRet = false;
        }
        if (conf.equals("")) {
            error(">> Not Found -conf <config file path>");
            isRet = false;
        }
        if (srcid.equals("")) {
            error(">> Not Found -srcid <source id>");
            isRet = false;
        }
        if (mode == -1) {
            error(">> Not Found -mode <init | test | static>");
            isRet = false;
        }
        return isRet;
    }

    /**
     *
     * @return conf
     */
    public String getConf() {
        return conf;
    }

    public String getSf1_home() {
        return sf1_home;
    }

    public String getSrcid() {
        return srcid;
    }

    /**
     *
     * @return  log
     */
    public String getLog() {
        return log;
    }

    /**
     *
     * @return loglevel
     */
    public int getLoglevel() {
        return loglevel;
    }

    /**
     *
     * @return  verbose
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     *
     * @return mode
     */
    public int getMode() {
        return mode;
    }

    /**
     *
     * @return  filterFileDelete
     */
    public boolean isFilterFileDelete() {
        return filterFileDelete;
    }

    /**
     *
     * @return debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     *
     * @return userIndexDir
     */
    public boolean isUserIndexDir() {
        return userIndexDir;
    }

    /**
     *
     * @param err error message
     */
    private void error(String err){
        System.out.println(err);
    }

    public String getLogPath() {
        return logPath;
    }
    
    public String getScdRcvMode() {
		return scdRcvMode;
	}
}
