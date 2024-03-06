/*
 * @(#)DBManager.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.db;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import kr.co.wisenut.bridge3.config.Config;
import kr.co.wisenut.bridge3.config.datasource.DataSource;
import kr.co.wisenut.bridge3.job.CFactory;
import kr.co.wisenut.common.Exception.DBFactoryException;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.io.IOUtil;

/**
 *
 * DBManager
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public abstract class DBManager implements IDBManager {
    private boolean isError = false;
    protected int m_vender;
    protected DBColumnData m_columndata;
    private int columnCnt = 0;
    protected Config m_config;
    protected String characterSet = "";


    /**
     * DBManager class constructor
     * @param vender dbms vendor
     * @param config  Config object
     * @throws DBFactoryException error info
     */
    public DBManager(int vender, Config config) throws DBFactoryException{
        this.m_config = config;
        this.m_vender = vender;
        this.m_columndata = new DBColumnData();
        HashMap dscMap = config.getDataSource();
        DataSource m_datasource = (DataSource) dscMap.get(m_config.getSource().getTargetDSN());
        if(m_datasource == null) {
            throw new DBFactoryException(": Unable to read the datasource name" +
                    "setting in configuration file. dsn name("+m_config.getSource().getTargetDSN()+")");
        }
        this.characterSet = m_datasource.getChar_set();
        if(!characterSet.equals(""))
            Log2.debug("[DBManager] [database convert character set "+characterSet+"]", 3);
    }

    public int getColumnCnt() {
        return columnCnt;
    }

    public void setColumnCnt(int columnCnt) {
        this.columnCnt = columnCnt;
    }

    /**
     * Call ResultSet�쓽 next()
     * @param rs  ResultSet
     * @return boolean
     */
    public boolean next(ResultSet rs) {
        boolean isBool;
        if(rs == null) {
            return false;
        }
        try {
            isBool = rs.next();
        } catch (SQLException e) {
            Log2.error("[DBManager] [next() SQL Error Code : "
                    +e.getErrorCode()+" ]");
            Log2.error("[DBManager] [next() SQLException "
                    +IOUtil.StackTraceToString(e)+"\n]");
            isBool = false;
            isError = true;
            return isBool;
        }
        return isBool;
    }

    /**
     * Get ResultSet column count
     * @param rs ResultSet
     * @return int column count
     */
    public int getColunmCnt(ResultSet rs) {
        int num = 0;
        try {
            if(rs != null) {
                num = rs.getMetaData().getColumnCount();
            }
            //ResultSetMetaData Meta = rs.getMetaData();
            //num = Meta.getColumnCount();
        } catch (SQLException e) {
            Log2.error("[DBManager] [getColunmCnt SQL Error Code : "
                    +e.getErrorCode()+" ]");
            Log2.error("[DBManager] [getColunmCnt SQLException "
                    +e.getMessage()+"\n"+IOUtil.StackTraceToString(e)+"\n]");
        }
        return num;
    }

    /**
     * set ResultSet abstract method
     * @param query sql query string
     * @throws DBFactoryException error info
     */
    public abstract void setResultSet(String query) throws DBFactoryException;


    /**
     * Sql query execute method
     * @param conn  connection object
     * @param query  sql query string
     * @param values parameter values
     * @param isStatement statement or preparestatement
     * @return success or fail
     * @throws SQLException error info
     * @throws DBFactoryException  error info
     */
    public int executePQuery(Connection conn, String query, String[][] values, boolean isStatement)
            throws SQLException, DBFactoryException {
        debug("[DBManager] ["+query+"]", 3);
        PreparedStatement pstmt ;
        Statement stmt ;
        int ret = -1;
        if(isStatement){
            stmt = conn.createStatement();
            query = getStmtQuery(query, values);
            ret = stmt.executeUpdate(query);
            releaseDB(stmt);
        }else{
            pstmt = conn.prepareStatement(query);
            if( values != null){
                pstmt = setPstmt(pstmt, values);
            }
            ret = pstmt.executeUpdate();
            releaseDB(pstmt);
        }

        return ret;
    }

    /**
     * Get Resultset column data method
     * @param rs  ResultSet
     * @param idx   column index
     * @param count limit count
     * @return String[][] result
     * @throws DBFactoryException error info
     */
    public String[][] getString(ResultSet rs, int idx, int count) throws DBFactoryException {
        String[][] coldata = new String[count][2];
        for(int i=0; i<count; i++){
            String[][] arr = getString(rs, idx);
            coldata[i][0] = arr[0][0];
            coldata[i][1] = arr[0][1];
            idx++;
        }
        return coldata;
    }

    /**
     * Get Resultset column data method
     * @param rs ResultSet
     * @param idx column index
     * @return String[][] result
     * @throws DBFactoryException error info
     */
    public String[][] getString(ResultSet rs, int idx) throws DBFactoryException {
        debug("[DBManager] [ResultSet Column Index(" + idx + ")]", 4);
        String[][] coldata = new String[1][2];
        ResultSetMetaData meta ;
        try {
            meta = rs.getMetaData();
            String classType = meta.getColumnTypeName(idx).toLowerCase();
            String className = "";
            if(m_vender == DBVender.SYMFOWARE ) {
                className = getSymfowareClassType(classType);
            }else {
                className = meta.getColumnClassName(idx); //Not supported method at symfoware
            }
            if(classType.equals("char")) className = "java.lang.String";
            className = getClassName(className);
            coldata[0][1] = className;
            if (classType.equals("text") && (m_vender == DBVender.INFORMIX )) {
                coldata[0][0] = inputStreamToString( rs.getAsciiStream(idx) );
            } else{
                Object object = rs.getObject(idx);
                if(object != null) {
                    coldata[0][0] = ( m_columndata.getColumnData( object, classType) );
                    if(!characterSet.equals("")) {
                        String columnData = m_columndata.getColumnData( object, classType);
                        coldata[0][0]
                                = StringUtil.convert(columnData, characterSet);
                    }
                } else {
                    Log2.debug("[DBManager] [rs.getObject("+idx+"), " +
                            "columnName=\""+meta.getColumnName(idx)+"\", " +
                            "className=\""+className+"\" is null]", 4);
                }
            }
        } catch (SQLException e) {

            Log2.error("[DBManager] [SQL Error Code : "+e.getErrorCode()+" ]");
            Log2.error("[DBManager] [SQLException][GetString() column index ("+idx+") ]");
            Log2.error("[DBManager] [SQLException][ "
                    +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        }
        return StringUtil.checkNull( coldata );
    }
    
    
    public String[][] getQueryData(Connection conn, String query, String[][] values,
            String seperator, boolean isStatement) {
    	return getQueryData(conn, query, values, seperator, isStatement, null);
    }

    /**
     * Get subquery data method
     * @param conn  connection object
     * @param query  sql query string
     * @param values  parameter values
     * @param seperator result seperator
     * @param isStatement statement or preparestatement
     * @return  String[][] subquery data
     * @return  HashMap subMappingMap
     */
    public String[][] getQueryData(Connection conn, String query, String[][] values,
                                   String seperator, boolean isStatement, HashMap subMappingMap) {
        debug("[DBManager] ["+query+"]", 3);
        String[][] retArrs = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            if(isStatement){
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
            }else{
                pstmt = conn.prepareStatement(query);
                pstmt = setPstmt(pstmt, values);
                rs = pstmt.executeQuery();
            }
            int columnCnt = getColunmCnt(rs);
            retArrs = new String[columnCnt][2];
            for(int k=0; k<columnCnt; k++) {
                retArrs[k][0] = "";
                retArrs[k][1] = "";
            }
            int rowCnt = 0;
			while (rs.next()) {
				for (int i = 0; i < columnCnt; i++) {
					String[][] arrTmp = getString(rs, i + 1);

					// <SubCustomClass> check
					if (subMappingMap != null) {
						String className = (String) subMappingMap.get(String.valueOf(i + 1));
						if (className != null) {
							arrTmp[0][0] = applySubCustomClass(className, arrTmp[0][0]);
						}
					}

					if (rowCnt > 0) {
						retArrs[i][0] += seperator;
					}
					retArrs[i][0] += arrTmp[0][0];
					retArrs[i][1] += arrTmp[0][1];
				}
				rowCnt++;
			}
        } catch (SQLException e) {
            Log2.error("[DBManager] [SQL Error Code : "+e.getErrorCode()+" ]");
            Log2.error("[DBManager] [SQL Error Query : "+query+"]");
            Log2.error("[DBManager] [DBFactoryException"
                    +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        } catch (DBFactoryException e) {
            Log2.error("[DBManager] [DBFactoryException"
                    +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        } finally {
            releaseDB(rs, pstmt);
            releaseDB(rs, stmt);
        }
        if(retArrs == null) {
        	return null;
        }else{
        	return StringUtil.checkNull(retArrs);
        }
    }

    /**
     * className data return
     * @param className
     * @param data
     * @return
     */
	private String applySubCustomClass(String className, String data) {
		try {
			data = CFactory.getInstance(className).customData(data);
		} catch (Exception e) {
			Log2.error("[getQueryData] [SubCustomClass error:" + e.toString() + "]");
		}
		return data;
	}


    /**
     * Get subquery data method
     * @param conn  connection object
     * @param query  sql query string
     * @param values  parameter values
     * @param seperator result seperator
     * @param useCnt using result count
     * @param isStatement statement or preparestatement
     * @return  String[][] subquery data
     */
    public String[][] getQueryData2(Connection conn, String query, String[][] values, String seperator,
                                    int useCnt, boolean isStatement) {
        int maxCnt = 100; //maximum attach file count is 100
        debug("[DBManager] ["+query+"]", 3);
        String[][] newRetArrs = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if(values.length >0 && !values[0][0].equals("")) {
                if(isStatement){
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(query);
                }else{
                    pstmt = conn.prepareStatement(query);
                    pstmt = setPstmt(pstmt, values);
                    rs = pstmt.executeQuery();
                }
                int columnCnt = getColunmCnt(rs);
                setColumnCnt(columnCnt);
                if(columnCnt < useCnt) {
                    throw new DBFactoryException(":useCnt is larger than Attach ResultSet count." +
                            "Please check the <Filter useCnt> setting in configuration file.");
                }
                String[][] retArrs = new String[columnCnt][maxCnt];
                int rowCnt = 0;
                while(rs.next() && rowCnt < maxCnt) {
                    for(int i=0; i<columnCnt; i++) {
                        String columnData = getString(rs, i+1)[0][0];
                        if(i >= columnCnt-useCnt) {    // attach filtering info
                            retArrs[i][rowCnt] = columnData;
                        } else {
                            if(rowCnt > 0) {
                                retArrs[i][0] += seperator;
                                retArrs[i][1] += seperator;
                            }
                            String[][] arrTmp = getString(rs, i+1);
                            if(rowCnt == 0) {
                                retArrs[i][0] = arrTmp[0][0];
                                retArrs[i][1] = arrTmp[0][1];
                            } else {
                                retArrs[i][0] += arrTmp[0][0];
                                retArrs[i][1] += arrTmp[0][1];
                            }
                        }
                    }
                    rowCnt++;
                }

                if(rowCnt > 0 && columnCnt > 0)   {
                    newRetArrs = new String[columnCnt][rowCnt];
                    for(int i=0; i<columnCnt; i++) {
                        for(int j=0; j<rowCnt; j++) {
                            if(retArrs[i][j] != null){
                                newRetArrs[i][j] = retArrs[i][j];
                            }
                        }
                    }
                }
            } else {
                Log2.debug("[DBManager] [FilterInfo: Where Condition.length = 0]", 3);
            }
        } catch (SQLException e) {
            Log2.error("[DBManager] [SQL Error Code : "+e.getErrorCode()+" ]");
            Log2.error("[DBManager] [SQL Error Query : "+query+"]");
        } catch (DBFactoryException e) {
            Log2.error("[DBManager] [DBFactoryException"
                    +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        } finally {
            releaseDB(rs, pstmt);
            releaseDB(rs, stmt);

            return newRetArrs;
        }
    }

    /**
     * Using getConnectBy method
     * @param conn  connection object
     * @param query  sql query string
     * @param values parameter values
     * @param maxCount limit count
     * @param isStatement statement or preparestatement
     * @return String[][]
     */
    public String[][] getRsToArrList(Connection conn, String query, String[][] values,
                                     int maxCount, boolean isStatement) {
        debug("[DBManager] ["+query+"]", 3);
        String[][] retArrs = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if(values.length >0 && !values[0][0].equals("")) {
                if(isStatement){
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(query);
                }else{
                    pstmt = conn.prepareStatement(query);
                    pstmt = setPstmt(pstmt, values);
                    rs = pstmt.executeQuery();
                }
                int columnCnt = getColunmCnt(rs);
                retArrs = new String[maxCount][columnCnt];
                for(int i=0; i<maxCount; i++) {
                    for(int k=0; k<columnCnt; k++) {
                        retArrs[i][k] = "";
                    }
                }
                int count = 0;
                while(rs.next() && count < maxCount) {
                    for(int i=0; i<columnCnt; i++) {
                        retArrs[count][i] = getString(rs, i+1)[0][0];
                    }
                    count++;
                }
            } else {
                Log2.debug("[DBManager] [SubQuery where count length = 0]", 3);
            }
        } catch (SQLException e) {
            Log2.error("[DBManager] [SQL Error Code : "+e.getErrorCode()+"]");
            Log2.error("[DBManager] [SQL Error Query : "+query+"]");
        } catch (DBFactoryException e) {
            Log2.error("[DBManager] [DBFactoryException"
                    +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        } finally {
            releaseDB(rs, pstmt);
            releaseDB(rs, stmt);
            return StringUtil.checkNull(retArrs);
        }
    }


    /**
     * bind variable method
     * @param pstmt PreparedStatement
     * @param values parameter values
     * @return PreparedStatement
     * @throws DBFactoryException error info
     * @throws SQLException error info
     */
    protected PreparedStatement setPstmt(PreparedStatement pstmt, String[][] values)
            throws DBFactoryException, SQLException {
        for(int i=0; values != null && i<values.length  ; i++) {
            String value = values[i][0];
            String type = values[i][1];
            debug("[DBManager] [pstmt set value is '"+value+"', type is '"+type+"']", 3);
            if((!type.equals("java.lang.String") && (value == null || value.equals("")))) {
            	pstmt.setString(i+1, "");
            	Log2.debug("[SubQuery] [sql bind variable value(not String Type) is null or empty : Please check SubQuery.]", 2);
            }else if(type.equals("java.lang.Integer")) {
                pstmt.setInt(i+1, Integer.parseInt(value));
            } else if(type.equals("java.lang.Long") || type.equals("java.math.BigInteger")) {
                pstmt.setLong(i+1, Long.parseLong(value));
            } else if(type.equals("java.lang.Short")) {
                pstmt.setShort(i+1, Short.parseShort(value));
            } else if(type.equals("java.lang.Double")) {
                pstmt.setDouble(i+1, Double.parseDouble(value));
            } else if(type.equals("java.math.BigDecimal")) {    //java.math.BigDecimal
                pstmt.setBigDecimal(i+1, BigDecimal.valueOf(Long.parseLong(value)));
            } else if(type.equals("java.lang.String")) {
                pstmt.setString(i+1, value);
            } else {
                throw new DBFactoryException(": DBManager Class - " +
                        "Not Used Type in PreparedStatement ("+type+")");
            }
        }
        debug("[DBManager] [pstmt set end(success)]", 3);
        return pstmt;
    }



    private static String[] className = new String[]{"Integer","Short", "Long","Double","BigDecimal","String"};

    /**
     *
     * @param classNm dbms classname
     * @return java ClassName
     */
    private String getClassName(String classNm) {
        String ret = classNm;
        for(int i=0;i<className.length; i++) {
            if(classNm.equals(className[i])) {
                if(classNm.equals("BigDecimal")) {
                    ret  = "java.math.BigDecimal";
                }else{
                    ret = "java.lang."+classNm;
                }
                break;
            }
        }
        return ret;
    }

    /**
     * Using symfoware dbms
     * @param classType symfoware class type
     * @return java class type
     */
    private String getSymfowareClassType(String classType) {
        String className = "";
        if(classType.equals("integer")) {
            className = "java.lang.Integer";
        }else if(classType.indexOf("char") > -1) {
            className = "java.lang.String";
        }else if(classType.equals("long")) {
            className = "java.lang.Long";
        }else if(classType.equals("decimal")) {
            className = "java.math.BigDecimal";
        }else if(classType.equals("smallint")) {
            className = "java.lang.Short";
        }else if(classType.equals("double")) {
            className = "java.lang.Double";
        }
        return className;
    }

    /**
     *
     * @param query sql query string
     * @param values  parameter values
     * @return  String  statement query string
     * @throws DBFactoryException error info
     * @throws SQLException  error info
     */
    protected String getStmtQuery(String query, String[][] values)
            throws DBFactoryException, SQLException{
        for(int i=0; values != null && i<values.length  ; i++) {
            String value = values[i][0];
            String type = values[i][1];
            if(type.equals("java.lang.String")) {
                query = StringUtil.replace(query, "%s"+(i+1), "'"+value+"'" );
            }else{
                query = StringUtil.replace(query, "%s"+(i+1),value );
            }
        }
        return query;
    }

    /**
     * Clob type processing method
     * @param in InputStream
     * @return String result string
     */
    public String inputStreamToString(InputStream in) {
        String retStr = "";
        if(in == null) return retStr;
        try {
            int size = in.available();
            byte[] buf = new byte[size];
            int n = in.read(buf);
            retStr = new String(buf).trim();
        } catch (Exception e) {
            Log2.error("[DBManager] [Occur during reading stream error : "
                    +"\n"+IOUtil.StackTraceToString(e)+"\n]");
            return retStr;
        }
        return retStr;
    }

    /**
     * blob file list return method
     * @param conn  connection object
     * @param query  sql query string
     * @param values  parameter values
     * @param preFix blob file path
     * @param isStatement statement or preparestatement
     * @return Vector result
     * @throws DBFactoryException error info
     */
    public Vector blobToFile(Connection conn, String query, String[][] values,
                             String preFix, boolean isStatement) throws DBFactoryException {
        String dbFileName = "";
        String retFileName = "";
        int cnt = 0;
        Vector vtRet = new Vector();
        debug("[DBManager] ["+query+"]", 3);
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if(isStatement){
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
            }else{
                pstmt = conn.prepareStatement(query);
                pstmt = setPstmt(pstmt, values);
                rs = pstmt.executeQuery();
            }
            ResultSetMetaData Meta = rs.getMetaData();
            cnt = Meta.getColumnCount();
            if(cnt != 2){
                String msg = "DBManager.BlobToFile Please Check  Blob select query\n";
                msg +=  "ex) SELECT filename, blob_content FROM table_name";
                throw new DBFactoryException(msg);
            }
            while(rs.next()) {
                for(int i=1;i<=cnt;i++) {
                    String className = Meta.getColumnClassName(i);
                    if(i == 1 && className.equals("java.lang.String")){
                        dbFileName = StringUtil.getTimeBasedUniqueID()+rs.getString(i);
                    } else if( i == 2 ) {
                        Log2.debug("[DBManager] [DBManager  blobToFile...]", 4);
                        InputStream is = rs.getBinaryStream(i);

                        if(is == null) {
                        	Log2.debug("[DBManager] [Blob data is Empty.]");
                        }

                        retFileName = FileUtil.inputStreamToFile(preFix,dbFileName, is);
                        vtRet.add(retFileName);
                    } else{
                        throw new DBFactoryException(": DBManager Class Useing Table Struct : file_name(string), file_content(blob)");
                    }
                }
            }
        } catch (SQLException e) {
            Log2.error("[DBManager] [SQL Error Code : "+e.getErrorCode()+" ]");
            Log2.error("[DBManager] [SQLException][ "
                    +"\n"+IOUtil.StackTraceToString(e)+"\n]");
            throw new DBFactoryException(": Make blobToFile error :" + e.getMessage());
        } finally {
            releaseDB(rs, pstmt);
            releaseDB(rs, stmt);
            return vtRet;
        }
    }

    /**
     * check error method
     * @return boolean
     */
    public boolean isError() {
        return isError;
    }

    /**
     * debug message method
     * @param msg debug message
     */
    protected void debug(String msg){
        Log2.debug(msg, 4);
    }
    protected void debug(String msg, int level) {
        Log2.debug(msg, level);
    }
    protected void error(String err) {
        Log2.error(err);
    }
    protected void error(Exception e) {
        Log2.error(e);
    }
    /**
     * Connection Release
     * @param conn Connection
     */
    public void releaseDB(Connection conn){
        if(conn != null) {
            try{
                conn.close();
            }catch(Exception e){
                Log2.error("[DBManager] [releaseDB "+e.getMessage()+"]");
            }
        }
    }

    public void releaseDB(ResultSet rs, Statement stmt) {
        releaseDB(rs);
        if (! (m_vender == DBVender.SYBASE )) {
            releaseDB(stmt);
        }
    }

    public void releaseDB(ResultSet rs, PreparedStatement pstmt) {
        releaseDB(rs);
        if (! (m_vender == DBVender.SYBASE )) {
            releaseDB(pstmt);
        }
    }

	public void releaseDB(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				if (m_vender != DBVender.ACCESS) {
					Log2.error("[DBManager] [releaseDB " + e.getMessage() + "]");
				}
			}
		}
	}


    public void releaseDB(Statement stmt){
        if(stmt != null) {try{ stmt.close(); }catch(Exception e){Log2.error("[DBManager] [releaseDB "+e.getMessage()+"]");}}
    }

    public void releaseDB(PreparedStatement pstmt){
        if(pstmt != null) {try{ pstmt.close(); }catch(Exception e){Log2.error("[DBManager] [releaseDB "+e.getMessage()+"]");}}
    }
}
