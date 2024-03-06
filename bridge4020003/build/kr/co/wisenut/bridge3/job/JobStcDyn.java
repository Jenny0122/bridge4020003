package kr.co.wisenut.bridge3.job;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import kr.co.wisenut.bridge3.config.Config;
import kr.co.wisenut.bridge3.config.RunTimeArgs;
import kr.co.wisenut.bridge3.config.catalogInfo.InfoSet;
import kr.co.wisenut.bridge3.config.source.AttachInfo;
import kr.co.wisenut.bridge3.config.source.FtServerConfig;
import kr.co.wisenut.bridge3.config.source.MemorySelect;
import kr.co.wisenut.bridge3.config.source.MemoryTable;
import kr.co.wisenut.bridge3.config.source.SubQuery;
import kr.co.wisenut.bridge3.config.source.TableSchema;
import kr.co.wisenut.bridge3.config.source.node.XmlQuery;
import kr.co.wisenut.bridge3.config.source.node.XmlValue;
import kr.co.wisenut.bridge3.ftserver.FtClient;
import kr.co.wisenut.common.Exception.BridgeException;
import kr.co.wisenut.common.Exception.CustomException;
import kr.co.wisenut.common.Exception.DBFactoryException;
import kr.co.wisenut.common.Exception.FilterException;
import kr.co.wisenut.common.filter.FilterFactory;
import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.filter.IFilter;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.scdreceiver.SCDTransmit;
import kr.co.wisenut.common.util.DateUtil;
import kr.co.wisenut.common.util.EncryptUtil;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.HtmlUtil;
import kr.co.wisenut.common.util.JSONManager;
import kr.co.wisenut.common.util.PropsReader;
import kr.co.wisenut.common.util.SCDManager;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.XmlUtil;
import kr.co.wisenut.common.util.io.IOUtil;

/**
 * 
 * JobStcDyn
 * 
 * Copyright 2000-2012 WISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 19 Jun 2012
 * Bridge Release 11 March 2009
 * 
 * @author WISEnut
 * @version 3.8,5. 2012/06/19 Bridge Release
 * 
 */

public class JobStcDyn extends Job {
	protected HashMap m_filters = new HashMap();

	/**
	 * key : ftserver.id, value:FtClient
	 * */
	private HashMap m_ftClients = new HashMap();
	private boolean isTest = false;
	private StringBuffer sbSubQuery = new StringBuffer(8192);
	private boolean isIndexDir = false;
	private PropsReader dateTimeProp = null;

	private SubMemoryManager subMemoryMgr;

	private String filteredTextDir = "";

	public JobStcDyn(Config config, int mode) throws DBFactoryException {
		super(config, mode);
		// v3.8.4 modified.
		File indexDir;
		// scd/index directory is default create.
		if (config.getArgs().isScdFilter()) {
			String sourceId = config.getArgs().getSrcid();
			FileUtil.makeDir(m_source.getScdDir().getPath(), sourceId + "-temp");
			indexDir = new File(m_source.getScdDir().getPath(), sourceId + "-temp");
		} else {
			FileUtil.makeDir(m_source.getScdDir().getPath(), "index");

			if (config.getArgs().isUserIndexDir()) {
				indexDir = new File(m_source.getScdDir().getPath(), "index");
			} else if (mode == IJob.STATIC) {
				indexDir = new File(m_source.getScdDir().getPath(), "static");
			} else if (mode == IJob.DYNAMIC || mode == IJob.REPLACE) {
				indexDir = new File(m_source.getScdDir().getPath(), "dynamic");
			} else {
				indexDir = new File(m_source.getScdDir().getPath());
			}
		}
		this.fileManager = new JSONManager(indexDir, m_source.getScdCharSet(), m_source.getScdDir().getIdx());

		if (m_source.getExtension().equalsIgnoreCase("scd")) {
			this.fileManager = new SCDManager(indexDir, m_source.getScdCharSet(), m_source.getScdDir().getIdx());
		}

		this.isIndexDir = config.getArgs().isUserIndexDir();
		this.filteredTextDir = FileUtil.lastSeparator(config.getSf1Home()) + "Filter" + FileUtil.getFileSeperator()
				+ config.getSrcid() + FileUtil.getFileSeperator();
	}

	public boolean run() throws BridgeException {

		String brgRunningDateTimeValue = "";
		String strDateTimeQuery = "";
		try {
			// Setting Target DataBase Connection
			m_dbjob.setTargetConnection();

			// Get database datetime configuration
			String strDateTimePropertyPath = m_query.getDateTimePropertyPath();
			strDateTimeQuery = m_query.getDateTimeQuery();

			if (!"".equals(strDateTimePropertyPath) && !"".equals(strDateTimeQuery)) {
				dateTimeProp = new PropsReader(strDateTimePropertyPath);
				String[][] dateTimeTemp = m_dbjob.getQueryRsData(strDateTimeQuery, null, "", true);
				String runningStatus = "0";
				if (dateTimeProp.getProperty(m_config.getSrcid() + "." + BRIDGE_RUNNING_STATUS) != null) {
					runningStatus = dateTimeProp.getProperty(m_config.getSrcid() + "." + BRIDGE_RUNNING_STATUS);
				}
				if (dateTimeProp.getProperty(m_config.getSrcid() + "." + BRIDGE_START_DATETIME) != null) {
					if (runningStatus.equals("0")) {
						brgRunningDateTimeValue = dateTimeProp.getProperty(m_config.getSrcid() + "."
								+ BRIDGE_START_DATETIME);
					}
					if (runningStatus.equals("-1")) {
						brgRunningDateTimeValue = dateTimeProp.getProperty(m_config.getSrcid() + "."
								+ BRIDGE_RUNNING_DATETIME);
					}
				}
				dateTimeProp.addProperty(m_config.getSrcid() + "." + BRIDGE_START_DATETIME, dateTimeTemp[0][0]);
			}

			// Master Table processing ...
			String[] mstTblList = null;

			String mstTblQuery = m_query.getMasterTblSelect();
			if (!mstTblQuery.equals("")) {
				String mstList[][] = m_dbjob.getQueryRsData(mstTblQuery, null, ":", false);
				String mstData = mstList[0][0];
				StringTokenizer token = new StringTokenizer(mstData, ":");
				int tokenCount = token.countTokens();

				mstTblList = new String[tokenCount];

				for (int idx = 0; idx < tokenCount; idx++) {
					mstTblList[idx] = token.nextToken();
				}

				for (int idx = 1; idx < mstList.length; idx++) {
					mstData = mstList[idx][0];
					token = new StringTokenizer(mstData, ":");
					tokenCount = token.countTokens();

					for (int i = 0; i < tokenCount; i++) {
						mstTblList[i] += "/" + token.nextToken();
					}
				}

			}

			String dynMode = m_query.getDynMode();

			// load SubMemory
			if (m_config.getSource().getSubMemory() != null) {
				if (m_config.getSource().getSubMemory().getMemorySelectMap().size() > 0) {
					subMemoryMgr = new SubMemoryManager(m_config, m_dbjob);
					subMemoryMgr.loadSubMemory();
				}
			}

			// starting DB Bridge job
			if (m_mode == STATIC) {
				Log2.out("[info][JobStcDyn][Static process....]");
				// Not using Queue Table or Log Table
				String staticQuery = m_query.getStaticQuery();
				if (!staticQuery.equals("")) {
					if (mstTblList != null && mstTblList.length > 0) {
						int mstTblCount = mstTblList.length;

						for (int idx = 0; idx < mstTblCount; idx++) {
							String masterQuery = staticQuery;
							String[] masterInfo = mstTblList[idx].split("/");

							int infoCount = masterInfo.length;

							for (int idx_j = 0; idx_j < infoCount; idx_j++) {
								masterQuery = m_queryGen.replaceQuery(masterQuery, "%s" + (idx_j + 1),
										masterInfo[idx_j]);
							}

							insertUpdate(INSERT, masterQuery, "");
						}
					} else {
						insertUpdate(INSERT, staticQuery, ""); // execute InsertSelect sql query
					}
				} else {
					error("[JobStcDyn][SQL query empty."
							+ ": Please check the <Static> - <Select> SQL query in configuration file.]");
				}
				// END STATIC MODE
			} else if (m_mode == DYNAMIC) {
				if (!setLogConnection()) {
					error("[JobStcDyn][DBMS connection error."
							+ ": Please check the <Dynamic mode=\"logdb\"> setting in configuration file.]");
					return false;
				}

				String sqlDeleteQuery = m_query.getSelectQDeleteDoc();

				// dynamic once mode
				if (dynMode.toLowerCase().equals("datetime")) {
					String strDateTimeValue = dateTimeProp.getProperty(m_config.getSrcid() + "."
							+ BRIDGE_RUNNING_DATETIME);
					sqlDeleteQuery = sqlDeleteQuery.replaceAll("%DATETIME%", strDateTimeValue);
					log("[info] [JobStcDyn] %DATETIME% of Delete SQL: "+strDateTimeValue);
				}

				// crawling deleted data
				if( !sqlDeleteQuery.equals( "" ) )
				{
				    if (mstTblList != null && mstTblList.length > 0) {
	                    int mstTblCount = mstTblList.length;

	                    for (int idx = 0; idx < mstTblCount; idx++) {
	                        String masterQuery = sqlDeleteQuery;
	                        String[] masterInfo = mstTblList[idx].split("/");

	                        int infoCount = masterInfo.length;

	                        for (int idx_j = 0; idx_j < infoCount; idx_j++) {
	                            masterQuery = m_queryGen.replaceQuery(masterQuery, "%s" + (idx_j + 1),
	                                    masterInfo[idx_j]);
	                        }

	                        delete(sqlDeleteQuery);
	                    }
	                } else {
	                    delete(sqlDeleteQuery);
	                }
				}
				else
				{
				    debug("[JobStcDyn][Missing "
                            + ":<Dynamic> - <Select type=\"D\"> - <sql> setting in configuration file.]");
				}

				String sqlInsertQuery = m_query.getSQLInsert();
				String sqlUpdateQuery = m_query.getSQLUpdate();
				boolean hasInsertQuery = true;

				if (sqlInsertQuery.equals(sqlUpdateQuery)) {
					hasInsertQuery = false;
					
					debug("[JobStcDyn][Inserted Query Same as Updated Query. \"I\" Type crawling result will be skip]");
				} else if(sqlInsertQuery.equals( "" )) {
				    hasInsertQuery = false;
				    
				    debug("[JobStcDyn][Missing "
	                        + ":<Dynamic> - <Select type=\"IU\"> - <condition type=\"I\"> setting in configuration file.]");
				}
				
				if (!sqlUpdateQuery.equals("")) {
					if (dynMode.toLowerCase().equals("datetime")) {
						String strDateTimeValue = dateTimeProp.getProperty(m_config.getSrcid() + "."
								+ BRIDGE_RUNNING_DATETIME);
						sqlUpdateQuery = sqlUpdateQuery.replaceAll("%DATETIME%", strDateTimeValue);
						log("[info] [JobStcDyn] %DATETIME% of Update SQL : "+strDateTimeValue);
					}
					
					if (mstTblList != null && mstTblList.length > 0) {
						int mstTblCount = mstTblList.length;

						for (int idx = 0; idx < mstTblCount; idx++) {
							String masterQuery = sqlUpdateQuery;
							String[] masterInfo = mstTblList[idx].split("/");

							int infoCount = masterInfo.length;

							for (int idx_j = 0; idx_j < infoCount; idx_j++) {
								masterQuery = m_queryGen.replaceQuery(masterQuery, "%s" + (idx_j + 1),
										masterInfo[idx_j]);
							}

							insertUpdate(DYNAMIC, masterQuery, "");
						}
					} else {
						// execute dynamic update select sql query
						insertUpdate(UPDATE, sqlUpdateQuery, "");
					}
				} else {
					error("[JobStcDyn][SQL query empty."
							+ ": Please check the <Dynamic> - <Select> SQL query in configuration file.]");
				}
				if (hasInsertQuery) { // if same to insert sql between update sql skip
					// //Using Log Table or Queue Table
					if (!sqlInsertQuery.equals("")) {
						if (dynMode.toLowerCase().equals("datetime")) {
							String strDateTimeValue = dateTimeProp.getProperty(m_config.getSrcid() + "."
									+ BRIDGE_RUNNING_DATETIME);
							sqlInsertQuery = sqlInsertQuery.replaceAll("%DATETIME%", strDateTimeValue);
							log("[info] [JobStcDyn] %DATETIME% of Insert SQL : "+strDateTimeValue);
						}
						
						if (mstTblList != null && mstTblList.length > 0) {
							int mstTblCount = mstTblList.length;

							for (int idx = 0; idx < mstTblCount; idx++) {
								String masterQuery = sqlInsertQuery;
								String[] masterInfo = mstTblList[idx].split("/");

								int infoCount = masterInfo.length;

								for (int idx_j = 0; idx_j < infoCount; idx_j++) {
									masterQuery = m_queryGen.replaceQuery(masterQuery, "%s" + (idx_j + 1),
											masterInfo[idx_j]);
								}

								insertUpdate(INSERT, masterQuery, "");
							}
						} else {
							insertUpdate(INSERT, sqlInsertQuery, ""); // Execute static select
						}
					} else {
						error("[JobStcDyn][SQL query empty. "
								+ ": Please check the <Dynamic> - <Select> SQL query in configuration file.]");
					}
				}

				// END DYNAMIC MODE
			} else if (m_mode == REPLACE) {
				if (!setLogConnection()) {
					error("[JobStcDyn][DBMS connection error."
							+ ": Please check the <Dynamic mode=\"logdb\"> setting in configuration file.]");
					return false;
				}
				String replaceQuery = m_query.getReplaceQuery();
				if (!replaceQuery.equals("")) {
					if (dynMode.toLowerCase().equals("datetime")) {
						String strDateTimeValue = dateTimeProp.getProperty(m_config.getSrcid() + "."
								+ BRIDGE_RUNNING_DATETIME);
						replaceQuery = replaceQuery.replaceAll("%DATETIME%", strDateTimeValue);
						log("[info] [JobStcDyn] %DATETIME% of Replace SQL : "+strDateTimeValue);
					}
					
					
					if (mstTblList != null && mstTblList.length > 0) {
						int mstTblCount = mstTblList.length;

						for (int idx = 0; idx < mstTblCount; idx++) {
							String masterQuery = replaceQuery;
							String[] masterInfo = mstTblList[idx].split("/");

							int infoCount = masterInfo.length;

							for (int idx_j = 0; idx_j < infoCount; idx_j++) {
								masterQuery = m_queryGen.replaceQuery(masterQuery, "%s" + (idx_j + 1),
										masterInfo[idx_j]);
							}

							insertUpdate(REPLACE, masterQuery, "");
						}
					} else {
						insertUpdate(REPLACE, replaceQuery, ""); // Execute replace sql query
					}
				} else {
					error("[JobStcDyn][SQL query empty."
							+ ": Please check the <Dynamic> - <Select> SQL query in configuration file.]");
				}
				// END REPLACE MODE
			}
		} catch (Exception e) {
			m_dbjob.releaseDB();
			try {
				fileManager.errorClose(false);
				// Exception error datetime Initialized
				if (dateTimeProp != null) {
					dateTimeProp.addProperty(m_config.getSrcid() + "." + BRIDGE_RUNNING_DATETIME,
							brgRunningDateTimeValue);
					dateTimeProp.addProperty(m_config.getSrcid() + "." + BRIDGE_RUNNING_STATUS, "-1");
				}
			} catch (IOException ex) {
				error("[Bridge][SCD Close I/O Exception: " + "\n" + IOUtil.StackTraceToString(ex) + "\n]");
			}
			throw new BridgeException(": JobStcDyn Class," + "\n" + IOUtil.StackTraceToString(e) + "\n]");
		}
		try {
			
			if (dateTimeProp != null) {
				dateTimeProp.addProperty(m_config.getSrcid() + "." + BRIDGE_RUNNING_STATUS, "0");
				// If not exists properties file that running to db time.
				String[][] dateTimeTemp = m_dbjob.getQueryRsData(strDateTimeQuery, null, "", true);
				dateTimeProp.addProperty(m_config.getSrcid() + "." + BRIDGE_START_DATETIME, dateTimeTemp[0][0]);
				dateTimeProp.addProperty(m_config.getSrcid() + "." + BRIDGE_RUNNING_DATETIME, dateTimeTemp[0][0]);
			}
			
			// Target Connection Release
			m_dbjob.releaseDB();

			fileManager.close(false);

			// SCD file send. 2010.06.14 by ikcho
			if (m_source.getRemoteinfo().length != 0) {
				int remoteSize = m_source.getRemoteinfo().length;
				String rcvMode = m_config.getArgs().getScdRcvMode();
				String scdDirPath = m_source.getScdDir().getPath();
				String remoteDirPath = m_source.getRemoteDir();
				
				if (this.isIndexDir) {
					scdDirPath = scdDirPath + "/index";
					remoteDirPath = remoteDirPath + "index";
				} else if (m_mode == IJob.STATIC) {
					scdDirPath = scdDirPath + "/static";
					remoteDirPath = remoteDirPath + "static";
				} else if (m_mode == IJob.DYNAMIC) {
					scdDirPath = scdDirPath + "/dynamic";
					remoteDirPath = remoteDirPath + "dynamic";
				}
				
				File backupFile = new File(scdDirPath);
				for(int idx=0; idx<remoteSize; idx++) {
					String remoteIp = m_source.getRemoteinfo()[idx].getIp();
					int remotePort = m_source.getRemoteinfo()[idx].getPort();
					boolean isScdDelete = false;
					
					SCDTransmit trans = new SCDTransmit(remoteIp, remotePort, rcvMode);
					Log2.debug("[Job ] [SCD File TransMit" + remoteIp + "]", 3);
					
					if(idx+1 == remoteSize) {
						isScdDelete = m_source.getRemoteinfo()[idx].isDeleteSCD();
						backupFile = new File(m_source.getScdDir().getPath(), "backup");
					}
					
					trans.sendSCD(scdDirPath, remoteDirPath , backupFile.getPath(), isScdDelete); // setting path..
				}
			}

		} catch (IOException e) {
			error("[Bridge][I/O Exception: " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
			return false;
		}catch (Exception e) {
			error("[Bridge][Exception: " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
			return false;
		}

		if (m_config.getArgs().isFilterFileDelete() && new File(filteredTextDir).exists()) {
			gabageFilteredTextFile();
		}

		return true;
	}

	private void gabageFilteredTextFile() {
		if (!FileUtil.deleteFile(filteredTextDir, ".txt")) {
			Log2.error("[JobStcDyn] [Some filtered Files Delete Failed, Directory: " + filteredTextDir + "]");
		}
		Log2.out("[JobStcDyn] [Gabage filtered text file deleted.]");
	}

	/**
	 * LogTable connection method
	 * @return success or fail
	 * @throws Exception
	 *             connection error info
	 */
	private boolean setLogConnection() throws Exception {
		String dynMode = m_query.getDynMode();
		if (dynMode.equals("none")) {
			error("[JobStcDyn][Missing "
					+ ": <Dynamic mode=\"logdb\"> or <Dynamic mode=\"queue\"> setting in configuration file.]");
			return false;
		} else if (dynMode.equals("logdb")) {
			m_dbjob.setLogConnection();
			if (m_query.getDynInitExecute().length == 0) {
				error("[JobStcDyn][Missing "
						+ ": <Dynamic> - <Init> - <Execute> - <sql> setting in configuration file.]");
				return false;
			}
		}
		return true;
	}

	/**
	 * DB Bridge business logic method
	 * @param scdType
	 *            scd file type
	 * @param query
	 *            crawling query
	 * @param strReplace
	 *            replace sql bind variable
	 * @throws BridgeException
	 *             error info
	 * @throws DBFactoryException
	 *             error info
	 * @throws FilterException
	 *             error info
	 * @throws CustomException
	 */
	public void insertUpdate(int scdType, String query, String strReplace) throws BridgeException, DBFactoryException,
			FilterException, SQLException, CustomException, Exception {

		isSCDWrite(scdType); // Create SCD file
		String dynMode = m_query.getDynMode();

		if (m_mode == STATIC) {
			String[] execQuery = m_query.getStaticInitExecute();
			log("[info] [JobStcDyn] [Static init Start ...]");
			int count = 0;
			if (execQuery != null && execQuery.length > 0) {
				for (int i = 0; i < execQuery.length; i++) {
					if (!execQuery[i].equals("")) {
						log("[info] [JobStcDyn] [Query Execute]");
						execQuery[i] = replaceQueryByArguments(execQuery[i]);
						try {
							count = m_dbjob.execPQuery(execQuery[i]);
							debug("[info] [Execute query successful (" + count + ") ]", 3);
						} catch (SQLException e) {
							error("[JobStcDyn] [Static init SQL Error Query: " + execQuery[i] + "\n"
									+ IOUtil.StackTraceToString(e) + "]");
						}
					}
				}
			}
			log("[info] [JobStcDyn] [Static init End.]");
		}

		// mode none static
		// execute init sql query
		if (m_mode != STATIC) {
			// LogTable Update query
			String[] execQuery = m_query.getDynInitExecute();
			log("[info] [JobStcDyn] [Dynamic init Start ...]");
			int count = 0;
			if (execQuery != null && execQuery.length > 0) {
				for (int i = 0; i < execQuery.length; i++) {
					if (!execQuery[i].equals("")) {
						log("[info] [JobStcDyn] [Query Execute]");
						execQuery[i] = replaceQueryByArguments(execQuery[i]);
						try {
							if (dynMode.equals("logdb")) {
								count = m_dbjob.execPQueryLog(execQuery[i]);
							} else if (dynMode.equals("queue")) {
								count = m_dbjob.execPQuery(execQuery[i]);
							}
							debug("[info] [Execute query successful (" + count + ") ]", 3);
						} catch (SQLException e) {
							error("[JobStcDyn] [Dynamic init SQL Error Query: " + execQuery[i] + "\n"
									+ IOUtil.StackTraceToString(e) + "]");
						}
					}
				}
			}
			log("[info] [JobStcDyn] [Dynamic init End.]");
		}

		// execute sql query
		log("[info] [JobStcDyn] [Set target result set : wait a moment, please]");
		query = replaceQueryByArguments(query);
		m_dbjob.setResultSet(query);

		// int chkCount = 0;
		long totalCount = 0;

		InfoSet[] catalog = m_collection.getCatalog();
		TableSchema tbls = m_source.getTblSchema();

		int rsCnt = m_dbjob.getTargetColumnCnt();
		// <PrimaryKey> <Column> count
		int pkCnt = tbls.getPrimaryKeys().length;

		int tagCnt = catalog.length;

		// Source prefix
		String srcPrefix = m_source.getRefColl().getPrefix();

		String convert;
		log("[info] [JobStcDyn] [String process...]");

		// Add 2005.07.24 Test mode
		if (m_mode == TEST) {
			isTest = true;
		}

		boolean isQueueSeq = false;
		if (m_mode == DYNAMIC || m_mode == REPLACE) {
			if (dynMode.equals("queue") && m_query.isSeqNoYn())
				isQueueSeq = true;
		}
		while (!m_dbjob.isError() && m_dbjob.next()) {
			String[][] pkArrs; // primary key array
			String[][] arrQueue; // using queue table key array
			if (isQueueSeq) {
				// queue table and seq select -> pk + 1
				pkArrs = new String[pkCnt + 1][2];
			} else {
				pkArrs = new String[pkCnt][2];
			}

			String docid = "";
			for (int colIdx = 0, dbIdx = 1; colIdx < tagCnt; colIdx++, dbIdx++) {
				String tagName = "<" + catalog[colIdx].getTagName() + ">";
				debug("[JobStcDyn] [Mapping column=\"" + colIdx + "\" " + "fieldname=\"" + catalog[colIdx].getTagName()
						+ "\"]");
				String colunmData = "";

				if (colIdx == 0) {// SET DOCID
					for (int pkIdx = 1; pkIdx <= pkCnt; pkIdx++) {
						String[][] arrPk = m_dbjob.getString(pkIdx);
						pkArrs[pkIdx - 1][0] = arrPk[0][0];
						pkArrs[pkIdx - 1][1] = arrPk[0][1];
						docid += arrPk[0][0];
						dbIdx = pkIdx;
					}
					// sbSCDBuf.append(tagName+docid+m_lineseperator);

					// Add 2008.06.18 DOCID -> MD5
					if (catalog[colIdx].isMd5()) {
						docid = EncryptUtil.MD5(docid);
					}

					// DOCID + srcPrefix
					data.put(catalog[colIdx].getTagName(), srcPrefix + docid);
					continue;
				}
				// if replace mode ...
				if (scdType == REPLACE) {
					if (catalog[colIdx].isReplace()) {
						debug("[JobStcDyn] [replace column(" + colIdx + ")]", 3);
					} else {
						dbIdx--;
						continue;
					}
				}

				// refnum
				if (catalog[colIdx].getRefnum() > -1) {
					colunmData = data.get(catalog[catalog[colIdx].getRefnum()].getTagName());

					// Custom Class processing
					String classNames = catalog[colIdx].getClassName();
					if (!classNames.equals("0x00")) {
						try {
							String[] classNameAry = classNames.split(",");
							for(String className : classNameAry){
								colunmData = CFactory.getInstance(className).customData(colunmData);
							}
						} catch (CustomException e) {
							Log2.error("[JobStcDyn] [ " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
						}
					}

					data.put(catalog[colIdx].getTagName(), colunmData);
					dbIdx--;

					continue;
				}

				// set sourceailas
				if (catalog[colIdx].isSourceAlias()) {
					debug("[JobStcDyn] [issourcealias column(" + colIdx + ")]");

					data.put(catalog[colIdx].getTagName(), m_source.getSourceaias());
					dbIdx--;

					continue;
				}

				// sub memory
				MemorySelect memorySelect = catalog[colIdx].getMemorySelect();
				if (memorySelect != null) {

					MemoryTable refMemoryTable = memorySelect.getRefMemoryTable();
					// Get key count from main sql query
					String[] keyFields = m_dbjob.getStringValue(dbIdx, refMemoryTable.getKeyCount());
					dbIdx = dbIdx + refMemoryTable.getKeyCount() - 1;
					// keyFields, seperator join
					String joinedKey = StringUtil.join(keyFields, refMemoryTable.getKeySeperator());

					// 占쎌뮆�닏占쎄퀣�뵠占쏙옙rows
					ArrayList rowDataList = subMemoryMgr.getData(refMemoryTable, joinedKey, memorySelect.getUseIdx());

					// 鈺곌퀗援뷂옙�끉肉� 筌띿쉶�뮉 占쎄퀣�뵠占쎄퀗占� 筌롫뗀�걟�뵳�딅퓠 占쎈냱�뱽占쏙옙
					if (rowDataList == null || rowDataList.size() == 0) {
						data.put(catalog[colIdx].getTagName(), "");
					} else {
						StringBuffer mergedValue = new StringBuffer();
						for (int i = 0; i < rowDataList.size(); i++) {

							// 占쎌늿�뵬 �뚎됱쓥占쏙옙row 占쎄퀣�뵠占쎄퀡諭�
							String rowValue = (String) rowDataList.get(i);

							if (!memorySelect.getClassName().equals("")) {
								String[] subMemoryClassAry = memorySelect.getClassName().split(",");
								for(String subMemoryClass : subMemoryClassAry){
									rowValue = CFactory.getSubInstance(subMemoryClass).customData(rowValue, memorySelect);
								}
							}
							// 揶쏄낫而뽳옙占퐎ow 占쎄퀣�뵠占쎄퀣肉� seperator �몴占쏙옙怨몄뒠
							mergedValue.append(rowValue).append(memorySelect.getSeperator());
						}

						String appendRowData = mergedValue.toString().trim();

						// 筌띾뜆占쏙쭕占퐏eperator 占쏙옙占쏙옙�젫占쎌뮆�뼄.
						if (rowDataList.size() > 0 && memorySelect.getSeperator().length() > 0
								&& !memorySelect.getSeperator().trim().equals("")) {
							appendRowData = appendRowData.substring(0, appendRowData.length()
									- memorySelect.getSeperator().length());
						}

						data.put(catalog[colIdx].getTagName(), appendRowData);
					}

					continue;
				}
				
				// xml query
				XmlQuery xmlQuery = catalog[colIdx].getXmlQuery();
				
				if(xmlQuery != null) {
					int whereCnt = xmlQuery.getWherecnt();
					String[][] subArrs = null;
					
					debug("[JobStcDyn] [XmlQuery order=\"" + colIdx + "\" wherecnt=\"" + whereCnt + "\"]");
					debug("[JobStcDyn] [XmlQuery Target DOCID is " + docid + "]");
					
					String[][] xml = m_dbjob.getString(dbIdx,whereCnt);
					String sqlXml = xmlQuery.getQuery();
					
					subArrs = m_dbjob.getQueryRsData(sqlXml, xml, xmlQuery.getSeperator(), xmlQuery.isStatemet());
					
					int useCollCnt = 0;
					try {
						for (int idx = 0; subArrs != null && idx < subArrs.length; idx++) {
							String columnData = subArrs[idx][0];
							
							//source.setSourceaias(getElementValue(element, "sourcealias", ""));
							
							XmlValue[] xmlValues = xmlQuery.getXmlValues();
							
							useCollCnt = xmlValues.length;
							
							for(int idx_j=0; idx_j < xmlValues.length; idx_j++) {
								String type = xmlValues[idx_j].getType();
								String path = xmlValues[idx_j].getPath();
								String attr = xmlValues[idx_j].getAttr();
								
								if(type.equals("text")) {
									String text = XmlUtil.getXmlPathToString(columnData, path, xmlQuery.getSeperator());
									data.put(catalog[colIdx].getTagName(), text);
								} else if(type.equals("attribute")) {
									String attribute = XmlUtil.getXmlPathToAttribute(columnData, path, attr, xmlQuery.getSeperator());
									data.put(catalog[colIdx].getTagName(), attribute);
								}
							}
						}
					} catch (Exception e) {
						
					}

					colIdx = colIdx + useCollCnt - 1;
					dbIdx = dbIdx + whereCnt - 1;

					continue;
				}

				// sub query
				SubQuery subQuery = catalog[colIdx].getSubQuery();
				int subMode = -1;
				if (subQuery != null) {
					subMode = subQuery.getSubquerymode();
					// Start SubQuery logic ...
					if ((m_mode == subMode || subMode == -1)) {
						int count = subQuery.getWhereCnt();
						String[][] subArrs = null;
						debug("[JobStcDyn] [SubQuery order=\"" + colIdx + "\" " + "type=\"" + subQuery.getType()
								+ "\" wherecnt=\"" + count + "\"]");

						debug("[JobStcDyn] [SubQuery Target DOCID is " + docid + "]"); // [SubQuery] Add DOCID Debug Message @JunSeok.Jung
						String[][] sub = m_dbjob.getString(dbIdx, count);
						String sqlSub = subQuery.getQuery();
						sqlSub = replaceQueryByArguments(sqlSub);
						if (!subQuery.getType().equals("reQuery")) {
							sqlSub = m_queryGen.replaceQuery(sqlSub, "%s1", strReplace);
							// If statement is yes 2006/02/14
							if (subQuery.isStatement()) {
								sqlSub = m_queryGen.addWhereCondition(subQuery.getQuery(), sub);
							}
						}
						if (subQuery.getType().equals("subQuery")) { // type: subQuery
							// SubCustomClass 鈺곕똻�삺占쏙옙野껋럩�뒭
							if (subQuery.getSubMappingMap() != null) {
								subArrs = m_dbjob.getQueryRsData(sqlSub, sub, subQuery.getSeperator(),
										subQuery.isStatement(), subQuery.getSubMappingMap());
							} else {
								subArrs = m_dbjob.getQueryRsData(sqlSub, sub, subQuery.getSeperator(),
										subQuery.isStatement());
							}

							// recursion
						} else if (subQuery.getType().equals("recursion")) { // type: recursion
							subArrs = m_dbjob.getRecRsData(sqlSub, sub, null, count, subQuery.isStatement());
						} else if (subQuery.getType().equals("reQuery")) { // type: reQuery
							subArrs = m_dbjob.getRecRsData(sqlSub, sub, subQuery.getSeperator(), count,
									subQuery.isStatement());
						} else if (subQuery.getType().equals("cims")) { // type: cims
							subArrs = m_dbjob.getCimsCategory(sqlSub, sub, subQuery.getSeperator(),
									subQuery.isStatement());
						} else if (subQuery.getType().equals("connectBy")) { // type: connectBy
							subArrs = m_dbjob.getConnectBy(sqlSub, sub, subQuery.getMaxCount(), subQuery.isStatement());
						} else if (subQuery.getType().equals("append")) { // type: append
							String sqlSplite[] = StringUtil.split(sqlSub, "[append]");
							for (int i = 0; sqlSplite != null && i < sqlSplite.length; i++) {
								subArrs = m_dbjob.getQueryRsData(sqlSplite[i], sub, subQuery.getSeperator(),
										subQuery.isStatement());
								for (int j = 0; subArrs != null && j < subArrs.length; j++) {
									convert = catalog[colIdx].getConvert(); // Add convert function 2005.12.8
									if (!convert.equals(""))
										subArrs[j][0] = StringUtil.convert(subArrs[j][0], convert);
									sbSubQuery.append(subArrs[j][0]);
								}
							}
						} else {
							throw new BridgeException("JobStcDyn Class SubQuery type Error!!"
									+ " type=\"subQuery|reQuery\"");
						}

						int subMappingCnt = 0;
						if (subQuery.getType().equals("append")) {
							if (catalog[colIdx].isHtmlParse()) { // html parse
								String subqueryData = sbSubQuery.toString();
								try {
									String htmlRemoveStr = HtmlUtil.getHtmlParse(subqueryData.getBytes("UTF-8"));
									data.put(catalog[colIdx].getTagName(), htmlRemoveStr.trim());
								} catch (UnsupportedEncodingException e) {

								}
							} else {
								data.put(catalog[colIdx].getTagName(), sbSubQuery.toString());
							}

							// sbSubQuery buffer initialize
							sbSubQuery.setLength(0);
						} else if (!subQuery.getClassName().equals("0x00")) { // Using Custom class
							try {
								for (int j = 0; subArrs != null && j < subArrs.length; j++) {
									String[] subQueryClassAry = subQuery.getClassName().split(",");
									for(String subQueryClass : subQueryClassAry){
										colunmData = CFactory.getSubInstance(subQueryClass).customData(subArrs[j][0], subQuery);
									}

									data.put(catalog[colIdx].getTagName(), colunmData);
									subMappingCnt = j; // Check Mapping column count
								}
							} catch (CustomException e) {
								Log2.error("[JobStcDyn] [ " + e.getMessage() + "]");
							}
						} else {
							for (int j = 0; subArrs != null && j < subArrs.length; j++) {
								// Add subQuery convert function by 2005.12.8
								convert = catalog[colIdx + j].getConvert();
								if (!convert.equals(""))
									subArrs[j][0] = StringUtil.convert(subArrs[j][0], convert);
								if (catalog[colIdx + j].isHtmlParse()) { // html parse
									try {
										subArrs[j][0] = HtmlUtil.getHtmlParse(subArrs[j][0].getBytes("UTF-8"));
										subArrs[j][0] = subArrs[j][0].trim();
									} catch (UnsupportedEncodingException e) {
									}
									// subArrs[j][0] = htmlUtil.removeHtmlTag(subArrs[j][0]);
								}
								// dateformat yyyyMMddHHmiss(none_time_t) type convert
								if (m_source.getDateFormat().getOrder() == colIdx + j) {
									String format = m_source.getDateFormat().getFormat();
									subArrs[j][0] = DateUtil.parseDate(subArrs[j][0], format);
								}

								// 占쎄퀣�뵠占쎄퀗占� 占쎈끏�뮉 野껋럩�뒭 Default Value�몴占쏙옙占썲칰猿뗭뵥筌욑옙占쎈벡�뵥
								if (!subQuery.getNullValue().equals("") && subArrs[j][0].equals("")) {
									data.put(catalog[colIdx + j].getTagName(), subQuery.getNullValue());
								} else {
									data.put(catalog[colIdx + j].getTagName(), subArrs[j][0]);
								}

								subMappingCnt = j; // Check Mapping column count
							}
						}

						colIdx = colIdx + subMappingCnt;
						dbIdx = dbIdx + count - 1;

						continue;
					}
				} // subquery type function end

				//Attach filtering process ...
                FilterSource filter = catalog[colIdx].getFilter();
                if(filter != null) {
                    debug("[JobStcDyn] [Filter column("+colIdx+")]");

                    String filterType = filter.getFilterType();
                    if(filterType.equals("") )
                        throw new BridgeException(":Missing <Filter filter=\"\"> setting in configuration file.");

                    AttachInfo attachInfo ;
                    int count = filter.getWhereCnt();
                    int useCnt = filter.getUseFilteringCnt();
                    String[][] arrDBColData = m_dbjob.getString(dbIdx, count);

                    if(filter.getQuery().equals("")) { //filter query empty case
                        attachInfo = new AttachInfo(count-useCnt, useCnt);
                        for(int i=0, k=0; i<count; i++) {
                            if( i >=count-useCnt ) {
                                attachInfo.addAttach(k, arrDBColData[count-useCnt][0]);
                                k++;
                            } else {
                                attachInfo.setColunmData(i, arrDBColData[i][0]);
                            }
                        }
                    } else { // filter sql query result set
                        //attachInfo = m_dbjob.getAttachInfo(filter.getQuery(), arrDBColData, filter.getSeperator(), useCnt);
                    	filter.setQuery(replaceQueryByArguments(filter.getQuery()));
                    	Log2.debug("Filter sql=" + filter.getQuery());
                        String filterQuery = m_queryGen.replaceQuery(filter.getQuery(), "%s1", strReplace) ;
                        //Add check statement 2006/02/14
                        if(filter.isStatement()){
                            filterQuery = m_queryGen.addWhereCondition(filter.getQuery(), arrDBColData) ;
                        }
                        attachInfo = m_dbjob.getAttachInfo(filterQuery, arrDBColData,
                                filter.getSeperator(), useCnt, filter.getRetrival(),
                                filter.getDir(), filter.isStatement());
                    }

                    // filter query data insert CataloigInfo
                    String[] arrAttachInfo = attachInfo.getColunmData();
                    String attachData = "";
                    if(arrAttachInfo != null){
                        if( arrAttachInfo.length >0 ) { //Filter query data insert into SCD
                            for(int i=0; i<arrAttachInfo.length; i++) {
								data.put(catalog[colIdx].getTagName(), arrAttachInfo[i]);
                                colIdx++;   // CataloigInfo column index increment
                            }
                        }

                        // filtering attach path info(String[][]) processing ...
                        if( attachInfo.getAttachse().length > 0 ) { // filtering attach file
                            
                        	 if(m_config.getArgs().isScdFilter()) {
                        		StringBuffer tmp = new StringBuffer();
                             	if ( attachInfo.getAttachse() != null && attachInfo.getAttachse().length > 0 ) {
                             		String[][] attaches = attachInfo.getAttachse();
                             		for ( int i=0; i<attaches.length; i++) {
                             			for ( int j=0; j<attaches[i].length; j++) {
                             				tmp.append(attaches[i][j]);
                             				if ( i < attaches.length-1) tmp.append(filter.getSplit());
                             			}
                             		}
                             	}
                             	attachData = tmp.toString();
                             } else {
                            	 attachData = getFilterData(filter, attachInfo.getAttachse(), docid).toString();
                             }
                        }
                    }

                    if(catalog[colIdx].isHtmlParse()) { // HTML Parsing...
                        try {
                            attachData = HtmlUtil.getHtmlParse(attachData.getBytes("UTF-8"));
                            attachData = attachData.trim();
                        } catch (Exception e) {} //Html Tag Exception
                    }
                    
                    //buffering data append                   
					data.put(catalog[colIdx].getTagName(), attachData);
                    dbIdx = dbIdx + count - 1;

                    continue;
                } // Attach filtering processing END

				// ResultSet column append... next n column append
				for (int cnt = 0; cnt < catalog[colIdx].getAppend(); cnt++) {
					if ((cnt == 0)) { // initialize
						colunmData = (m_dbjob.getString(dbIdx + cnt))[0][0];
					} else {
						colunmData = colunmData + " " + (m_dbjob.getString(dbIdx + cnt))[0][0];
					}
					if (cnt == catalog[colIdx].getAppend() - 1) {
						dbIdx = dbIdx + cnt;
					}
				}

				// Custom Class processing
				if (!catalog[colIdx].getClassName().equals("0x00")) {
					try {
						String[] customClassAry = catalog[colIdx].getClassName().split(",");
						for(String customClass : customClassAry){
							colunmData = CFactory.getInstance(customClass).customData(colunmData);
						}
					} catch (CustomException e) {
						Log2.error("[JobStcDyn] [ " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
					}
				}

				// column encoding convert
				convert = catalog[colIdx].getConvert();
				if (!convert.equals(""))
					colunmData = StringUtil.convert(colunmData, convert);

				// date format yyyyMMddHHmiss(none_time_t) type convert.
				if (m_source.getDateFormat().getOrder() == colIdx) {
					String format = m_source.getDateFormat().getFormat();
					colunmData = DateUtil.parseDate(colunmData, format);
				}

				// html parse
				if (catalog[colIdx].isHtmlParse()) {
					try {
						colunmData = HtmlUtil.getHtmlParse(colunmData.getBytes("UTF-8"));
						colunmData = colunmData.trim();
					} catch (Exception e) {

					}// Html Tag Exception
				}

				// Add Data integer type convert function by 2005.01.20
				if (catalog[colIdx].getType().equals("integer")) {
					colunmData = StringUtil.convertInteger(colunmData, (catalog[colIdx].getMaxLen()));
				}

				// SCD buffring data append
				data.put(catalog[colIdx].getTagName(), colunmData);
			} // SCD Column data proccesing ... END

			// oracle long type read bug
			// code line 300 to 500 moving
			// If oracle long type read that should be selecting ResultSet order
			if (isQueueSeq && dynMode.equals("queue") && m_mode != STATIC) {
				arrQueue = m_dbjob.getString(rsCnt);
				pkArrs[pkCnt][0] = arrQueue[0][0];
				pkArrs[pkCnt][1] = arrQueue[0][1];
			}

			// start dynamic update processing
			String[] execDynQuery = m_query.getDynExecute();
			if( execDynQuery != null && execDynQuery.length > 0 ){
				for (int i = 0; i < execDynQuery.length; i++) {
					if ( !execDynQuery[i].equals("") ) {
						int count = 0;
						execDynQuery[i] = replaceQueryByArguments(execDynQuery[i]);
						try {
							if (dynMode.equals("logdb") && m_mode != STATIC) {
								count = m_dbjob.execPQueryLog(execDynQuery[i], pkArrs, m_query.isStatement()[i]);
							} else if (dynMode.equals("queue") && m_mode != STATIC) {
								if (pkArrs == null) { // Modify v3.7 build06
									debug("[JobStcDyn] [Queue Table Delete key value is null. "
											+ "Please Checked last select column in SQLSelect]", 4);
								} else {
									count = m_dbjob.execPQuery(execDynQuery[i], pkArrs, m_query.isStatement()[i]);
								}
							}
						} catch (SQLException e) {
							error("[JobStcDyn] [SQL Error Query: " + execDynQuery + "]");
							throw new BridgeException(
									"Execute Query Fail, Error Message:" + "\n" + IOUtil.StackTraceToString(e) + "\n]");
						}
						debug("[info] [ Successful (" + count + ") ]", 3);
					}
				}
			}

			/*
			if (m_config.getArgs().isVerbose()) {
				System.out.print(sbSCDBuf.toString());
			}
			*/

			if (totalCount % 100 == 0) {
				if (totalCount % 1000 == 0 || totalCount == 0) {
					System.out.print("[" + totalCount + "]");
				} else {
					System.out.print(".");
				}
			}

			totalCount++;

			// SCD data ... write to SCD file
			isSCDWrite(scdType);
			if (isTest && (totalCount % 10 == 0)) {
				break;
			}

		} // Crawling data resultset end while

		if (!data.isEmpty()) {
			isSCDWrite(scdType);
		}

		closeAllFtClients();

		try {
			if (m_dbjob.isError()) {
				fileManager.errorClose(isTest);
			} else {
				fileManager.close(isTest);
			}
		} catch (IOException e) {
			Log2.error("[JobStcDyn][SCD close I/O Exception: " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
		}
		// Target ResultSet initialize
		m_dbjob.execCommit();

		// Target ResultSet initialize
		m_dbjob.releaseRs();

		if (m_dbjob.isError()) {
			throw new BridgeException("Database error, see error log messages\n]");
		}

		Log2.out("[info] [JobStcDyn] [Total " + getModeMsg(scdType) + " count : " + totalCount + "]");
	}

	/**
	 * All FtClient socket close
	 */
	private void closeAllFtClients() {
		// FtClient socket close
		Set ftClientSet = m_ftClients.keySet();
		Iterator iter = ftClientSet.iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			FtClient ftClient = (FtClient) m_ftClients.get(key);
			try {
				if (ftClient != null) {
					ftClient.excute(ftClient.CMD_DISCONNECT);
				}
			} catch (IOException e) {
				Log2.error("[JobStcDyn] [Filter client socket I/O exception: " + "\n" + IOUtil.StackTraceToString(e)
						+ "\n]");
			} finally {
				if (ftClient != null)
					ftClient = null;
			}
			debug("[JobStcDyn] [Filter client socket destroy success. id=" + key + "]", 3);
		}

		// dynamic mode type is reconnected fterver
		m_ftClients = new HashMap();
		debug("ft clients map inited.");
	}

	/**
	 * Create Delete Type SCD
	 * @throws DBFactoryException
	 *             error info
	 * @throws BridgeException
	 *             error info
	 */
	private void delete(String deleteTypeSql) throws DBFactoryException, BridgeException, SQLException {
		String dynMode = m_query.getDynMode();
		// String deleteQuery = "";
		// Setting Source prefix
		String srcPrefix = m_source.getRefColl().getPrefix();

		m_dbjob.setResultSet(deleteTypeSql);

		int count = m_dbjob.getTargetColumnCnt();
		long deleteCnt = 0;
		TableSchema tbls = m_source.getTblSchema();
		int pkCnt = tbls.getPrimaryKeys().length;

		boolean isQueueSeq = false;
		if (count > pkCnt)
			isQueueSeq = true;

		// Modify 2006.08.10 Queue table type. resultset end column append seq column.
		if (dynMode.equals("queue") && isQueueSeq) {
			pkCnt = pkCnt + 1;
			if (count != pkCnt) {
				error("[JobStcDyn] [Not found sequence column in Dynamic Execute SQL]");
				m_dbjob.releaseRs();
				System.exit(-1);
			}
		}

		LinkedHashMap<String, String> deleteData = new LinkedHashMap();

		// Add MD5 CONVERT function by 2008.06.18
		InfoSet[] catalog = m_collection.getCatalog();

		while (!m_dbjob.isError() && m_dbjob.next()) {
			String docid = "";
			String[][] pkArrs = new String[pkCnt][2];
			// logdb update
			for (int idx = 1; idx <= pkCnt; idx++) {
				String[][] arrPk = m_dbjob.getString(idx);
				pkArrs[idx - 1][0] = arrPk[0][0]; // DOCID
				pkArrs[idx - 1][1] = arrPk[0][1];
				if (isQueueSeq && dynMode.equals("queue") && idx == pkCnt) {
					docid += "";
				} else {
					docid += arrPk[0][0];
				}
			}
			String[] execDynQuery = m_query.getDynExecute();
			boolean[] isStatement = m_query.isStatement();
			
			int uCount = 0;
			if( execDynQuery.length > 0){
				for (int i = 0; i < execDynQuery.length; i++) {
					if (!execDynQuery[i].equals("")) {
						execDynQuery[i] = replaceQueryByArguments(execDynQuery[i]);
						try {
							if (dynMode.equals("logdb")) {
								uCount = m_dbjob.execPQueryLog(execDynQuery[i], pkArrs, isStatement[i]);
							} else if (dynMode.equals("queue")) {
								uCount = m_dbjob.execPQuery(execDynQuery[i], pkArrs, isStatement[i]);
							}
						} catch (SQLException e) {
							throw new BridgeException("JobStcDyn Class  Log Table Delete Fail, " + " Query:"
									+ execDynQuery + ", msg: " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
						}
					}
				}
			}
			debug("[info] [ Successful (" + uCount + ") ]", 3);
			// Add DOCID MD5 function by 2008.06.18
			if (catalog[0].isMd5()) {
				docid = EncryptUtil.MD5(docid);
			}

			deleteData.put("DOCID", srcPrefix + docid);
			try {
				fileManager.delete(deleteData);
				deleteData.clear();
			} catch (IOException e) {
				Log2.error("[JobStcDyn][Delete SCD write  I/O Exception: " + "\n" + IOUtil.StackTraceToString(e)
						+ "\n]");
			}
			deleteCnt++;
		}
		try {
			if (m_dbjob.isError()) {
				fileManager.errorClose(isTest);
			} else {
				fileManager.close(isTest);
			}
		} catch (IOException e) {
			Log2.error("[JobStcDyn][Delete SCD close  I/O Exception: " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
		}
		// Target ResultSet initialize
		m_dbjob.execCommit();

		// Target ResultSet initialize
		m_dbjob.releaseRs();

		if (m_dbjob.isError()) {
			throw new BridgeException("Database error, see error log messages\n]");
		}

		Log2.out("[info] [JobStcDyn] [Delete SCD count: " + deleteCnt + "]");
	}

	/**
	 * Attach file filtering method
	 * local -> local directory filtering
	 * remote -> use ftserver
	 * @param filter
	 *            FilterSource object
	 * @param attachInfo
	 *            attach configuration info
	 * @return StringBuffer filtered data
	 * @throws BridgeException
	 *             error info
	 * @throws FilterException
	 *             error info
	 */
	private StringBuffer getFilterData(FilterSource filter, String[][] attaches, String docid) throws BridgeException,
			FilterException {

		StringBuffer sbFilter = new StringBuffer();

		if (filter.getRetrival().equals("local")) {
			// IFilter ifilter = new FilterFactory().getInstance(filter.getClassName(),
			// new Boolean(m_config.getArgs().isFilterFileDelete()));
			IFilter ifilter = filterQueue(filter.getClassName());
			if (ifilter == null) {
				throw new FilterException(":Not Found this Class in Config File, "
						+ "Please Check '<Filter className' in config file");
			}

			// When Occur Filtering Exception, DOCID Error Log Writing @JunSeok.Jung
			try {
				sbFilter.append(ifilter.getFilterData(attaches, filter, m_source.getScdCharSet()));
			} catch (Exception e) {
				Log2.error("[JobStcDyn] [getFilterData] Filtering Error DOCID = " + docid);
				e.printStackTrace();
			}
		} else if (filter.getRetrival().equals("blob")) {
			IFilter ifilter = filterQueue(filter.getClassName());
			if (ifilter == null) {
				throw new FilterException(":Not Found this Class in Config File, "
						+ "Please Check '<Filter className' in config file");
			}

			try {
				sbFilter.append(ifilter.getFilterData(attaches, filter, m_source.getScdCharSet()));
			} catch (Exception e) {
				Log2.error("[JobStcDyn] [getFilterData] Filtering Error DOCID = " + docid);
				e.printStackTrace();
			}
		} else if (filter.getRetrival().equals("url")) {
			IFilter ifilter = filterQueue(filter.getClassName());
			if (ifilter == null) {
				throw new FilterException(":Not Found this Class in Config File, "
						+ "Please Check '<Filter className' in config file");
			}

			try {
				sbFilter.append(ifilter.getFilterData(attaches, filter, m_source.getScdCharSet()));
			} catch (Exception e) {
				Log2.error("[JobStcDyn] [getFilterData] Filtering Error DOCID = " + docid);
				e.printStackTrace();
			}
		} else if (filter.getRetrival().equals("ftserver")) {
			String szServerID = filter.getServerid(); // FtServer ID
			if (szServerID.equals("")) {
				throw new BridgeException(":Missing <FilterInfo> - <Filter serverid=\"\"> "
						+ "setting in configuration file.");
			} else {
				FtServerConfig fsConfig;
				if (m_config.getFtServerConfMap() != null) {
					fsConfig = (FtServerConfig) m_config.getFtServerConfMap().get(szServerID);
				} else {
					throw new BridgeException(":Missing " + "<FtServer> - <server> setting in configuration file.");
				}

				if (fsConfig == null) {
					throw new BridgeException(":Missing " + "<FtServer> - <server> setting in configuration file. "
							+ "(<Id>" + szServerID + "</Id>)");
				} else {
					try {
						int size = fsConfig.getServerName().length;

						FtClient ftClient = (FtClient) m_ftClients.get(fsConfig.getId());

						if (ftClient == null) {
							for (int n = 0; n < size; n++) {
								try {
									ftClient = new FtClient(m_config.getArgs().getSrcid(), fsConfig.getServerName()[n],
											fsConfig.getPortNumber());
									m_ftClients.put(fsConfig.getId(), ftClient);
								} catch (Exception e) {
									if (n == (size - 1) && ftClient == null) {
										throw new BridgeException(": Filter Client Socket," + "\n"
												+ IOUtil.StackTraceToString(e) + "\n]");
									} else {
										debug("[JobStcDyn][FtServer Connection][Can't Connection Server: "
												+ fsConfig.getServerName()[n] + ",Trying Next Server Connection]", 2);
										continue;
									}
								}
								log("[info] [JobStcDyn] [Connection FtServer IP Address :" + " "
										+ fsConfig.getServerName()[n] + ", id=" + fsConfig.getId() + "]");
								break;
							}
						}

						if (ftClient.getFilterData(attaches, filter, m_source.getScdCharSet(), m_config.getArgs()
								.isFilterFileDelete())) {
							sbFilter.append(ftClient.receive());
							if (sbFilter.toString().equals("MSG:EXCEPTION")) {
								Log2.debug("[JobStcDyn] [Filter Client Message Exception]", 2);
								sbFilter = new StringBuffer();
								ftClient.destory();
								ftClient = null;
							}
						}
					} catch (IOException e) {
						error("[JobStcDyn] [Bridge Socket Exception][Please check the "
								+ "<FtServer> - <server> - <PortNumber> (" + fsConfig.getPortNumber() + ")]");
						throw new BridgeException(": Filter Client Socket," + "\n" + IOUtil.StackTraceToString(e)
								+ "\n]");
					} catch (Exception e) {
						Log2.error("[JobStcDyn] [getFilterData] Filtering Error DOCID = " + docid);
						e.printStackTrace();
					}
				}
			}
		}

		return sbFilter;
	} // END getFilterData

	/**
	 * Filtering custom class process method
	 * @param className
	 *            custom classname
	 * @return IFilter Interface object
	 * @throws FilterException
	 *             error info
	 * @throws BridgeException
	 *             error info
	 */
	private IFilter filterQueue(String className) throws FilterException, BridgeException {
		IFilter filterClass = (IFilter) m_filters.get(className);
		if (filterClass == null) {
			try {
				filterClass = new FilterFactory().getInstance(className, new Boolean(m_config.getArgs()
						.isFilterFileDelete()), filteredTextDir);
				if (filterClass == null) {
					throw new FilterException(": Unable to load the java class. "
							+ "Please check the <Filter className>");
				} else {
					m_filters.put(className, filterClass);
				}
			} catch (BridgeException e) {
				throw new BridgeException(IOUtil.StackTraceToString(e));
			}
		}
		return filterClass;
	}

	private boolean isSCDWrite(int mode) {
		try {
			switch (mode) {
			case INSERT:
				fileManager.insert(data); // Insert Type Document
				break;
			case UPDATE:
				fileManager.update(data); // Update Type Document
				break;
			case REPLACE:
				fileManager.replace(data); // Replace Type Document
				break;
			case DELETE:
				break;
			default:
				error("[Bridge][SCD write mode not found]");
				return false;
			}

			data.clear();
		} catch (IOException e) {
			error("[Bridge][SCD I/O Exception: " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
			return false;
		}
		return true;
	}

	private String getModeMsg(int mode) {
		String printMsg = "";
		switch (mode) {
		case INSERT:
			printMsg = "Insert";
			break;
		case UPDATE:
			printMsg = "Update";
			break;
		case REPLACE:
			printMsg = "Replace";
			break;
		case DELETE:
			break;
		default:
			return "";
		}
		return printMsg;
	}

	public boolean runScdFilter() throws BridgeException {
		int scdType = 0;
		try {
			// scd/index : last destination folder after filtering
			File indexDir = null;

			if (m_config.getArgs().isUserIndexDir()) {
				FileUtil.makeDir(m_source.getScdDir().getPath(), "index");
				indexDir = new File(m_source.getScdDir().getPath(), "index");
			} else if (m_mode == IJob.STATIC) {
				FileUtil.makeDir(m_source.getScdDir().getPath(), "static");
				indexDir = new File(m_source.getScdDir().getPath(), "static");
			} else if (m_mode == IJob.DYNAMIC || m_mode == IJob.REPLACE) {
				FileUtil.makeDir(m_source.getScdDir().getPath(), "dynamic");
				indexDir = new File(m_source.getScdDir().getPath(), "dynamic");
			} else {
				indexDir = new File(m_source.getScdDir().getPath());
			}
			
			String sourceId = m_config.getArgs().getSrcid();

			// scd/temp : temp folder
			File tempDir = new File(m_source.getScdDir().getPath(), sourceId + "-temp");
			String newScdFilePath = "";
			File tempScd = null;
			if (tempDir != null && tempDir.exists()) {
				File[] sourceFile = tempDir.listFiles();
				int filelist_size = sourceFile.length;
				if (filelist_size > 0) {
					for (int i = 0; i < filelist_size; i++) {
						texFileReplace(sourceFile[i], indexDir);
					}
				}
			}

			// starting SCD Filter job
		} catch (Exception e) {
			m_dbjob.releaseDB();
			throw new BridgeException(": JobStcDyn Class," + "\n" + IOUtil.StackTraceToString(e) + "\n]");
		} finally {
		}
		try {
			fileManager.close(false);
		} catch (IOException e) {
			error("[Bridge][SCD Close I/O Exception: " + "\n" + IOUtil.StackTraceToString(e) + "\n]");
			return false;
		}
		// Target Connection Release
		m_dbjob.releaseDB();
		return true;
	}

	/**
	 * 
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 *             modified by joonskwon
	 */
	public void texFileReplace(File file, File indexDir) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("File should not be null.");
		}
		if (!file.exists()) {
			throw new FileNotFoundException("File does not exist: " + file);
		}
		if (!file.isFile()) {
			throw new IllegalArgumentException("Should not be a directory: " + file);
		}
		if (!file.canWrite()) {
			throw new IllegalArgumentException("File cannot be written: " + file);
		}
		File tempFile = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			
			String line = "";
			StringBuffer tmp = new StringBuffer();
			String tagName = "";
			String content = "";
			InfoSet[] catalog = m_collection.getCatalog();
			int tagCnt = catalog.length;
			HashMap<String,FilterSource> hmFilterTag = new HashMap<String,FilterSource>();
			HashMap<String,Boolean> isHtmlCategory = new HashMap<String, Boolean>();
			tempFile = new File(file.getAbsolutePath()+".temp");
			
			
			
			String charset = m_source.getScdCharSet();
			if(charset.equalsIgnoreCase("UTF-8"))
			{
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8"));
			}
			else
			{
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),charset));
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), charset));
			}
			
    		for(int colIdx=0; colIdx < tagCnt ; colIdx++) 
    		{
    			boolean isSCDFilter = catalog[colIdx].isSCDFilter();
    			if(!isSCDFilter) continue;

    			FilterSource filtersource = null;
    			int filterCnt = colIdx;
    			while(true) {
    				filtersource =  catalog[filterCnt].getFilter();
    				
    				if(filtersource != null) {
    					break;
    				} else {
    					--filterCnt;
    				}
    			}
    			
    			hmFilterTag.put(catalog[colIdx].getTagName(), filtersource);
    			
    			if(catalog[colIdx].isHtmlParse())
    			{
    				isHtmlCategory.put(catalog[colIdx].getTagName(), true);
    			}
    			else
    			{
    				isHtmlCategory.put(catalog[colIdx].getTagName(), false);
    			}
    		}
    		
    		
    		int crawlCnt = 0;
    		int bIdx = 0;
			while ((line = reader.readLine()) != null) {
    			int idx = line.indexOf(">");
    			
    			if ( line != null && idx > 1 ) {
    				tagName = line.substring(1, idx);
    				FilterSource filtersource = hmFilterTag.get(tagName);
    				Boolean isHtml = isHtmlCategory.get(tagName);
	    			if ( filtersource != null ) {
	    				content = line.substring(idx+1);
	    				if ( content != null && !"".equals(content) ) {
			                StringTokenizer stResult = new StringTokenizer(content,filtersource.getSplit());
			                String[] atts = new String[stResult.countTokens()];
			                int cnt = 0;
			                
			                AttachInfo attInfo ;
			                if(atts != null) {
		                        int size = atts.length;
		                        attInfo = new AttachInfo(size, 1);
		    	                while (stResult.hasMoreTokens()) {
		    	                	atts[cnt] = stResult.nextToken();
		    	                	if(atts[cnt] != null && !atts[cnt].equals("")) {
		                                attInfo.addAttach(0, atts[cnt]);
		                            }
		    	                	cnt++;
		    	                }
		    	                
		    	                String attachData = getFilterData(filtersource, attInfo.getAttachse(), "").toString();
		    	                
		    	                if(isHtml)
		    	                {
		    	                	attachData = HtmlUtil.getHtmlParse(attachData.getBytes("UTF-8"));
		    	                }
		                        tmp.append("<").append(tagName).append(">"); 
		         				tmp.append(attachData);
		         				line = replace(content, content, tmp.toString());
								tmp.delete(0,tmp.length());
			                }
	    				}
	    				if (crawlCnt % 100 == 0) {
	    	                if (crawlCnt % 1000 == 0 || crawlCnt == 0) {
	    	                    System.out.print("[" + crawlCnt + "]");
	    	                } else {
	    	                    System.out.print(".");
	    	                }
	    	            }
	    				
	    				crawlCnt++;
	    			}
    			}
    			
    			if((tempFile.length() > (2048 * 1000 * 1000)) && tagName.startsWith("DOCID")) {
					writer.close();
					
					String scdIndex = String.format("%02d", ++bIdx);
			
					tempFile = new File(file.getAbsolutePath().replace("B-00", "B-" + scdIndex) +".temp");
					
					if(charset.equalsIgnoreCase("UTF-8"))
					{
						writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8"));
					}
					else
					{
						writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), charset));
					}
				}
    			
				if(!"".equals(line)) 
				{
					writer.write(line);
					writer.newLine();
					writer.flush();
				}
			}
			writer.close();
			reader.close();
			file.delete();
			//tempFile.renameTo(file);
			
			String sourceId = m_config.getArgs().getSrcid();

			// scd/temp : temp folder
			File tempDir = new File(m_source.getScdDir().getPath(), sourceId + "-temp");
			File tempScd = null;
			File[] sourceFile = tempDir.listFiles();
			int filelist_size = sourceFile.length;
			if (filelist_size > 0) {
				int bIndex = 0;
				for (int i = 0; i < filelist_size; i++) {
					if(sourceFile[i].getName().endsWith(".temp")) {
						String scdIndex = String.format("%02d", bIndex);
						String newScdFilePath = indexDir.getAbsolutePath() + FileUtil.fileseperator + sourceFile[i].getName().replace("B-00", "B-" + scdIndex).replace(".temp", "");
						sourceFile[i].renameTo(new File(newScdFilePath));
						log("[info][JobStcDyn][Temp SCD move to Index Dir : " + sourceFile[i].getAbsolutePath() + "->"
								+ newScdFilePath);
						tempScd = new File(sourceFile[i].getAbsolutePath() + ".temp");
						if (tempScd != null && tempScd.exists())
							tempScd.delete();
						debug("[info][JobStcDyn][Temp SCD delete : " + tempScd.getAbsolutePath());
						
						bIndex++;
					}
				}
			}
			
			Log2.out("[info] [texFileReplace] [Total SCD count : " + crawlCnt+"]");
	        
		} catch (Exception e) {

			if (writer != null) {
				try {
					writer.close();
				} catch (IOException er) {
					Log2.error("[FilterToSCD] [Filter I/O exception: " + "\n" + IOUtil.StackTraceToString(er) + "\n]");
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException er) {
					Log2.error("[FilterToSCD] [Filter I/O exception: " + "\n" + IOUtil.StackTraceToString(er) + "\n]");
				}
			}

			closeAllFtClients();
		}

	}

	/**
	 * 
	 * @param inString
	 * @param oldPattern
	 * @param newPattern
	 * @return String
	 *         modified by joonskwon
	 */
	public String replace(String inString, String oldPattern, String newPattern) {

		if (inString == null) {
			return null;
		}
		if ("".equals(inString)) {
			return inString;
		}

		if (oldPattern == null || newPattern == null) {
			return inString;
		}
		if (newPattern == null || (newPattern != null && "".equals(newPattern.trim()))) {
			return newPattern;
		}

		StringBuffer sbuf = new StringBuffer();

		int pos = 0;
		int index = inString.indexOf(oldPattern);

		int patLen = oldPattern.length();
		while (index >= 0) {
			sbuf.append(inString.substring(pos, index));
			sbuf.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sbuf.append(inString.substring(pos));

		return sbuf.toString();
	}
	
	private String replaceQueryByArguments(String query) {
		List<String> argument_names = RunTimeArgs.getParamName();
		List<String> argument_values = RunTimeArgs.getParamValue();
		int arg_size = (argument_names != null) ? argument_names.size() : 0;
		if(arg_size == argument_values.size()) {
			for(int i=0;i<arg_size; i++) {
				query = query.replaceAll("%"+argument_names.get(i), argument_values.get(i));
			}
		}
		return query;
	}
} // END Class