/*
 * @(#)DBJob.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import kr.co.wisenut.bridge3.config.Config;
import kr.co.wisenut.bridge3.config.source.AttachInfo;
import kr.co.wisenut.bridge3.config.source.MemoryTable;
import kr.co.wisenut.bridge3.job.IJob;
import kr.co.wisenut.common.Exception.DBFactoryException;
import kr.co.wisenut.common.Exception.FilterException;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.io.IOUtil;

/**
 *
 * DBJob
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class DBJob extends DBManager  {
    private Connection t_conn;
    private Connection l_conn;
    private ResultSet t_rs;
    private PreparedStatement t_pstmt;
    private Statement t_stmt;
    private DBConnFactory m_dbFactory;
    private int m_mode = 0;
	private QueryGenerator m_queryGen;
	protected static final int DEFAULT_FETCH_SIZE = 500;

    /**
     * DBJob class constructor
     * @param config  Config
     * @param vender dbms vendor
     * @param mode runtime mode
     * @throws DBFactoryException error info
     */
    public DBJob(Config config, int vender, int mode) throws DBFactoryException{
        super(vender, config);
        this.m_mode = mode;
        m_dbFactory = new DBConnFactory(config.getSf1Home(), config.getDataSource());
		m_queryGen = new QueryGenerator();
    }

    /**
     * SET Target Connection
     * @throws DBFactoryException error info
     */
    public void setTargetConnection() throws DBFactoryException{
        t_conn = m_dbFactory.getConnection(m_config.getSource().getTargetDSN());
        try {
            if( m_vender == DBVender.SYMFOWARE){
                t_conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                l_conn = m_dbFactory.getConnection(m_config.getSource().getTargetDSN());
            } else if (m_vender == DBVender.CUBRID || ((m_vender == DBVender.MYSQL || m_vender == DBVender.EDB) && m_mode == IJob.STATIC)){
            	l_conn = m_dbFactory.getConnection(m_config.getSource().getTargetDSN());
            }
            // altibase 에서 blob field select 시 setAutoCommit(false) 시켜야 한다.
            if( m_vender == DBVender.ALTIBASE || m_vender == DBVender.EDB) {
                t_conn.setAutoCommit(false);
            }
            DatabaseMetaData dbm= t_conn.getMetaData();
            Log2.debug("Connect DB  Product Name :"+dbm.getDatabaseProductName(), 3);
            Log2.debug("Connect DB Product Version :"+dbm.getDatabaseProductVersion(), 3);
            Log2.debug("JDBC Driver Name :"+dbm.getDriverName(), 3);
            Log2.debug("JDBC Driver Version :"+dbm.getDriverVersion(), 3);
        } catch (SQLException e) {
            Log2.error("[DBJob] [setResultSet SQL Error Code : "+e.getErrorCode()+" ]");
            Log2.error("[DBJob] [setResultSet SQLException "+e.getMessage()+"]");
            throw new DBFactoryException(": setTargetConnection Error " +e);
        }
    }

    /**
     * Set Log DB Connection
     * @throws DBFactoryException error info
     */
    public void setLogConnection() throws DBFactoryException {
        l_conn = m_dbFactory.getConnection(m_config.getSource().getLogDSN());
    }

    /**
     * Set Target Table ResultSet
     * @param query SQL query string
     * @throws DBFactoryException Error info
     */
    public void setResultSet(String query) throws DBFactoryException {
        setResultSet(query, null);
    }

    /**
     * Set Target Table ResultSet
     * @param query SQL query string
     * @param value parameter value
     * @throws DBFactoryException Error info
     */
    public void setResultSet(String query, String value) throws DBFactoryException {
        t_rs = null;
        Log2.debug("[DBJob]"+" ["+StringUtil.trimDuplecateSpace(query)+"]", 3);
        //if(!characterSet.equals("")) {
            query = StringUtil.convert(query, characterSet);
        //}
        try {
            if(m_vender == DBVender.CUBRID || ((m_vender == DBVender.MYSQL || m_vender == DBVender.EDB) && m_mode == IJob.STATIC)) {
                t_stmt = t_conn.createStatement
                        (ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
                
                // fetchsize must be over 0 in CUBRID DB by joonskwon 2013.09.30
                if (m_vender == DBVender.CUBRID) t_stmt.setFetchSize(DEFAULT_FETCH_SIZE);
                else if(m_vender == DBVender.EDB) t_stmt.setFetchSize(10);
                else t_stmt.setFetchSize(Integer.MIN_VALUE);
                
                if(m_config.getSource().getLogDSN().equals("")) {
                    Log2.error("[DBJob] [DBVender is MYSQL or CUBRID or EDB and runtime mode is static. " +
                            "Missing <DSN type=\"log\"> setting in configuration file.]");
                    return;
                }

                t_rs = t_stmt.executeQuery(query);
            }else{
                t_pstmt = t_conn.prepareStatement(query);
                if(query.indexOf("?") > 0 && value != null) {
                    t_pstmt.setString(1, value);
                }
                t_rs = t_pstmt.executeQuery();
            }
        } catch (SQLException e) {
            Log2.error("[DBJob] [setResultSet SQL Query : "+query+"]");
            Log2.error("[DBJob] [setResultSet SQL Error Code : "+e.getErrorCode()+" ]");
            Log2.error("[DBJob] [setResultSet SQLException "+e.getMessage()+"]");
            throw new DBFactoryException(": setResultSet SQL Error " +e);
        }
    }

    /**
     * Get column value method
     * String[0][0] : column data
     * String[0][1] : column class name
     * @param idx column index number
     * @return String[][]
     * @throws DBFactoryException  error info
     */
    public String[][] getString(int idx) throws DBFactoryException {
        return getString(t_rs, idx);
    }

    /**
     * Get column value method
     * String[0][0] : column data
     * String[0][1] : column class name
     * @param idx  column index number
     * @param count  column index count
     * @return  String[][]
     * @throws DBFactoryException error info
     */
    public String[][] getString(int idx, int count) throws DBFactoryException {
        return getString(t_rs, idx, count);
    }
    
    /**
     * @param idx
     * @param count
     * @return
     * @throws DBFactoryException
     */
    public String[] getStringValue(int idx, int count) throws DBFactoryException {
    	String[][] tmp = getString(t_rs, idx, count);
    	return extractValue(tmp);
    }

    /**
     * GET SubQuery data method
     * String[0][0] : column data
     * String[0][1] : column class name
     * @param query sqlquery
     * @param values parameter values
     * @param seperator result seperator
     * @param isStatement statment or preparestatment
     * @param subMappingMap  subquery column custom class using...
     * @return String[][]
     * @throws DBFactoryException error info
     */
    public String[][] getQueryRsData(String query, String[][] values, String seperator,
                                     boolean isStatement, HashMap subMappingMap) throws DBFactoryException{
        String[][] arrResult ;
        //if(!characterSet.equals("")) {
            query = StringUtil.convert(query, characterSet);
        //}
        if( m_vender == DBVender.SYMFOWARE || m_vender == DBVender.CUBRID || ((m_vender == DBVender.MYSQL || m_vender == DBVender.EDB) && m_mode == IJob.STATIC)) {
            arrResult =  getQueryData(l_conn, query, values, seperator, isStatement, subMappingMap);
        } else {
            arrResult =  getQueryData(t_conn, query, values, seperator, isStatement, subMappingMap);
        }
        return arrResult;
    }

    public String[][] getQueryRsData(String query, String[][] values, String seperator,
                                     boolean isStatement) throws DBFactoryException{
        return getQueryRsData(query, values, seperator, isStatement, null);
    }

    /**
     * Get Recursion result
     * @param query  sqlquery
     * @param values  parameter values
     * @param separator result seperator
     * @param isStatement  statment or preparestatment
     * @return String[][]  Recursion result
     * @throws DBFactoryException error info
     */
    public String[][] getRecRsData(String query, String[][] values, String separator, int count,
                                   boolean isStatement) throws DBFactoryException {
        String[][] arrResult ;
        //if(!characterSet.equals("")) {
            query = StringUtil.convert(query, characterSet);
        //}
        if( m_vender == DBVender.SYMFOWARE || m_vender == DBVender.CUBRID || ((m_vender == DBVender.MYSQL || m_vender == DBVender.EDB) && m_mode == IJob.STATIC)) {
            arrResult =  getRecusionQuery2(l_conn, query, values, separator, count, isStatement);
        } else {
            arrResult =  getRecusionQuery2(t_conn, query, values, separator, count, isStatement);
        }
        return arrResult;
    }

    /**
     * Get Recursion result
     * @param conn dbms connection object
     * @param query  sqlquery
     * @param values  parameter values
     * @param separator result seperator
     * @param count  Recursion count
     * @param isStatement  statment or preparestatment
     * @return Recursion result
     */
    public String getRecusionQuery(Connection conn, String query, String[][] values,
                                   String separator, int count, boolean isStatement) {
        String[][] arrs = getQueryData(conn, query, values, separator, isStatement);
        //if(!characterSet.equals("")) {
            query = StringUtil.convert(query, characterSet);
        //}
        if(arrs != null) {
            int size = arrs.length;
            for(int i=0, j=count; j>0 ; i++, j--) {
                values[i][0] = arrs[size-count+i][0];
                values[i][1] = arrs[size-count+i][1];
            }
        } else {
            return null;
        }

        String result = arrs[0][0];
        if(result.equals("")) {
            return "";
        }
        result = getRecusionQuery(conn, query, values, separator, count, isStatement)
                + result + separator;
        return result;
    }

    /**
     * Get Recursion result
     * @param conn dbms connection object
     * @param query  sqlquery
     * @param values  parameter values
     * @param seperator result seperator
     * @param isStatement  statment or preparestatment
     * @return  String[][]
     */
    public String[][] getRecusionQuery2(Connection conn, String query,
                                        String[][] values, String seperator, int count, boolean isStatement) {
        int maxCnt = 100;
        String[][] result = null;
        String temp = "";
        int size = 0;
        int idx = 0;
        //if(!characterSet.equals("")) {
        	query = StringUtil.convert(query, characterSet);
        //}
        do {
        	String[][] arrs = null;
        	if(isStatement) {
        		String replacedQuery = m_queryGen.addWhereCondition(query, values);
        		arrs = getQueryData(conn, replacedQuery, values, seperator, isStatement);	
        	}else{
        		arrs = getQueryData(conn, query, values, seperator, isStatement);
        	}
            size = arrs.length;
            for(int i=0, j=count; j>0 ; i++, j--) {
                if(values[i][0].equals(arrs[size-count+i][0])) {
                    values[i][0] = "";
                } else {
                    values[i][0] = arrs[size-count+i][0];
                }
                values[i][1] = arrs[size-count+i][1];
                
            }
            if(idx ==0) {
                result = new String[size-count][maxCnt];
            }
            for(int i=0; i<size-count; i++) {
                temp = arrs[i][0];
                result[i][idx] = temp;
            }

            idx++;
        } while( !"".equals(temp));

        String[][] result2 = new String[size-count][2];
        for(int i=0; i<result2.length; i++) {
            result2[i][0] = "";
        }

        if(seperator == null) {
            for(int i=0; i<result2.length; i++) {
                for(int k=0; k < maxCnt; k++) {
                    if( result[i][k] != null && !"".equals(result[i][k]) ) {
                        result2[i][0] = result[i][k];
                        //if(k != 0) result2[i][0] = result2[i][0] + seperator;
                    }
                }
            }
        } else {
            for(int i=0; i<result2.length; i++) {
                for(int k=maxCnt-1; k>=0; k--) {
                    if( result[i][k] != null && !"".equals(result[i][k]) ) {
                        result2[i][0] += result[i][k];
                        if(k != 0) result2[i][0] = result2[i][0] + seperator;
                    }
                }
            }
        }

        return result2;
    }

    /**
     * create CIMS Category method
     * @param query  sqlquery
     * @param values   parameter values
     * @param seperator result seperator
     * @param isStatement  statment or preparestatment
     * @return  String[][]
     */
    public String[][] getCimsCategory(String query, String[][] values,
                                      String seperator, boolean isStatement) {
        //if(!characterSet.equals("")) {
            query = StringUtil.convert(query, characterSet);
        //}
        String[][] arrs = getQueryData(l_conn, query, values, seperator, isStatement);
        StringTokenizer siteToken = null;
        StringTokenizer categoryToken = null;
        if(arrs != null && arrs.length == 2) {
            siteToken = new StringTokenizer(arrs[0][0], " ");
            categoryToken = new StringTokenizer(arrs[1][0], " ");
        }
        String[] site = new String[siteToken.countTokens()];
        String[] category = new String[categoryToken.countTokens()];
        int idx = 0;
        String temp = "";
        while(siteToken.hasMoreElements()){
            boolean duplicate = false;
            temp = siteToken.nextToken();
            for(int j=0 ;  j<=idx; j++) {
                if(site[j] != null && site[j].equals(temp)) {
                    duplicate = true;
                    break;
                }
            }
            if( !duplicate ) {
                site[idx] = temp;
                idx++;
            }
        }

        idx = 0;
        temp = "";
        while(categoryToken.hasMoreElements()){
            boolean duplicate = false;
            temp = categoryToken.nextToken();
            for(int j=0 ;  j<=idx; j++) {
                if(category[j] != null && category[j].equals(temp)) {
                    duplicate = true;
                    break;
                }
            }
            if( !duplicate ) {
                category[idx] = temp;
                idx++;
            }
        }

        String retCategory = "";
        for(int k=0; k<site.length; k++) {
            if(site[k] != null) {
                retCategory += site[k]+":00 " + site[k]+":0 ";
                for(int h=0; h<category.length; h++) {
                    if(category[h] != null) {
                        retCategory += site[k] +":"+category[h].substring(0,1) + " ";
                        retCategory += site[k] +":"+category[h] + " ";
                    }
                }
            }
        }
        String[][] rets = new String[1][2];
        rets[0][0] = retCategory;
        rets[0][1] = "";
        return rets;
    }

    /**
     * Get Attach file info method
     * @param query   sqlquery
     * @param values  parameter values
     * @param seperator result seperator
     * @param useCnt using attach count
     * @param type  filtering type
     * @param preFix filtered path
     * @param isStatement statment or preparestatment
     * @return Filtering AttachInfo
     * @throws FilterException  error info
     * @throws DBFactoryException  error info
     */
    public AttachInfo getAttachInfo(String query, String[][] values, String seperator,
                                    int useCnt, String type, String preFix, boolean isStatement)
            throws FilterException, DBFactoryException {
        AttachInfo attInfo ;
        //if(!characterSet.equals("")) {
            query = StringUtil.convert(query, characterSet);
        //}
        if(type.equals("blob")) { //if binary large object type
            String[] nameList = getBolbFileNames(query, values,  preFix, isStatement);
            attInfo = new AttachInfo(0, 1);
            for(int i=0; i<nameList.length; i++) {
                attInfo.addAttach(0, nameList[i]); //bug fix
            }
        } else {
            String[][] results ;
            //= getQueryData2(t_conn, query, values, seperator,useCnt, isStatement);
            if( m_vender == DBVender.SYMFOWARE || m_vender == DBVender.CUBRID || ((m_vender == DBVender.MYSQL || m_vender == DBVender.EDB) && m_mode == IJob.STATIC)) {
                results
                        = getQueryData2(l_conn, query, values, seperator,useCnt, isStatement);
            } else {
                results
                        = getQueryData2(t_conn, query, values, seperator,useCnt, isStatement);
            }

            if(results != null) {
                int size = results.length;
                int scdFileCnt = (size-useCnt);
                if (scdFileCnt < 0) scdFileCnt = 0;
                attInfo = new AttachInfo(scdFileCnt, useCnt);
                for(int i=0, j=0; i<size; i++) {
                    if( i >= scdFileCnt ) {
                        String[] atts = results[i];
                        for(int k=0; k<atts.length; k++) {
                            if(atts[k] != null && !atts[k].equals("")) {
                                attInfo.addAttach(j, atts[k]);
                            }
                        }
                        j++;     // j is Vector[idx] index
                    } else {
                        attInfo.setColunmData(i, results[i][0]);
                    }
                }
            } else {
                int scdFileCnt = (getColumnCnt()-useCnt);
                if (scdFileCnt < 0) scdFileCnt = 0;
                attInfo = new AttachInfo(scdFileCnt, useCnt);
            }
        }

        return attInfo;
    }

    /**
     * Get BLOB Attach file name method
     * Not support mysql blob type
     * @param query sql query string
     * @param values prameter values
     * @param preFix  blob file path
     * @param isStatement statement or preparestatement
     * @return  attach file names
     * @throws DBFactoryException error info
     */
    public String[] getBolbFileNames(String query, String[][] values, String preFix,
                                     boolean isStatement) throws DBFactoryException {
        //if(!characterSet.equals("")) {
            query = StringUtil.convert(query, characterSet);
        //}
        Vector vt;
        if(m_vender == DBVender.SYMFOWARE || m_vender == DBVender.CUBRID || ((m_vender == DBVender.MYSQL || m_vender == DBVender.EDB) && m_mode == IJob.STATIC)) {
            vt = blobToFile(l_conn, query, values, preFix, isStatement);
        } else {
            vt = blobToFile(t_conn, query, values, preFix, isStatement);
        }
        String[] list = new String[vt.size()];
        for(int i=0; i<vt.size(); i++) {
            list[i] = (String) vt.get(i);
        }
        return list;
    }

    /**
     * Target ResultSet next()
     * @return boolean
     */
    public boolean next() {
        return next(t_rs);
    }

    /**
     * Taget ResultSet column count
     * @return int
     */
    public int getTargetColumnCnt() {
        return getColunmCnt(t_rs);
    }

    /**
     * Get connect by result method
     * @param query sql query string
     * @param values  parameter values
     * @param maxCollNum max column count
     * @param isStatement statment or pareparestatment
     * @return  String[][]  connect by result
     */
    public String[][] getConnectBy(String query, String[][] values, int maxCollNum,
                                   boolean isStatement) {
        //if(!characterSet.equals("")) {
            query = StringUtil.convert(query, characterSet);
        //}
        String[][] arrList ;
        if( m_vender == DBVender.SYMFOWARE || m_vender == DBVender.CUBRID || ((m_vender == DBVender.MYSQL || m_vender == DBVender.EDB) && m_mode == IJob.STATIC)) {
            arrList = getRsToArrList(l_conn, query, values, maxCollNum, isStatement);
        } else {
            arrList = getRsToArrList(t_conn, query, values, maxCollNum, isStatement);
        }
        int rowCount = arrList[0].length;
        int retLen = arrList.length;
        String[][] retList = new String[rowCount * retLen][1];
        int idx = 0;
        for(int i=0; i<arrList.length; i++) {
            for(int k=0; k<arrList[i].length; k++) {
                retList[idx][0] = arrList[i][k];
                idx++;
            }
        }
        return retList;
    }

    public int execPQuery(String query) throws SQLException, DBFactoryException {
        //if(!characterSet.equals("")) {
            query = StringUtil.convert(query, characterSet);
        //}
        int ret ;
        if( m_vender == DBVender.SYMFOWARE || m_vender == DBVender.CUBRID || ((m_vender == DBVender.MYSQL || m_vender == DBVender.EDB) && m_mode == IJob.STATIC)) {
            ret = executePQuery(l_conn, query, null, false);
        } else {
            ret = executePQuery(t_conn, query, null, false);
        }

        return ret;
    }

    public int execPQuery(String query, String[][] values, boolean isStatement)
            throws SQLException, DBFactoryException {
        //if(!characterSet.equals("")) {
            query = StringUtil.convert(query, characterSet);
        //}
        int ret ;
        if( m_vender == DBVender.SYMFOWARE || m_vender == DBVender.CUBRID || ((m_vender == DBVender.MYSQL || m_vender == DBVender.EDB) && m_mode == IJob.STATIC)) {
            ret = executePQuery(l_conn, query, values, isStatement);
        } else {
            ret = executePQuery(t_conn, query, values, isStatement);
        }
        return ret;
    }

    public int execPQueryLog(String query)
            throws SQLException, DBFactoryException {
        int ret = 0;
        if(l_conn == null) {
            debug("[DBJob] [Log DB Connection is null]",2);
            return 0;
        }
        if(query != null && !query.equals("")) {
            //if(!characterSet.equals("")) {
                query = StringUtil.convert(query, characterSet);
            //}
            ret = executePQuery(l_conn, query, null, false);
        } else {
            debug("[DBJob] [Log Query is Empty]",2);
        }
        return ret;
    }

    public int execPQueryLog(String query, String[][] values, boolean isStatement)
            throws SQLException, DBFactoryException {
        if(l_conn == null) {
            debug("[DBJob] [Log DB Connection is null]", 3);
            return 0;
        }
        //if(!characterSet.equals("")) {
            query = StringUtil.convert(query, characterSet);
        //}
        return executePQuery(l_conn, query, values, isStatement);
    }

    /**
     * release resource
     */
    public void releaseDB(){
        releaseDB(t_rs, t_pstmt);
        releaseDB(t_rs, t_stmt);
        releaseDB(t_conn);
        releaseDB(l_conn);
    }

    public void releaseRs() {
        releaseDB(t_rs, t_stmt);
        releaseDB(t_rs, t_pstmt);
    }

    public void execCommit() throws SQLException{
        // should be autocommit false.
        // (ex) altibase blog field select
        // queue table commit. by jwlee
        if(m_vender == DBVender.ALTIBASE && !t_conn.getAutoCommit()) {
            t_conn.commit();
        }
    }

    /**
     * SumMemory's hashmap return function
     * @param memoryTable
     * @param debugFilePath fileyn="y" . (using debug)
     * @return
     * @throws IOException
     */
	public HashMap executeMemoryTableQuery(MemoryTable memoryTable, String debugFilePath) throws IOException {
		
		HashMap resultMap = new HashMap();
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		long useBytes = 0;
		
		BufferedWriter bw = null;

		try {
			stmt = t_conn.createStatement();
			rs = stmt.executeQuery(memoryTable.getSql());
			int columnCnt = getColunmCnt(rs);

			// field check 쿼리 컬럼개수로 keycount 값과 usecount 값이 정상인지 체크한다.
			if (columnCnt != (memoryTable.getKeyCount() + memoryTable.getUseCount())) {
				throw new IllegalArgumentException(
						"column count of (keycount+usecount) does not match. MemoryTable order=\"" + memoryTable.getId() + "\"");
			}
			int rowCnt = 0;
			while (rs.next()) {
				String key = "";
				
				// 1차 scd field 별, 2차 field 하나당 n 개 데이터 (조건에 맞는 서브데이터 row수만큼)
				ArrayList[] value = null ;	
				for (int i = 0; i < columnCnt; i++) {
					
					// key join
					if(i == 0) {
						String[] keyArr = new String[memoryTable.getKeyCount()];						
						for(int keyIdx=0;keyIdx<memoryTable.getKeyCount();keyIdx++) {
							keyArr[keyIdx] = getString(rs, keyIdx + 1)[0][0];
						}
						key = StringUtil.join(keyArr, memoryTable.getKeySeperator());
						i = i + memoryTable.getKeyCount() - 1;
						
						if(!resultMap.containsKey(key)) {
							value = new ArrayList[memoryTable.getUseCount()];
							resultMap.put(key, value);
							useBytes = useBytes + key.getBytes().length;
						}
						continue;
					}
					
					String dataField = getString(rs, i + 1)[0][0];

					int dataIdx = i - memoryTable.getKeyCount();
					// data
					value = (ArrayList[]) resultMap.get(key);
					
					useBytes = useBytes + dataField.getBytes().length;
					
					if(value[dataIdx] == null) {
						value[dataIdx] = new ArrayList();
						value[dataIdx].add(dataField);
					}else{
						// 세로데이터 구분자 이용해서 append
						value[dataIdx].add(dataField);
					}
				}
				
				rowCnt++;
				
				if(rowCnt % 1000 == 0) {
					Log2.out("[info] [DBJob] [" + rowCnt + " count loading...]");
				}
			}
			
			if(rowCnt % 1000 != 0) {
				Log2.out("[info] [DBJob] [" + rowCnt + " count loaded.]");
			}
			
			// file dump
			if (memoryTable.isFileYn()) {
				Log2.out("[info] [DBJob] [Write SubMemory debug file. path : " + debugFilePath + "]");
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(debugFilePath)), "UTF-8"), 1024);
				
				Set keySet = resultMap.keySet();
				Iterator keyIter = keySet.iterator();
				while (keyIter.hasNext()) {
					String k = (String) keyIter.next();
					ArrayList[] v = (ArrayList[])resultMap.get(k);
					
					String line = k;
					for (int j = 0; j < v.length; j++) {
						ArrayList rows = (ArrayList)v[j];
						String[] rowArr = (String[])rows.toArray(new String[rows.size()]);
						line = line + "\t" + StringUtil.join(rowArr,  "!&!");
					}
					
					
					bw.write(line + "\n");
					bw.flush();
				}
			}
			
			Log2.out("[info] [DBJob] [MemoryTableMap " + useBytes + " bytes loaded.]");
			
		} catch (SQLException e) {
			Log2.error("[DBJob] [SQL Error Code : " + e.getErrorCode()
					+ " ]");
			Log2.error("[DBJob] [SQL Error Query : " + memoryTable.getSql()
					+ "]");
			Log2.error("[DBJob] [DBFactoryException" + "\n"
					+ IOUtil.StackTraceToString(e) + "\n]");
		} catch (DBFactoryException e) {
			Log2.error("[DBJob] [DBFactoryException" + "\n"
					+ IOUtil.StackTraceToString(e) + "\n]");
		} finally {
			if(bw != null) {
				bw.flush();
				bw.close();
			}
			releaseDB(rs, pstmt);
			releaseDB(rs, stmt);
		}
		return resultMap;
	}

	/**
	 * 값,데이터타입 구조의 배열에서 값구조의 배열로 반환
	 * @param rsData
	 * @return
	 */
	private String[] extractValue(String[][] rsData) {
		String[] values = new String[rsData.length];
		for (int i = 0; i < rsData.length; i++) {
			values[i] = rsData[i][0];
		}
		return values;
	}
	

}
