/*
 * @(#)WiseProtocol.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.ftserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.Exception.BridgeException;
import kr.co.wisenut.common.filter.FilterFactory;
import kr.co.wisenut.common.filter.IFilter;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.socket.Packets;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.io.IOUtil;
import kr.co.wisenut.common.util.time.StopWatch;

/**
 *
 * WiseProtocol
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class WiseProtocol implements Runnable,Serializable  {
    /**
     *
     */
    private Socket clntSock;  // Connection socket

    /**
     *
     */
    private boolean isAlive = true;
    /**
     *
     */
    private String CMD_DISCONNECT = "CMD:DISCONNECT";
    /**
     * 
     */
    private String CMD_SHUTDOWN = "CMD:SHUTDOWN";
    /**
     *
     */
    private InputStream in = null;
    /**
     *
     */
    private OutputStream out = null;
    /**
     *
     */
    private ObjectOutputStream oos = null;
    /**
     *
     */
    private ObjectInputStream ois = null;
    
    private String filteredTextDir = null;
    
    private Boolean isFilterDel = null;

	private String clientIp;

    /**
     *
     * @param clntSock
     */
    public WiseProtocol(Socket clntSock) {
        this.clntSock = clntSock;
        InetAddress addr = clntSock.getInetAddress();
        this.clientIp = addr.getHostAddress();

    }

    /**
     * run method
     */
    public void run() {
        try {
            Log2.out("[WiseProtocol] [" + clientIp + " connected. (port : " + clntSock.getPort() + ")]");
            
            in = clntSock.getInputStream();
            out = clntSock.getOutputStream();
            oos = new ObjectOutputStream(out);
            ois = new ObjectInputStream(in);
            
            Log2.out("[WiseProtocol] [Run Method Start Processing ....]");
            
            while( isAlive) {
                try{
                    Object oData = ois.readObject();
                    Packets dataClass = (Packets) oData;
                    String msg = dataClass.getMsg();
                    if(msg.equals(CMD_DISCONNECT)) {
                        Log2.out("[WiseProtocol] [Normal disconnected to FtClient]");
                        isAlive = false;
                    } else if(msg.equals(CMD_SHUTDOWN)){
                        Log2.out("[WiseProtocol] [FtServer Process Finished");
                        isAlive = false;
                        destory() ; //close socket
                        System.exit(0);
                    }else {
                        //starting message process
                        try{
                            process(dataClass);
                        }  catch (BridgeException e) {
                            Log2.error("[WiseProtocol] [process Method Exception :  "
                                   +"\n"+IOUtil.StackTraceToString(e)+"\n]");
                        }
                    }
                } catch (Exception e) {
                      Log2.error("[WiseProtocol] [process socket Exception :  "
                              +"\n"+IOUtil.StackTraceToString(e)+"\n]");
                      isAlive = false;
                }
            }
        }catch (SocketException e) {
            Log2.error("[WiseProtocol] [Thread Run Method Exception : invalid client request. msg : " + e.getMessage());            
        }catch (Exception e) {
            Log2.error("[WiseProtocol] [Thread Run Method Exception : "
                         +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        } finally{
        	if(isFilterDel != null && isFilterDel.booleanValue() && new File(filteredTextDir).exists()) {
        		gabageFilteredTextFile();
        	}
            destory() ;
        }
    }

    private void gabageFilteredTextFile() {
        if( !FileUtil.deleteFile(filteredTextDir, ".txt") ){
			Log2.error("[WiseProtocol] [Some filtered Files Delete Failed, Directory: " + filteredTextDir + "]");
        }
        Log2.debug("[WiseProtocol] [Gabage filtered text file deleted.]", 4);
	}

	/**
     *
     * @param oData
     */
    private void process(Packets oData)  throws BridgeException{
        String[][] arrSourceInfo = oData.getSourceFileInfo() ;
        
        FilterSource filterSource = new FilterSource();
        filterSource.setDir(oData.getPreFixDir());
        filterSource.setRetrival(oData.getRetrive());
        filterSource.setCondition(oData.getCondition());
        filterSource.setClassName(oData.getClassName());
        filterSource.setFilterType(oData.getFilter());
        filterSource.setSplit(oData.getSplit());
        filterSource.setJungumKey(oData.getJungumKey());
        filterSource.setSeperator(oData.getSeperator());
        
        String charset = oData.getCharset();
        boolean isFilterDel = oData.isFilterDel();
        
        Log2.debug("[WiseProtocol] [Start Filtering process.]");
        
        if(this.isFilterDel == null) {
        	this.isFilterDel = new Boolean(isFilterDel);
        	Log2.out("[WiseProtocol] [isFilterDel : " + isFilterDel+ "]");        	
        }
        
        if(filteredTextDir == null) {
			// TODO 한서버에서 동일한 source id 를 동시에 돌리는 것 허용해야 하는지?...
			// 만약 그래야 한다면 동기화 처리 및 unique 한 디렉토리 생성해야 하고 끝나고 나면 디렉토리도 지워야 한다. 
			filteredTextDir = FileUtil.lastSeparator(System.getProperty("sf1_home")) + "Filter" + FileUtil.getFileSeperator()
					+ "ftserver_" + clientIp + "_" + oData.getSrcId() + FileUtil.getFileSeperator();
        	Log2.debug("[WiseProtocol] [Filtered text directory : " + filteredTextDir + "]", 4);
        }
        
        IFilter ifilter = new FilterFactory().getInstance(filterSource.getClassName(),  new Boolean(isFilterDel), filteredTextDir);
        if(ifilter == null) {
            Log2.out("[WiseProtocol] [Not Found Class in Config File, " +
                    "Please Check to <Filter className]");
        }
        String ret = "";
        if (ifilter != null) {
            ret = ifilter.getFilterData(arrSourceInfo, filterSource, charset);
        }
        oData.setAttachData(ret);
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Log2.debug("[WiseProtocol] [End filtered txt sending...]");
        
        send(oData);
        
        stopWatch.stop();
        Log2.debug("[WiseProtocol] [filtered data send completed. elapsed time : " + stopWatch.getTime() + " msec]");
        Log2.debug("[WiseProtocol] [End Filtering process.]");
    }

    /**
     *
     * @param oData
     * */
    private void send(Object oData) {
        try {
            oos.writeObject(oData);
            oos.flush();
            oos.reset();  // NOTE: reset() is very important.
            //Thread.sleep(50);
        } catch (IOException e) {
            Log2.error("[WiseProtocol] [Data Send IOException : "
                            +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        } /*catch (InterruptedException e) {
            Log2.error("[WiseProtocol] [Data Send InterruptedException : "
                            +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        }*/
    }

    /**
     * Client Socket Close Function
     */
    private void destory() {
        Log2.out("[WiseProtocol] [Message] [Client Socket Closed]");
        try {
            if (clntSock != null) {
                clntSock.close();
            }
            if ( out != null ){
                out.close();
            }
            if ( in != null ){
                in.close();
            }
            if ( oos != null ) {
                oos.close();
            }
            if ( ois != null ){
                ois.close();
            }
        } catch (IOException e) {
            Log2.error("[WiseProtocol ] [Destory Method : "
                         +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        }

    }
}
