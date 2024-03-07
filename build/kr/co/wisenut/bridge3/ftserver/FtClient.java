/*
 * @(#)FtClient.java   3.8.1 2009/03/11
 *
 */

package kr.co.wisenut.bridge3.ftserver;

import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.Exception.BridgeException;
import kr.co.wisenut.common.socket.Packets;
import kr.co.wisenut.common.util.io.IOUtil;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * FtClient
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class FtClient {
    public String CMD_DISCONNECT = "CMD:DISCONNECT";
    private Socket socket;
    private OutputStream out = null;
    private InputStream in = null;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    
    private String srcId = null;

    public FtClient(String srcId, String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = socket.getOutputStream();
        in = socket.getInputStream();
        oos = new ObjectOutputStream(out);
        ois = new ObjectInputStream(in);
        this.srcId = srcId;
    }

    public boolean getFilterData(String[][] sourceFileInfo, FilterSource filterSource, String charset,  boolean isFilterDel)
            throws BridgeException, IOException {
        Packets dataClass = new Packets();
        dataClass.setSourceFileInfo(sourceFileInfo);
        dataClass.setPreFixDir(filterSource.getDir());
        dataClass.setRetrive(filterSource.getRetrival());
        dataClass.setCondition(filterSource.getCondition());
        dataClass.setClassName(filterSource.getClassName());
        dataClass.setFilter(filterSource.getFilterType());
        dataClass.setSplit(filterSource.getSplit());
        dataClass.setJungumKey(filterSource.getJungumKey());
        dataClass.setCharset(charset);
        dataClass.setFilterDel(isFilterDel);
        dataClass.setSrcId(srcId);
        dataClass.setSeperator(filterSource.getSeperator());

        send(dataClass);      //Send to Filtering Object Class
        return true;
    }

    public boolean excute(String cmd) throws IOException {
        Packets dataClass = new Packets();
        if(cmd.equals(CMD_DISCONNECT)) {
            dataClass.setMsg(CMD_DISCONNECT);
            send(dataClass);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log2.error("[FtClient ] [Excute Method : "+e+"]");
            }
            destory() ;
            return true;
        }else{
            return false;
        }
    }

    public String  receive() {
        Object oData = null;
        Packets dataClass = new Packets();
        try{
            oData = ois.readObject();
            dataClass = (Packets) oData;
        }catch(ClassNotFoundException e){
            Log2.error("[FtClient ] [Receive Exception : "+e+"]");
        }catch(IOException e){
            Log2.error("[FtClient ] [Receive Exception : "+e+"]");
        }
        return dataClass.getAttachData();
    }

    public void send(Object oData) throws IOException {
        oos.writeObject(oData);
        oos.flush();
        oos.reset();  // NOTE: reset() is very important.
    }

    /**
     * Client Socket Close Function
     */
    public void destory() {
        try {
            if (socket != null) socket.close();
            if ( out != null ) out.close();
            if ( in != null ) in.close();
            if ( oos != null ) oos.close();
            if ( ois != null ) ois.close();
        } catch (IOException e) {
            Log2.error("[FtClient ] [Destory Method : "
            +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        }

    }
}
