/*
 * @(#)GetSource.java 3.8.1 2009/03/11
 */
package kr.co.wisenut.bridge3.config.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import kr.co.wisenut.bridge3.config.catalogInfo.GetCatalogInfo;
import kr.co.wisenut.bridge3.config.catalogInfo.Mapping;
import kr.co.wisenut.bridge3.config.source.node.RefColl;
import kr.co.wisenut.bridge3.config.source.node.SCDdir;
import kr.co.wisenut.bridge3.config.source.node.XmlQuery;
import kr.co.wisenut.bridge3.config.source.node.XmlValue;
import kr.co.wisenut.bridge3.job.CFactory;
import kr.co.wisenut.common.Exception.ConfigException;
import kr.co.wisenut.common.filter.FilterSource;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.common.util.XmlUtil;

import org.jdom.Element;

/**
 * 
 * GetSource
 * 
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 * 
 * @author WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 * 
 */
public class GetSource extends XmlUtil {
	private String srcid = null;
	private Element element = null;

	public GetSource(String path, String srcid) throws ConfigException {
		super(path);
		HashMap m_source_map = getElementHashMap("Source");
		this.srcid = srcid;
		element = (Element) m_source_map.get(srcid);
		if (element == null) {
			throw new ConfigException(": Missing <Source id=\"" + srcid + "\"> " + "setting in configuration file.");
		}
	}

	/**
	 * GetCatalogInfo Return function
	 * @return Mapping Class
	 * @throws ConfigException
	 *             error
	 */
	public Mapping getCollection() throws ConfigException {
		return new GetCatalogInfo(element).getCatalogInfo();
	}

	public Source getSource() throws ConfigException {
		boolean isCharSet = false;
		Source source = new Source();
		if (element == null) {
			throw new ConfigException(": Missing " + "<Source id=" + srcid + "> setting in configuration file."
					+ "Could not parse Source Config.");
		}
		// setting sourcealias
		source.setSourceaias(getElementValue(element, "sourcealias", ""));

		// setting SCD charset.add 2005.01.20
		if (element.getAttribute("charset") != null) {
			source.setScdCharSet(element.getAttribute("charset").getValue());
			Log2.out("SCD File Encoding: " + element.getAttribute("charset").getValue());
			isCharSet = true;
		}

		// setting file extension
		if (element.getAttribute("extension") != null) {
			String extension = element.getAttribute("extension").getValue();
			if (!extension.equalsIgnoreCase("scd") && !extension.equalsIgnoreCase("json")) {
				throw new ConfigException("Please Check Element extension. extension value allow scd and json");
			}

			source.setExtension(extension);
		}

		// <Date format="none_time_t" dbformat="none" order="1"/>
		if (element.getChild("Date") != null) {
			int dateOrder;
			try {
				dateOrder = Integer.parseInt(getElementValue(element.getChild("Date"), "order"));
			} catch (NumberFormatException ne) {
				throw new ConfigException(": Missing " + "<Date order=\"number\"/> setting in configuration file. "
						+ "Please specify the numbers 1 ~ 32. Could not parse Date Config.");
			}

			String dateFormat = getElementValue(element.getChild("Date"), "dbformat", "none");
			source.setDataFormat(dateOrder, dateFormat);
		}

		if (element.getChild("SCDdir") != null) {
			SCDdir scdDir = new SCDdir();
			String path = getElementValue(element.getChild("SCDdir"), "path");
			String sourceIdx = getElementValue(element.getChild("SCDdir"), "idx", "00");

			scdDir.setPath(path);

			if (Integer.parseInt(sourceIdx) < 100 && Integer.parseInt(sourceIdx) > -1) {
				scdDir.setIdx(sourceIdx);
			} else {
				throw new ConfigException("Please Check Attribute [idx] in SCDdir Node. idx's value allow between 00~99");
			}

			source.setScdDir(scdDir);
		} else {
			throw new ConfigException(": Missing " + "<SCDdir path=\"SCD File Path\" idx=\"Source Idx(Option)\" /> setting in configuration file."
					+ "Could not parse SCDdir Config.");
		}

		if (element.getChild("RemoteSCDdir") != null) {
			source.setRemoteDir(getElementValue(element.getChild("RemoteSCDdir"), "path"));
		} else {
			source.setRemoteDir(getElementValue(element.getChild("SCDdir"), "path"));
		}
		
		if (element.getChild("RefColl") != null) {
			RefColl refColl = new RefColl();
			
			refColl.setId( getElementValue(element.getChild("RefColl"), "id", "") );
			refColl.setPrefix( getElementValue(element.getChild("RefColl"), "prefix", "") );
			
			source.setRefColl(refColl);
		}

		// <RefCBFS collid="tech" prefix="#001" charset="UTF-8" />
		
		/*
		if (element.getChild("RefCBFS") != null) {
			source.setRefCBFS(getElementValue(element.getChild("RefCBFS"), "collid", ""));
			source.setSrcPrefix(getElementValue(element.getChild("RefCBFS"), "prefix", ""));

			if (!isCharSet) {
				source.setScdCharSet(getElementValue(element.getChild("RefCBFS"), "charSet", "UTF-8"));
			}
		}*/

		// <DSN type="target" dsn="dbsrcid" encrypt="y"/>
		source.setTargetDSN(getElementListValue(element.getChildren("DSN"), "target"));
		source.setLogDSN(getElementListValue(element.getChildren("DSN"), "log"));

		// <SubQuery>
		source.setSubQuery(getSubQueryConf(element));

		// <SubMemory>
		source.setSubMemory(getSubMemoryConf(element));
		
		// <XmlQuery>
		source.setXmlQuery(getXmlQueryConf(element));

		// <FilterInfo>
		source.setFilterSource(getFilterConf(element));

		// order id cross duplicate check.
		if (isDuplecateOrderNum(source.getSubQuery(), source.getSubMemory(), source.getFilterSource())) {
			throw new ConfigException(": Duplicated \"order=\" value in <SubQuery>, <MemorySelect>, <FilterInfo>"
					+ " in configuration file.");
		}

		// <Query>
		source.setQuery(getQueryConf(element));

		// <TableSchema
		source.setTblSchema(getTableSchema(element));

		source.setRemoteinfo(getRemoteConf(element));

		source.setCustomServerInfo(getCustomServerConf(element));

		return source;
	}

	/**
	 * Get DNS ID
	 * @param list
	 *            Element list
	 * @param mode
	 *            database type
	 * @return dsn id
	 * @throws ConfigException
	 *             error info
	 */
	protected String getElementListValue(List list, String mode) throws ConfigException {
		String retStr = "";
		Element element;

		for (int i = 0; i < list.size(); i++) {
			element = (Element) list.get(i);
			String value = getElementValue(element, "type");
			if (!value.equals("") && value.equals(mode)) {
				if (mode.equals("target")) {
					retStr = getElementValue(element, "dsn");
				} else {
					retStr = getElementValue(element, "dsn", "");
				}
				break;
			}
		}
		return retStr;
	}
	
	private XmlQuery[] getXmlQueryConf(Element element) throws ConfigException {
		List xmlNodes;
		XmlQuery[] querySource;
		Element eleXmlQuery = element.getChild("XmlQuery");
		
		if(eleXmlQuery != null) {
			xmlNodes = getChildrenElementList(eleXmlQuery, "Xml");
		} else {
			return null;
		}
		
		if(xmlNodes == null) {
			return null;
		}
		if(xmlNodes.size() == 0) {
			Log2.debug("[GetSource] [Missing <XmlQuery> = <Xml> Setting in configuration file.]", 3);
		}
		
		int size = xmlNodes.size();
		
		querySource = new XmlQuery[size];
		String[] duplecateCheckOrders = new String[size];
		
		for (int idx = 0; idx < size; idx++) {
			querySource[idx] = new XmlQuery();
			String orderStr = getElementValue((Element) xmlNodes.get(idx), "order");
			
			if (StringUtil.isExistArray(duplecateCheckOrders, orderStr)) {
				throw new ConfigException(": Duplicated <XmlQuery><Xml order=\"\" /> in configuration file.");
			}
			
			duplecateCheckOrders[idx] = orderStr;
			querySource[idx].setOrder(Integer.parseInt(orderStr));
			querySource[idx].setSeperator(getElementValue((Element) xmlNodes.get(idx), "seperator", " ", false));
			
			String query = getElementChildText((Element) xmlNodes.get(idx), "sql", "");
			if(query.equals("")) {
				throw new ConfigException(": Missing <XmlQuery> - sql=\"\" /> setting in configuration file.");
			}
			querySource[idx].setQuery(query);
			
			int wherecnt;
			try {
				String attr_wherecnt = getElementValue((Element) xmlNodes.get(idx), "wherecnt", "1");
				if(attr_wherecnt.trim().equals("")) {
					attr_wherecnt = "1";
				}
				wherecnt = Integer.parseInt(attr_wherecnt);
			} catch(Exception e) {
				throw new ConfigException(": Please check the <XmlQuery> - <Xml wherecnt=\"\"> in configuration file.");
			}
			querySource[idx].setWherecnt(wherecnt);
			
			List xmlValueNodes = getChildrenElementList((Element) xmlNodes.get(idx), "XmlValue");
			XmlValue[] xmlValues = new XmlValue[xmlValueNodes.size()];
			for(int idx_j = 0; idx_j < xmlValueNodes.size(); idx_j++) {
				xmlValues[idx_j] = new XmlValue();
				
				xmlValues[idx_j].setType(getElementValue((Element) xmlValueNodes.get(idx_j), "type"));
				xmlValues[idx_j].setPath(getElementValue((Element) xmlValueNodes.get(idx_j), "path"));
				xmlValues[idx_j].setAttr(getElementValue((Element) xmlValueNodes.get(idx_j), "attr", ""));
			}
			querySource[idx].setXmlValues(xmlValues);
		}
		
		return querySource;
	}

	/**
	 * subquery configuration infomation method
	 * @param element
	 *            XML element
	 * @return subquery configuration infomation
	 * @throws ConfigException
	 *             error info
	 */
	private SubQuery[] getSubQueryConf(Element element) throws ConfigException {
		List elementList;
		SubQuery[] querySource;
		Element eleSubQuery = element.getChild("SubQuery");
		if (eleSubQuery != null) {
			elementList = getChildrenElementList(eleSubQuery, "Sub");
			if (elementList.size() == 0) {
				elementList = getChildrenElementList(element.getChild("SubQuery"), "Query");
			}
		} else {
			return null;
		}
		if (elementList == null)
			return null;
		if (elementList.size() == 0) {
			Log2.debug("[GetSource] [Missing <SubQuery> - <Query> setting in configuration file.]", 3);
		}

		int size = elementList.size();
		querySource = new SubQuery[size];
		String[] duplecateCheckOrders = new String[size];
		for (int i = 0; i < size; i++) {
			querySource[i] = new SubQuery();
			String orderStr = getElementValue(((Element) elementList.get(i)), "order");

			if (StringUtil.isExistArray(duplecateCheckOrders, orderStr)) {
				throw new ConfigException(": Duplicated <SubQuery>" + "<Sub order=\"\"> in configuration file.");
			}
			duplecateCheckOrders[i] = orderStr;
			querySource[i].setOrder(Integer.parseInt(orderStr));
			querySource[i].setType(getElementValue(((Element) elementList.get(i)), "type"));
			querySource[i].setSplit(getElementValue(((Element) elementList.get(i)), "split", " ", false));
			querySource[i].setClassName(getElementValue(((Element) elementList.get(i)), "className", "0x00", false));
			querySource[i].setSeperator(getElementValue(((Element) elementList.get(i)), "seperator", " ", false));
			querySource[i].setNullValue(getElementValue(((Element) elementList.get(i)), "nullvalue", "", false));

			String query = getElementChildText(((Element) elementList.get(i)), "sql", "");
			if (query.equals("")) {
				throw new ConfigException(": Missing <SubQuery> - <Query sql=\"\" /> setting in configuration file."
						+ "Please check the sql attribute.");
			}
			querySource[i].setQuery(query);

			// subquery column count. Default = 1
			int count;
			String szCount = "1";
			int mode = -1;
			try {
				szCount = getElementValue(((Element) elementList.get(i)), "wherecnt", "1");
				if (szCount.equals("")) {
					szCount = "1";
				}
				count = Integer.parseInt(szCount);
			} catch (Exception e) {
				throw new ConfigException(": Please check the <SubQuery> - <Sub wherecnt=\"\"> "
						+ "in configuration file. (wherecnt=\"" + szCount + "\")");
			}
			querySource[i].setWhereCnt(count);

			try {
				szCount = getElementValue(((Element) elementList.get(i)), "maxCount", "1");
				if (szCount.equals("")) {
					szCount = "1";
				}
				count = Integer.parseInt(szCount);
			} catch (Exception e) {
				throw new ConfigException(": Please check the <SubQuery> - <Sub maxCount=\"\"> "
						+ "in configuration file. (maxCount=\"" + szCount + "\")");
			}
			querySource[i].setMaxCount(count);

			try {
				if (getElementValue((Element) elementList.get(i), "mode", "-1").equals("static")) {
					mode = 1;
				} else if (getElementValue((Element) elementList.get(i), "mode", "-1").equals("dynamic")) {
					mode = 2;
				}
			} catch (Exception e) {
				throw new ConfigException(": Please check the <SubQuery> - <Sub mode=\"\"> "
						+ "in configuration file. (mode=\"" + mode + "\")");
			}
			querySource[i].setSubquerymode(mode);
			querySource[i].setStatement(getElementValue((((Element) elementList.get(i)).getChild("sql")), "statement",
					"n", false));

			// <SubCustomClass> check
			Element subCustomClass = ((Element) elementList.get(i)).getChild("SubCustomClass");
			if (subCustomClass != null) {
				try {
					List subMappingList = getChildrenElementList(subCustomClass, "SubMapping");
					if (subMappingList.size() > 0) {
						HashMap subCustomClassMap = new HashMap();
						Iterator iter = subMappingList.iterator();
						while (iter.hasNext()) {
							Element subMapping = (Element) iter.next();
							String index = getElementValue(subMapping, "index").trim();
							String className = getElementValue(subMapping, "className").trim();

							if (StringUtil.isNullOrEmpty(index)) {
								throw new ConfigException("Is blank <SubMapping index=\"" + index + "\"> ");
							}
							if (StringUtil.isNullOrEmpty(className)) {
								throw new ConfigException("Is blank <SubMapping className=\"" + className + "\"> ");
							}
							// index value duplicated check
							if (subCustomClassMap.containsKey(index)) {
								throw new ConfigException("Duplicated <SubMapping index=\"" + index + "\"> ");
							}

							subCustomClassMap.put(index, className);
						}
						querySource[i].setSubMappingMap(subCustomClassMap);
					}
				} catch (Exception e) {
					throw new ConfigException(": Please check the <SubCustomClass> : " + e.getMessage());
				}
			}// <SubCustomClass> check end

		} // <Sub> count
		return querySource;
	}

	/**
	 * SubMemory node config read
	 * @param sourceElement
	 * @return submemory config
	 * @throws ConfigException
	 */
	private SubMemory getSubMemoryConf(Element sourceElement) throws ConfigException {
		Element eleSubMemory = sourceElement.getChild("SubMemory");

		if (eleSubMemory == null) {
			return null;
		}

		HashMap memoryTableMap = parseMemoryTableList(eleSubMemory);

		SubMemory subMemory = new SubMemory();

		// MemorySelect set
		List memorySelectElementList = getChildrenElementList(eleSubMemory, "MemorySelect");
		if (memorySelectElementList == null || memorySelectElementList.size() == 0) {
			return null;
		}

		Iterator it = memorySelectElementList.iterator();
		while (it.hasNext()) {
			Element memorySelectElement = (Element) it.next();
			String order = getElementValue(memorySelectElement, "order");

			if (order.equals("")) {
				throw new ConfigException("MemorySelect.order is blank.");
			}

			String refMemoryTableId = getElementValue(memorySelectElement, "refmemorytableid");
			if (!memoryTableMap.containsKey(refMemoryTableId)) {
				throw new ConfigException("MemoryTable is not found. <refmemorytableid: " + refMemoryTableId + ">");
			}
			MemoryTable memoryTable = (MemoryTable) memoryTableMap.get(refMemoryTableId);

			String useIdxStr = getElementValue(memorySelectElement, "useidx");
			int useIdx = 0;
			try {
				useIdx = Integer.parseInt(useIdxStr);
			} catch (Exception e) {
				throw new ConfigException("MemorySelect.useidx  must be int type. <" + useIdxStr + ">");
			}

			if (!checkUseIdx(memoryTable.getUseCount(), useIdx)) {
				throw new ConfigException(
						"MemorySelect.useidx must be grater than 0 and equal to or lass than reference MemoryTable.usecount. <"
								+ useIdx + ">");
			}

			String seperator = getElementValue(memorySelectElement, "seperator", "", false);
			String className = getElementValue(memorySelectElement, "classname", "");

			if (!className.equals("")) {
				// 而ㅼ뒪�� �겢�옒�뒪媛� �쑀�슚�븳吏� 泥댄겕
				try {
					CFactory.getInstance(className).customData("test");
				} catch (Exception e) {
					throw new ConfigException("MemorySelect.classname in Invalid. <" + className + ">");
				}
			}

			MemorySelect memorySelect = new MemorySelect();
			memorySelect.setOrder(order);
			memorySelect.setUseIdx(useIdx);
			memorySelect.setSeperator(seperator);
			memorySelect.setClassName(className);
			memorySelect.setRefMemoryTable(memoryTable);

			try {
				subMemory.putAbailableMemoryTable(memoryTable);
				subMemory.putMemorySelect(memorySelect);
			} catch (Exception e) {
				throw new ConfigException("MemorySelect config parse failed. " + e.getMessage());
			}
		}

		return subMemory;
	}

	private HashMap parseMemoryTableList(Element eleSubMemory) throws ConfigException {
		HashMap map = new HashMap();
		// MemoryTable set
		List memoryTableElementList = getChildrenElementList(eleSubMemory, "MemoryTable");
		if (memoryTableElementList == null || memoryTableElementList.size() == 0) {
			return null;
		}

		Iterator it = memoryTableElementList.iterator();
		while (it.hasNext()) {
			Element memoryTableElement = (Element) it.next();

			String id = getElementValue(memoryTableElement, "id");

			if (id.equals("")) {
				throw new ConfigException("MemoryTable.id is blank.");
			}

			String keyCountStr = getElementValue(memoryTableElement, "keycount");
			int keyCount = 0;
			try {
				keyCount = Integer.parseInt(keyCountStr);
			} catch (Exception e) {
				throw new ConfigException("MemorySelect.keycount must be int type. <" + keyCountStr + ">");
			}

			String useCountStr = getElementValue(memoryTableElement, "usecount");
			int useCount = 0;
			try {
				useCount = Integer.parseInt(useCountStr);
			} catch (Exception e) {
				throw new ConfigException("MemorySelect.usecount must be int type. <" + useCountStr + ">");
			}
			String keySeperator = getElementValue(memoryTableElement, "keyseperator");

			String fileYnStr = getElementValue(memoryTableElement, "fileyn");
			boolean fileYn = false;
			try {
				fileYn = StringUtil.replaceYn(fileYnStr);
			} catch (Exception e) {
				throw new ConfigException("MemorySelect.fileyn must be 'y' or 'n'");
			}

			String sql = getElementChildText(memoryTableElement, "sql");

			MemoryTable memoryTable = new MemoryTable();
			memoryTable.setId(id);
			memoryTable.setKeyCount(keyCount);
			memoryTable.setUseCount(useCount);
			memoryTable.setKeySeperator(keySeperator);
			memoryTable.setFileYn(fileYn);
			memoryTable.setSql(sql);

			if (map.containsKey(memoryTable.getId())) {
				throw new ConfigException("MemoryTable.id duplicated. <" + memoryTable.getId() + ">");
			}

			map.put(memoryTable.getId(), memoryTable);

		}

		return map;
	}

	private boolean checkUseIdx(int useCount, int useIdx) throws ConfigException {
		boolean result = false;

		if (useIdx < 1 || useIdx > useCount) {
			return false;
		}

		return true;

	}

	/**
	 * SubQuery, SubMemory, FilterInfo cross order number duplication check
	 * @param subQueries
	 * @param subMemories
	 * @param filterSources
	 * @return
	 */
	private boolean isDuplecateOrderNum(SubQuery[] subQueries, SubMemory subMemory, FilterSource[] filterSources) {
		ArrayList arrayList = new ArrayList();
		if (subQueries != null) {
			for (int i = 0; i < subQueries.length; i++) {
				if (arrayList.contains(new Integer(subQueries[i].getOrder()))) {
					return true;
				}
				arrayList.add(new Integer(subQueries[i].getOrder()));
			}
		}

		if (subMemory != null) {

			Set orderKeySet = subMemory.getMemorySelectMap().keySet();

			Iterator it = orderKeySet.iterator();

			while (it.hasNext()) {
				String orderId = (String) it.next();
				if (arrayList.contains(new Integer(orderId))) {
					return true;
				}
				arrayList.add(new Integer(orderId));
			}
		}

		if (filterSources != null) {
			for (int i = 0; i < filterSources.length; i++) {
				if (arrayList.contains(new Integer(filterSources[i].getOrder()))) {
					return true;
				}
				arrayList.add(new Integer(filterSources[i].getOrder()));
			}
		}
		return false;

	}

	/**
	 * Attach File filtering configuration parsing method
	 * @param element
	 *            XML element
	 * @return FilterInfo
	 * @throws ConfigException
	 *             erro info
	 */
	private FilterSource[] getFilterConf(Element element) throws ConfigException {
		FilterSource[] filter = null;
		List list = getChildrenElementList(element.getChild("FilterInfo"), "Filter");
		if (list != null) {
			int size = list.size();
			String[] duplecateCheckOrders = new String[size];
			filter = new FilterSource[list.size()];
			for (int i = 0; i < size; i++) {
				filter[i] = new FilterSource();
				String orderStr = getElementValue(((Element) list.get(i)), "order");

				if (StringUtil.isExistArray(duplecateCheckOrders, orderStr)) {
					throw new ConfigException(": Duplicated <FilterInfo>"
							+ "<Filter order=\"\"> in configuration file.");
				}
				duplecateCheckOrders[i] = orderStr;

				filter[i].setOrder(Integer.parseInt(orderStr));
				filter[i].setFilterType(getElementValue(((Element) list.get(i)), "filter").toLowerCase());
				filter[i].setRetrival(getElementValue(((Element) list.get(i)), "retrieve", "").toLowerCase());
				if (filter[i].getRetrival().equals("ftserver")) {
					filter[i].setServerid(getElementValue(((Element) list.get(i)), "serverid"));
				}
				filter[i].setCondition(getElementValue(((Element) list.get(i)), "condition", "").toLowerCase());
				filter[i].setDir(getElementValue(((Element) list.get(i)), "dir", ""));
				filter[i].setFileext(getElementValue(((Element) list.get(i)), "fileext", ""));
				filter[i].setSeperator(getElementValue(((Element) list.get(i)), "seperator", " ", false));
				filter[i].setSplit(getElementValue(((Element) list.get(i)), "split", "", false));
				filter[i].setClassName(getElementValue(((Element) list.get(i)), "className", ""));
				filter[i].setStatement(getElementValue((((Element) list.get(i)).getChild("sql")), "statement", "n",
						false));

				filter[i].setQuery(getElementChildText(((Element) list.get(i)), "sql", ""));

				// Default = 1
				int count;
				String szCount = "1";
				try {
					szCount = getElementValue(((Element) list.get(i)), "wherecnt", "1");
					if (szCount.equals("")) {
						szCount = "1";
					}
					count = Integer.parseInt(szCount);
				} catch (Exception e) {
					throw new ConfigException(": Please check the <FilterInfo> - <Filter wherecnt=\"\">"
							+ "in configuration file. (wherecnt=\"" + szCount + "\")");
				}
				filter[i].setWhereCnt(count);

				try {
					szCount = getElementValue(((Element) list.get(i)), "usecnt", "1");
					if (szCount.equals("")) {
						szCount = "1";
					}
					count = Integer.parseInt(szCount);
				} catch (Exception e) {
					throw new ConfigException(": Please check the <FilterInfo> - <Filter usecnt=\"\">"
							+ "in configuration file. (usecnt=\"" + szCount + "\")");
				}
				filter[i].setUseFilteringCnt(count);

				String params = "";
				try {
					params = getElementValue(((Element) list.get(i)), "jungumKey", "");
				} catch (Exception e) {
					throw new ConfigException(": Please check the <FilterInfo> - <Filter jungumKey=\"\">"
							+ "in configuration file. (jungumKey=\"" + params + "\")");
				}
				filter[i].setJungumKey(params);
			}
		}
		return filter;
	}

	/**
	 * Get QUERY object method
	 * @param element
	 *            XML element
	 * @return Query
	 * @throws ConfigException
	 *             error info
	 */
	private Query getQueryConf(Element element) throws ConfigException {
		Query query = new Query();
		Element initEl = element.getChild("Init");
		if (initEl != null) {
			Element excuteEle = initEl.getChild("Execute");
			if (excuteEle != null) {
				List list = excuteEle.getChildren("sql");
				String[] select = new String[list.size()];
				for (int i = 0; i < list.size(); i++) {
					select[i] = StringUtil.trimDuplecateSpace(getElementText((Element) list.get(i)));
				}
				query.setInitExecute(select);
			}
		}
		// setting Dynamic element value
		Element dynEl = element.getChild("Dynamic");
		if (dynEl != null) {
			query.setDynMode(getElementValue(dynEl, "mode", "queue"));
			query.setSeqNoYn(getElementValue(dynEl, "seqno", "y"));
			Element dynInitEl = dynEl.getChild("Init");
			if (dynInitEl != null) {
				Element excuteEle = dynInitEl.getChild("Execute");
				if (excuteEle != null) {
					List list = excuteEle.getChildren("sql");
					String[] select = new String[list.size()];
					for (int i = 0; i < list.size(); i++) {
						select[i] = StringUtil.trimDuplecateSpace(getElementText((Element) list.get(i)));
					}
					query.setDynInitExecute(select);
				}
			}

			if (dynEl.getChildren("Select") != null) {
				List list = dynEl.getChildren("Select");
				for (int i = 0; i < list.size(); i++) {
					String sqlType = getElementValue((Element) list.get(i), "type");
					String sql = getElementChildText(((Element) list.get(i)), "sql");
					if (sqlType.equals("D")) {
						query.setSelectQDeleteDoc(sql);
					} else if (sqlType.equals("R")) {
						query.setReplaceQuery(sql);
					} else if (sqlType.equals("I")) {
						query.setSQLInsert(sql);
					} else if (sqlType.equals("U")) {
						query.setSQLUpdate(sql);
					} else if (sqlType.equals("IU") || sqlType.equals("UI")) {
						Element conditionEle = ((Element) list.get(i));
						if (conditionEle != null) {
							List conditionList = conditionEle.getChildren("condition");
							for (int n = 0; n < conditionList.size(); n++) {
								String conditionType = getElementValue((Element) conditionList.get(n), "type");
								if (conditionType.equals("I")) {
									String insertCondition = getElementText(((Element) conditionList.get(n)));
									query.setSQLInsert(sql + " " + insertCondition);
								} else if (conditionType.equals("U")) {
									String updateCondition = getElementText(((Element) conditionList.get(n)));
									query.setSQLUpdate(sql + " " + updateCondition);
								} else {
									throw new ConfigException(": Missing"
											+ " <Dynamic/> - <Select/> setting in configuration file.");
								}
							}
						}
					}
				}
			} else {
				throw new ConfigException(": Missing" + " <Dynamic/> - <Select/> setting in configuration file.");
			}
			Element dynExecChilds = dynEl.getChild("Execute");		
			if (dynExecChilds != null)
			{
				List list = dynExecChilds.getChildren("sql");
				String[] executeQueryAry = new String[list.size()];
				String[] statementAry = new String[list.size()];
				for( int n=0; n< list.size(); n++){
					executeQueryAry[n] = StringUtil.trimDuplecateSpace(getElementText((Element) list.get(n)));
					statementAry[n] = getElementValue((Element)list.get(n), "statement", "n", false);
				}
				
				query.setDynExecute(executeQueryAry);
				query.setStatement(statementAry);
			}
		}
		// setting static query
		Element staticEl = element.getChild("Static");

		if (staticEl != null) {
			
			Element staticInitEl = staticEl.getChild("Init");
			if (staticInitEl != null) {
				Element excuteEle = staticInitEl.getChild("Execute");
				if (excuteEle != null) {
					List list = excuteEle.getChildren("sql");
					String[] select = new String[list.size()];
					for (int i = 0; i < list.size(); i++) {
						select[i] = StringUtil.trimDuplecateSpace(getElementText((Element) list.get(i)));
					}
					query.setStaticInitExecute(select);
				}
			}

			if (staticEl.getChild("Select") != null) {
				query.setStaticQuery(getElementChildText(staticEl.getChild("Select"), "sql"));
			}
		}

		Element dateTimeEl = element.getChild("DateTime");
		if (dateTimeEl != null) {
			// query.setDynMode(getElementValue(dynEl,"mode", "queue" ));
			query.setDateTimePropertyPath(getElementValue(dateTimeEl, "path", ""));
			if (dateTimeEl.getChild("Select") != null) {
				query.setDateTimeQuery(getElementChildText(dateTimeEl.getChild("Select"), "sql"));
			}
		}

		Element masterEl = element.getChild("MasterTable");
		if (masterEl != null) {
			if (masterEl.getChild("Select") != null) {
				query.setMasterTblSelect(getElementChildText(masterEl.getChild("Select"), "sql"));
			}
		}

		return query;
	}

	/**
	 * 
	 * @param element
	 *            XML element
	 * @return TableSchema
	 * @throws ConfigException
	 *             error info
	 */
	private TableSchema getTableSchema(Element element) throws ConfigException {
		TableSchema schema = new TableSchema();
		Element tbl = element.getChild("PrimaryKey");
		List list = tbl.getChildren("Column");
		int size = list.size();
		String[] pkColunms = new String[size];
		for (int i = 0; i < size; i++) {
			Element pkEl = (Element) list.get(i);
			pkColunms[i] = getElementValue(pkEl, "name");
		}
		schema.setColumn(pkColunms);
		return schema;
	}

	private CustomServerInfo getCustomServerConf(Element source) throws ConfigException {
		CustomServerInfo customServerInfo;
		if (source != null) {
			customServerInfo = new CustomServerInfo();
		} else {
			return null;
		}

		if (source.getChild("CustomServer") != null) {
			int portNumber;
			try {
				portNumber = Integer.parseInt(getElementValue(element.getChild("CustomServer"), "port"));
			} catch (NumberFormatException ne) {
				throw new ConfigException(": Missing "
						+ "<CustomServer port=\"number\"/> setting in configuration file. "
						+ "Please specify the numbers 1 ~ 32. Could not parse CustomServer Config.");
			}
			String serverName = getElementValue(element.getChild("CustomServer"), "ip", "");
			customServerInfo.setPortNumber(portNumber);
			customServerInfo.setServerName(serverName);
		} else {
			return null;
		}

		return customServerInfo;
	}

	private RemoteInfo[] getRemoteConf(Element source) throws ConfigException {
		RemoteInfo[] remoteinfos = null;
		RemoteInfo remoteinfo;
		List remote;

		if (source != null) {
			remote = source.getChildren("Remote");
		} else {
			return null;
		}

		if (remote != null) {
			int size = remote.size();
			remoteinfos = new RemoteInfo[size];
			
			for (int i = 0; i < size; i++) {
				remoteinfo = new RemoteInfo();
				
				String ip = getElementValue(((Element) remote.get(i)), "ip", "");
				int port = Integer.parseInt(getElementValue(((Element) remote.get(i)), "port", ""));
				String deleteSCD = getElementValue(((Element) remote.get(i)), "deleteSCD", "n");
				remoteinfo.setIp(ip);
				remoteinfo.setPort(port);
				if (deleteSCD.equalsIgnoreCase("y")) {
					remoteinfo.setDeleteSCD(true);
				}
				
				remoteinfos[i] = remoteinfo;
			}
		}

		return remoteinfos;
	}
}
