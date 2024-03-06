/*
 * @(#)AttachInfo.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.config.source;

import kr.co.wisenut.common.Exception.FilterException;

import java.util.Vector;

/**
 *
 * AttachInfo
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class AttachInfo {
    private Vector[] vtAttach;
    private String[] colunmData;
    private int useCnt = 0;

    /**
     * Get Attach file info and attach file info method
     * @param scdFileCnt SCD Filed count
     * @param useCnt using filtering count
     */
    public AttachInfo(int scdFileCnt, int useCnt) {
        this.useCnt = useCnt;
        vtAttach = new Vector[useCnt];
        for(int i=0; i<useCnt; i++) {
            vtAttach[i] = new Vector();
        }
        if(scdFileCnt < 0) scdFileCnt = 0;
        colunmData = new String[scdFileCnt];
    }

    public void addAttach(int idx, String attach) throws FilterException {
        try{
            vtAttach[idx].add(attach);
        }catch(Exception e){
            throw new FilterException(": Please check the <Filter usecnt=\"\"> and " +
                    "SCD column count in config file.");
        }
    }

    public String[][] getAttachse() {
        int vtSize = vtAttach[0].size();
        String[][] attaches = new String[vtSize][useCnt];
        for(int i=0; i<vtSize; i++) {
            for(int j=0; j<useCnt; j++) {
                String attachName = (String) vtAttach[j].get(i);
                if(!attachName.equals(""))
                    attaches[i][j] = attachName;
            }
        }
        return attaches;
    }

    public String[] getColunmData() {
        // NULL check
        for( int i=0; i<colunmData.length; i++ ) {
            if( colunmData[i] == null ) {
                colunmData[i] = "";
            }
        }
        return colunmData;
    }

    public void setColunmData(int idx ,String colunmData) {
        this.colunmData[idx] = colunmData;
    }
}
