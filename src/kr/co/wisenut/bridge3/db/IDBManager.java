/*
 * @(#)IDBManager.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.db;

import kr.co.wisenut.common.Exception.DBFactoryException;
import java.sql.*;

/**
 *
 * IDBManager
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public interface IDBManager {

    public String[][] getString(ResultSet rs, int idx) throws SQLException, DBFactoryException;

    public String[][] getString(ResultSet rs, int idx, int count) throws DBFactoryException;

    public void setResultSet(String query) throws DBFactoryException;

    public boolean next(ResultSet rs);

    public int getColunmCnt(ResultSet rs);

    public int executePQuery(Connection conn, String query, String[][] values, boolean isStatement) throws SQLException, DBFactoryException;

    public String[][] getQueryData(Connection conn, String query, String[][] values, String seperator, boolean isStatement);

    public void releaseDB(Connection conn);

    public void releaseDB(ResultSet rs, Statement stmt);

    public void releaseDB(ResultSet rs, PreparedStatement pstmt);
}
