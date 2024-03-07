/*
 * @(#)Query.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.config.source;

import kr.co.wisenut.common.util.StringUtil;

/**
 *
 * Query
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class Query {
    private String dynMode = "none";
    private boolean isSeqNoYn = true;
    private String staticQuery = "";
    private String deleteLogTable = "";
    private String syncLogQuery = "";
    private String syncQueueQuery = "";
    private String deleteQueueDocRow = "";
    private String deleteQueueDocSelect = "";
    private String useDateDeleteDoc = "";
    //private String InitSelectDoc = "";
    private String insertQuery = "";
    private String insertUpdateQuery = "";
    private String updateQuery = "";
    private String masterTblSelect = "";
    private String replaceQuery = "";	 //ADD 2006/01/10
    //private String[][] customSelect = null;
    private String[] initExecute = null;
    private String[] DynExecute = null;
    private String[] dynInitExecute = null;
    private String[] staticInitExecute = null;
    private boolean[] isStatement = null; //ADD 2006/02/13

    private String dateTimePropertyPath = "";  //ADD 2011/09/09
    private String dateTimeQuery = "";

    public String[] getDynExecute() {
        return DynExecute;
    }

    public void setDynExecute(String[] dynExecute) {
        DynExecute = dynExecute;
    }

    public String[] getDynInitExecute() {
        return dynInitExecute;
    }
    
	public String[] getStaticInitExecute() {
		return staticInitExecute;
	}

    public void setDynInitExecute(String[] dynInitExecute) {
        this.dynInitExecute = dynInitExecute;
    }
    
    public void setStaticInitExecute(String[] staticInitExecute) {
        this.staticInitExecute = staticInitExecute;
    }    

    public String getInsertUpdateQuery() {
        return insertUpdateQuery;
    }

    public void setInsertUpdateQuery(String insertUpdateQuery) {
        this.insertUpdateQuery = insertUpdateQuery;
    }

    public boolean[] isStatement() {
        return isStatement;
    }

    public void setStatement(String[] statement) {
    	isStatement = new boolean[statement.length];
    	for(int i=0; i<statement.length; i++){
    		if(statement[i].equalsIgnoreCase("y"))
    			isStatement[i] = true;
    	}
    }

    public boolean isSeqNoYn() {
        return isSeqNoYn;
    }

    public void setSeqNoYn(String SeqNoYn) {
        if(SeqNoYn.equalsIgnoreCase("y")) {
            isSeqNoYn = true;
        }else if(SeqNoYn.equalsIgnoreCase("n")) {
        	isSeqNoYn = false;
        }
    }

    public String getDynMode() {
        return dynMode;
    }

    public void setDynMode(String dynMode) {
        this.dynMode = dynMode;
    }

     public String getStaticQuery() {
        return staticQuery;
    }

    public void setStaticQuery(String staticQuery) {
        this.staticQuery = staticQuery;
    }

    public String getSyncLogQuery() {
        return syncLogQuery;
    }

    public void setSyncLogQuery(String syncLogQuery) {
        this.syncLogQuery = StringUtil.trimDuplecateSpace(syncLogQuery);
    }

    public String getDeleteQData() {
        return deleteQueueDocRow;
    }

    public void setDeleteQData(String deleteQueueDocRow) {
        this.deleteQueueDocRow = StringUtil.trimDuplecateSpace(deleteQueueDocRow);
    }

    public String getSelectQDeleteDoc() {
        return deleteQueueDocSelect;
    }

    public void setSelectQDeleteDoc(String deleteQueueDocSelect) {
        this.deleteQueueDocSelect = StringUtil.trimDuplecateSpace(deleteQueueDocSelect);
    }

    public String getUseDateDeleteDoc() {
        return useDateDeleteDoc;
    }

    public void setUseDateDeleteDoc(String useDateDeleteDoc) {
        this.useDateDeleteDoc = StringUtil.trimDuplecateSpace(useDateDeleteDoc);
    }

    public String getSyncQueueQuery() { //ADD 2005/05/17
        return syncQueueQuery;
    }

    public void setSyncQueueQuery(String syncQueueQuery) {
        this.syncQueueQuery = syncQueueQuery;
    }

    /*public String getInitSelectDoc() {      //ADD 2005/05/17
        return InitSelectDoc;
    }

    public void setInitSelectDoc(String initSelectDoc) {
        InitSelectDoc = initSelectDoc;
    }*/


    public String getSQLInsert() {
        return insertQuery;
    }

    public void setSQLInsert(String staticSelect) {
        this.insertQuery = StringUtil.trimDuplecateSpace(staticSelect);
    }

    public String getSQLUpdate() {
        return updateQuery;
    }

    public void setSQLUpdate(String dynSelect) {
        this.updateQuery = StringUtil.trimDuplecateSpace(dynSelect);
    }

    /*public String[][] getCustomSelect() {
        return customSelect;
    }

    public void setCustomSelect(String[][] customSelect) {
        this.customSelect = customSelect;
    }

    public String[][] getCustomExecute() {
        return customExecute;
    }

    public void setCustomExecute(String[][] customExecute) {
        this.customExecute = customExecute;
    }*/

    public String[] getInitExecute() {
        return initExecute;
    }

    public void setInitExecute(String[] initExecute) {
        this.initExecute = initExecute;
    }

    public String getDeleteLogTable() {
        return deleteLogTable;
    }

    public void setDeleteLogTable(String deleteLogTable) {
        this.deleteLogTable = deleteLogTable;
    }

    public String getMasterTblSelect() {
        return masterTblSelect;
    }

    public void setMasterTblSelect(String masterTblSelect) {
        this.masterTblSelect = masterTblSelect;
    }

    public String getReplaceQuery() {
        return replaceQuery;
    }

    public void setReplaceQuery(String replaceQuery) {
        this.replaceQuery = StringUtil.trimDuplecateSpace(replaceQuery);
    }

        public String getDateTimePropertyPath() {
        return dateTimePropertyPath;
    }

    public void setDateTimePropertyPath(String dateTimePropertyPath) {
        this.dateTimePropertyPath = dateTimePropertyPath;
    }

    public String getDateTimeQuery() {
        return dateTimeQuery;
    }

    public void setDateTimeQuery(String dateTimeQuery) {
        this.dateTimeQuery = dateTimeQuery;
    }
}
