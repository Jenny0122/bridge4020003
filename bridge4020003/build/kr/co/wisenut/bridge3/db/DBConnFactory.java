/*
 * @(#)DBConnFactory.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.bridge3.db;

import kr.co.wisenut.bridge3.config.datasource.DataSource;
import kr.co.wisenut.bridge3.config.datasource.Dsn;
import kr.co.wisenut.common.Exception.DBFactoryException;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.EncryptUtil;
import kr.co.wisenut.common.util.io.IOUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Properties;
import java.util.Collection;
import java.util.Iterator;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.File;

/**
 *
 * DBConnFactory
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 * @version 3.8,3. 2009/10/26 Bridge Release
 */
public class DBConnFactory extends DBVender {
    private HashMap m_dsMap;
    protected String JDBC_ROOT = "";

    public DBConnFactory(String sf1_home, HashMap dsMap){
        m_dsMap = dsMap;
        
        /*JDBC_ROOT = FileUtil.lastSeparator(m_wcse_home)
            + "bridge" + FileUtil.getFileSeperator()+ "jdbc" + FileUtil.getFileSeperator();*/

        //jdbc folder path is sf1_home/lib.
        // 2011.04.29
        //modified version 3.8.4
        JDBC_ROOT = FileUtil.lastSeparator(sf1_home)
            + "lib" + FileUtil.getFileSeperator()+ "jdbc" + FileUtil.getFileSeperator();
    }

    public Connection getConnection(String dsn) throws DBFactoryException {
        Log2.out("[info] [DBConnFactory ] [Get DB Connection]");
        Connection conn ;
        DataSource dsource = (DataSource) m_dsMap.get(dsn);
        if(dsource == null) {
            throw new DBFactoryException(":Missing DataSource setting in datasource.xml. dsn name("+dsn+")");
        }
        Dsn _dsn = getDsn(dsource);
        if(_dsn == null) {
            throw new DBFactoryException(":Missing DSN setting in datasource.xml. dsn name("+dsn+")");
        }

        if(dsource.getVender() == KDB_APP) {
            com.dbapp.DBConnFactory kdb_factory = new com.dbapp.DBConnFactory();
            try  {
                conn = kdb_factory.getConnection(dsource.getServerName());
            } catch (Exception e) {
                throw new DBFactoryException(": KDB App connection fail."
                        +"\n"+IOUtil.StackTraceToString(e));
            }
       /* }else if(dsource.getVender() == POSTGRE
                || dsource.getVender() == ACCESS
                || dsource.getVender() == SYMFOWARE) {
            try  {
                Class.forName(_dsn.getDriver());
                conn = DriverManager.getConnection(_dsn.getUrl(), _dsn.getPrps());
            } catch (Exception e) {
                throw new DBFactoryException(": DB connection fail."
                        +"\n"+IOUtil.StackTraceToString(e));
            }*/
        } else {
            Log2.debug("[DBConnFactory ] [JDBC Connection]", 4);
            Log2.debug("[DBConnFactory ] [[user]"+_dsn.getPrps().getProperty("user")
                    +" [pwd]"+_dsn.getPrps().getProperty("password")+"]", 4);
            try {
                Class DBMSClass = _dsn.getDbms();
                Driver instance;
                instance = (Driver)DBMSClass.newInstance();
                conn = instance.connect(_dsn.getUrl(), _dsn.getPrps());
            } catch (SQLException se) {
                throw new DBFactoryException(": DB connection fail. "
                        +"\n"+IOUtil.StackTraceToString(se));
            } catch (InstantiationException ne) {
                throw new DBFactoryException(": Unable to load the JDBC Driver. "
                        +"\n"+IOUtil.StackTraceToString(ne));
            } catch (IllegalAccessException e) {
                throw new DBFactoryException(": JDBC IllegalAccessException. "
                        +"\n"+IOUtil.StackTraceToString(e));
            } 
            if(conn == null) {
                throw new DBFactoryException(": DB Connection is NULL / DB Connection fail. Check DataBase Info");
            }
            Log2.out("[info] [DBConnFactory ] [DB Connection: Successful]");
        }
        return conn;
    }

    private Dsn getDsn(DataSource dsource){
        Dsn dsn = new Dsn();
        Properties prps = new Properties();
        // 2006.07.11
        // database userid and password is encryption
        // decryption function add
        String userID = dsource.getUser();
        String userPWD = dsource.getPwd();
        if(EncryptUtil.isHexa(userID)) {
            if(userID.length() > 4 && userPWD.length() >4){
                userID = userID.substring(4, userID.length());
                userPWD = userPWD.substring(4, userPWD.length());
            }
            userID =  EncryptUtil.decryptString(userID) ;
            userPWD =  EncryptUtil.decryptString(userPWD) ;
        } else if(System.getProperty("datasource.encrypt") != null) {
        	String encrypt = System.getProperty("datasource.encrypt");
        	
        	if(encrypt.equalsIgnoreCase("aes")) {
        		userID =  EncryptUtil.decryptStringAES(userID) ;
                userPWD =  EncryptUtil.decryptStringAES(userPWD) ;
        	}
        	
        }
        prps.setProperty("user", userID);
        prps.setProperty("password", userPWD);
        //prps.setProperty("encoding", charSet);
        if(dsource.getDataBaseName() != null && !dsource.getDataBaseName().equals("") && dsource.getVender() != SYMFOWARE){
            prps.setProperty("DatabaseName", dsource.getDataBaseName());
        }
        
        if(dsource.getVender() == MYSQL){
        	prps.setProperty("validationQuery", "SELECT 1");
        } else if(dsource.getVender() == ORACLE){
        	prps.setProperty("validationQuery", "SELECT 1 FROM DUAL");
        } else if(dsource.getVender() == MSSQL){
        	prps.setProperty("validationQuery", "SELECT 1");
        } else if(dsource.getVender() == DB2){
        	prps.setProperty("validationQuery", "SELECT 1 FROM sysibm.sysdummy1");
        } else if(dsource.getVender() == POSTGRE){
        	prps.setProperty("validationQuery", "SELECT 1");
        } else if(dsource.getVender() == ALTIBASE){
        	prps.setProperty("validationQuery", "select 0 from db_root");
        } else if(dsource.getVender() == CUBRID){
        	prps.setProperty("validationQuery", "SELECT 1");
        } else if(dsource.getVender() == TIBERO){
        	prps.setProperty("validationQuery", "SELECT 1");
        } else if(dsource.getVender() == EDB){
        prps.setProperty("validationQuery", "SELECT 1");
    }
        
        URLClassLoader loader ;
        try{
            dsn.setPrps(prps);
            switch (dsource.getVender()) {
                case ORACLE:		//by oracle
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    dsn.setUrl("jdbc:oracle:thin:@" + dsource.getServerName()
                            +":"+dsource.getPort()+":"+dsource.getSid());
                    break;
                case ORACLE_OCI:		//by oracle oci
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    dsn.setUrl("jdbc:oracle:oci8:@" + dsource.getServerName());
                    break;
                case MSSQL: //by mssql v7, v2000, v2003, v2005, v2010
                    //set mssql jtds jdbc logging directory
                    FileUtil.makeDir(JDBC_ROOT, "tmp");
                    System.setProperty("java.io.tmpdir", JDBC_ROOT+"tmp");
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    //dsn.setUrl("jdbc:jtds:sqlserver://" + dsource.getServerName()+":"
                    // +dsource.getPort()+ "/" + dsource.getDataBaseName());
                    dsn.setUrl("jdbc:jtds:sqlserver://" + dsource.getServerName()
                            +":"+dsource.getPort()+ "/" + dsource.getDataBaseName());
                    break;
                case MSSQL2005:		//by v2005
                    //com.microsoft.sqlserver.jdbc.SQLServerDriver
                    //jdbc:sqlserver://61.82.137.177:1433;database=arq
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    dsn.setUrl("jdbc:sqlserver://" + dsource.getServerName()
                            +":"+dsource.getPort()+ ";database=" + dsource.getDataBaseName());
                    break;
                    //by mysql  old
                    //dsn.setDriver("org.gjt.mm.mysql.Driver");
                case MYSQL:     //by mysql v4.x
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    dsn.setUrl("jdbc:mysql://" + dsource.getServerName()
                            +":"+dsource.getPort()+ "/" + dsource.getDataBaseName());
                    break;
                case INFORMIX:		//by INFORMIX
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    dsn.setUrl("jdbc:informix-sqli://" + dsource.getServerName()
                            +":"+dsource.getPort()+ "/" +dsource.getDataBaseName());
                    break;
                case DB2:		//by DB2 8.x connect
                    //dsn.setDriver("COM.ibm.db2.jdbc.app.DB2Driver");
                    //dsn.setDriver("COM.ibm.db2.jdbc.net.DB2Driver");
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    if(dsource.getPort().length() < 1){
                        dsn.setUrl("jdbc:db2://"+ dsource.getServerName()
                                +"/"+dsource.getDataBaseName());
                    } else{
                        dsn.setUrl("jdbc:db2://"+ dsource.getServerName()
                                +":"+dsource.getPort()+"/"+dsource.getDataBaseName());
                    }
                    break;
                case AS400:		//by AS400 connect
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    if(dsource.getPort().length() < 1){
                        dsn.setUrl("jdbc:as400://"+ dsource.getServerName()
                                +"/"+dsource.getDataBaseName());
                    } else{
                        dsn.setUrl("jdbc:as400://"+ dsource.getServerName()
                                +":"+dsource.getPort()+"/"+dsource.getDataBaseName());
                    }
                    break;
                case UNISQL:		//by UNISQL
                    //dsn.setDriver("unisql.jdbc.driver.UniSQLDriver");
                    //con = Driver.Manager.getConnection("jdbc:unisql:10.20.30.40:"+port+":"+dbName+":::", userId, pwd);
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    dsn.setUrl("jdbc:unisql:"+dsource.getServerName()
                            +":"+dsource.getPort()+":"+dsource.getDataBaseName()+":::");
                    break;
                case SYBASE_ASE:		//by SYBASE ASE JDBC Driver
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));    //jdbc:sybase:Tds:myserver
                    //dsn.setDriver("com.sybase.jdbc2.jdbc.SybDriver");
                    //For jConnect 5.x com.sybase.jdbc2.jdbc.SybDriver
                    //jdbc:sybase:Tds:172.30.1.131:6789/junet"
                    dsn.setUrl("jdbc:sybase:Tds:"+dsource.getServerName()
                            +":"+dsource.getPort()+"/"+dsource.getDataBaseName());
                    break;
                case SYBASE:		//by SYBASE JTDS JDBC Driver
                    //set sybase jtds jdbc logging directory
                    FileUtil.makeDir(JDBC_ROOT, "tmp");
                    System.setProperty("java.io.tmpdir", JDBC_ROOT+"tmp");
                    loader = setJDBCClassLoader(dsource);
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    /*** example
                     dsn.setDriver("net.sourceforge.jtds.jdbc.Driver");
                     jdbc:jtds:sybase://127.0.0.1:5000/SAMPLE;TDS=5.0;charset=euc-kr
                     OR jdbc:jtds:sybase://127.0.0.1:5000/SAMPLE;charset=euc-kr
                     */
                    dsn.setUrl("jdbc:jtds:sybase://" + dsource.getServerName()
                            +":"+dsource.getPort()+ "/" + dsource.getDataBaseName());
                    break;
                case POSTGRE:		//by POSTGRE
                	loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    dsn.setDriver("org.postgresql.Driver");
                    dsn.setUrl("jdbc:postgresql://"+dsource.getServerName()
                            +":"+dsource.getPort()+"/"+dsource.getDataBaseName());
                    break;
                case ACCESS:		//by ACCESS
                    //SERVER NAME is odbc  dsn name
                	loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    dsn.setDriver("sun.jdbc.odbc.JdbcOdbcDriver");
                    dsn.setUrl("jdbc:odbc:"+dsource.getServerName());
                    break;
                case ALTIBASE:   //by ALTIBASE
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    dsn.setUrl("jdbc:Altibase://"+dsource.getServerName()
                            +":"+dsource.getPort()+"/"+dsource.getDataBaseName());
                    break;
                case CUBRID:		//by CUBRID
                    //cubrid.jdbc.driver.CUBRIDDriver
                    //jdbc:cubrid:210.216.33.250:43300:demodb
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    if ("".equals(dsource.getChar_set()))
                    {
                    	dsn.setUrl("jdbc:cubrid:"+dsource.getServerName()
                    			+":"+dsource.getPort()+":"+dsource.getDataBaseName()+":::?charset=UTF-8");
                    } else {
                    	dsn.setUrl("jdbc:cubrid:"+dsource.getServerName()
                    			+":"+dsource.getPort()+":"+dsource.getDataBaseName()+":::?charset="+dsource.getChar_set());
                    }
                    break;
                case DERBY: //by SLA on DERBY
                    //org.apache.derby.jdbc.ClientDriver
                    //jdbc:derby://211.39.140.159:1368/sla;create=true
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    dsn.setUrl("jdbc:derby://"+dsource.getServerName()+":"+dsource.getPort()+"/"+dsource.getDataBaseName()+";create=true");
                    break;
                case SYMFOWARE:		//by SYMFOWARE
                	loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    dsn.setDriver(dsource.getClassname());
                    dsn.setUrl("jdbc:symford://"+dsource.getServerName()
                            +":"+dsource.getPort()+"/"+dsource.getDataBaseName());
                    break;
                 case TIBERO:		//by TIBERO
                   	 loader = setJDBCClassLoader(dsource) ;
                     dsn.setDbms(loader.loadClass(dsource.getClassname()));
                     dsn.setUrl("jdbc:tibero:thin:@" + dsource.getServerName()
                             +":"+dsource.getPort()+":"+dsource.getSid());
                     break;
                 case HIRDB:		//by TIBERO
                   	 loader = setJDBCClassLoader(dsource) ;
                     dsn.setDbms(loader.loadClass(dsource.getClassname()));
                     dsn.setUrl("jdbc:hitachi:hirdb://DBID=" + dsource.getPort() +",DBHOST=" + dsource.getServerName());
                     break;
                case EDB:		//by EDB
                    loader = setJDBCClassLoader(dsource) ;
                    dsn.setDbms(loader.loadClass(dsource.getClassname()));
                    dsn.setUrl("jdbc:edb://"+dsource.getServerName()
                            +":"+dsource.getPort()+"/"+dsource.getDataBaseName());
                    break;
                default:
                    break;
            }

            //2007.03.26  Url format supported.
            String jdbcUrl = dsource.getJdbcUrl();
            if(jdbcUrl != null && !jdbcUrl.equals("")) {
                dsn.setUrl(jdbcUrl);
                Log2.debug("[DBConnFactory] [JDBC URL : " + jdbcUrl + "]", 3);
            }
        }catch(ClassNotFoundException e){
            Log2.error("[DBConnFactory] [Not Found JDBC Class : "
            +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        }

        return dsn;
    }

    /**
     *
     * @param dsource DataSource
     * @return URLClassLoader
     */
    public URLClassLoader setJDBCClassLoader(DataSource dsource){
        URL url[] = new URL[dsource.driverSize()];
        URLClassLoader loader = null;
        try{
            for(int i=0; i<url.length; i++) {
                url[i] = new File(JDBC_ROOT+dsource.getDriver()[i]).toURL();
            }

            loader = new URLClassLoader(url);
        }catch(MalformedURLException e){
            Log2.error("[DBConnFactory] [Please check the bridge/jdbc folder."
                    +"\n"+IOUtil.StackTraceToString(e)+"\n]");
        }
        return loader;
    }

    /**
     *
     * @param dsn String
     * @return dbms vendor
     * @throws DBFactoryException  error info
     */
    public int getDbmsType(String dsn) throws DBFactoryException {
        if( (DataSource)(m_dsMap.get(dsn)) == null) {
            String debug = "<Database/> configuration value "+StringUtil.newLine ;
            Collection collection = m_dsMap.values();
            Iterator ite = collection.iterator();
            while(ite.hasNext()) {
                debug += " Id=\""+((DataSource)ite.next()).getId() +"\""+StringUtil.newLine;
            }
            Log2.debug("[DBConnFactory ] [ "+debug+"]", 3);
            if( ((DataSource)(m_dsMap.get(dsn))) == null) {
                Log2.error("Unable to read the dsn=\""+dsn+"\" and id in datasource.xml." +
                        " Please check the <DSN type=\"target|log\" dsn=\""+dsn+"\"/>");
                return -1;
            }
            throw new DBFactoryException("Unable to read the dsn=\""+dsn+"\" and id in datasource.xml." +
                        " Please check the <DSN type=\"target|log\" dsn=\""+dsn+"\"/>");
        }

        return ((DataSource)(m_dsMap.get(dsn))).getVender();
    }

    protected void error(String err) {
        Log2.error(err);
    }

    protected void debug(String msg) {
        Log2.debug(msg, 4);
    }
}
