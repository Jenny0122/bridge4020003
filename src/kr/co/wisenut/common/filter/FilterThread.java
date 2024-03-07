/*
 * @(#)FilterThread.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.filter;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.io.IOUtil;

import java.io.InputStream;
import java.io.IOException;

/**
 *
 * FilterThread
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class FilterThread  extends Thread{
    public boolean procs = true;
    private String[] cmd = null;
    private Process process;

    public FilterThread(String[] cmd){
        this.cmd = cmd;
    }

 
    public void run() {
        procs = true;
        InputStream procInput =  null;
        InputStream procErr =  null;
        try{
            process = Runtime.getRuntime().exec(cmd);
            // for(int i=0; i < cmd.length; i++)System.out.println(cmd[i]);
            procInput = process.getInputStream();
            procErr = process.getErrorStream();

            byte[] bytes = new byte[1024];
            int bytesRead = procErr.read(bytes);
            if (bytesRead > 0) {
                Log2.debug("[FilterThread] [Filter Error: " + new String(bytes)+"]",3 );
            }
            bytes = new byte[1024];
            bytesRead = procInput.read(bytes);
            if (bytesRead > 0) {
                Log2.debug("[FilterThread] [Filter Out: " + new String(bytes)+"]",3 );
            }
            if(process!= null)  {
                process.waitFor(); 
            }
        }catch(IOException e){
            close(procInput, procErr);
            Log2.error("[FilterThread] [Process I/O Exception : "
                    +IOUtil.StackTraceToString(e)+"\n]");
            Log2.error("[FilterThread] [Please Check Target Document Or Document Filter Binary]");
        }catch(InterruptedException e){}

        close(procInput, procErr);
    }

    public void ForceEnd(){
        if(process != null) {
            process.destroy();
            process = null;
        }
    }

    private void close(InputStream procInput, InputStream procErr){
        procs=false;
        try{
            if(procInput != null){
                procInput.close();
            }
            if(procErr != null) {
                procErr.close();
            }
        }catch(IOException e){
            Log2.error("[FilterThread] [Process Close IOException "
                    +IOUtil.StackTraceToString(e)+"\n]");
        }
    }
}