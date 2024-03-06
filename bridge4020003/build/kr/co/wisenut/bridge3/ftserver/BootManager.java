/*
 * @(#)BootManager.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.ftserver;

import kr.co.wisenut.common.util.PidUtil;
import kr.co.wisenut.common.util.io.IOUtil;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.socket.Packets;

import java.io.*;
import java.net.Socket;
import java.lang.reflect.Method;

/**
 *
 * BootManager
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class BootManager {
    private static int debug = 0;
    private static boolean START = false;
    private static boolean STOP = false;
    private static  int port;
    private static Packets oData = new Packets();
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        if(args.length < 1) {
            usage();
            return ;
        }

        for(int i=0; i< args.length; i++) {
            if("-debug".equals(args[i])) {
                debug = 1;
            }
            if(args[i].equalsIgnoreCase("-start")) {
                START = true;
            }
            if(args[i].equalsIgnoreCase("-stop")) {
                STOP = true;
            }
            if(args[i].equals("-port")) {
                if(args[i+1] != null) {
                    try {
                        port = Integer.parseInt(args[i+1]);
                    } catch(Exception e) {
                        usage() ;
                        System.out.println("Input : -port number");
                    }
                }
            }
        }

        if(START) {
            String methodName = "preProcess";
            Class paramTypes[] = new Class[1];
            paramTypes[0] = args.getClass();
            Object paramValues[] = new Object[1];
            paramValues[0] = args;

            try {
                if(debug >= 1) {
                    log("[BootManager ] [Loading startup class]");
                }
                Class ftServerClass = ClassLoader.getSystemClassLoader().loadClass("kr.co.wisenut.bridge3.ftserver.FtServer");
                Object ftServerInstance = ftServerClass.newInstance();

                if(debug >= 1 ) {
                    log("[BootManager ] [Setting startup Method Invoke]");
                }
                Method method = ftServerInstance.getClass().getMethod(methodName, paramTypes);
                method.invoke(ftServerInstance, paramValues);

                
            } catch (Exception e) {
                log("[BootManager] [ "+"\n"+IOUtil.StackTraceToString(e)+"\n]");
                System.exit(-1);
            }
        }else if(STOP){
            try {
                Socket socket =  new Socket("127.0.0.1", port);
                OutputStream  out = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(out);
                oData.setMsg("CMD:SHUTDOWN");

                oos.writeObject(oData);
                oos.flush();
                oos.reset();

                Thread.sleep(1000);

                if (socket != null) socket.close();
                if (out != null) out.close();
                if (oos != null) oos.close();
            } catch (IOException e) {
                log("[BootManager ] [Server already  Stop"
                +"\n"+IOUtil.StackTraceToString(e)+"\n]");
            } catch (InterruptedException e) {
                log("[BootManager ] [Server Stop "
                        +"\n"+IOUtil.StackTraceToString(e)+"\n]");
            }
        }else{
            System.exit(-1);
        }
    }

    /**
     *
     * @param msg
     */
    private static void log(String msg) {
        System.out.println(msg);
    }

    /**
     *
     */
    private static void usage() {
        System.out.println("Usage)java -Dsf1_home=$SF1_HOME (or -Dwcse_home=$WCSE_HOME) -Dsf1.ver=<4|5> -Dthreadno=2 kr.co.wisenut.bridge3.ftserver.FtServer -port <number> [option]");
        System.out.println("option\t-log <day|stdout>");
        System.out.println("      \t-debug <1~4>");
    }
}
