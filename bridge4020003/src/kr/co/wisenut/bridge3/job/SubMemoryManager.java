package kr.co.wisenut.bridge3.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import kr.co.wisenut.bridge3.config.Config;
import kr.co.wisenut.bridge3.config.catalogInfo.InfoSet;
import kr.co.wisenut.bridge3.config.source.MemoryTable;
import kr.co.wisenut.bridge3.config.source.SubMemory;
import kr.co.wisenut.bridge3.db.DBJob;
import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.FileUtil;
import kr.co.wisenut.common.util.time.StopWatch;

/**
 * SubMemory Manager Class 2011/11/16
 */
public class SubMemoryManager {
	/**
	 * SubMemory data storage structure : <orderNum, HashMap<String(condition key),
	 * String[](result data)>> result data : Save to data field number.
	 * */
	private HashMap subMemoryGroup = new HashMap();

	private Config config;

	private DBJob dbjob;

	public SubMemoryManager(Config m_config, DBJob m_dbjob) {
		this.config = m_config;
		this.dbjob = m_dbjob;
	}

	/**
	 * subMemoryGroup query result.
	 * To compare infoSet column and only SumMemory to in memory
	 * 
	 * @throws Exception
	 */
	public void loadSubMemory() throws Exception {
		Log2.out("[info] [SubMemoryManager][All SubMemory load start.]");

		// 중복 제거 위해 hashmap 사용
		LinkedHashMap useMemoryTableMap = new LinkedHashMap();
		InfoSet[] infoSets = config.getCollection().getCatalog();
		SubMemory subMemory = config.getSource().getSubMemory();
		

		ArrayList abailableMemoryTables = subMemory.getAvailableMemoryTable();

		
		for (int i = 0; i < abailableMemoryTables.size(); i++) {
			MemoryTable abailableMemoryTable = (MemoryTable)abailableMemoryTables.get(i);
			useMemoryTableMap.put(abailableMemoryTable.getId(), abailableMemoryTable);
		}

		if (useMemoryTableMap.size() == 0) {
			Log2.error("[error][useSubMemoryList count is 0.]");
		}

		Iterator iterator = useMemoryTableMap.values().iterator();
		while (iterator.hasNext()) {
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			MemoryTable useMemoryTable = (MemoryTable) iterator.next();
			// Save to Map  from db data
			Log2.out("[info] [SubMemoryManager][MemoryTable id=\"" + useMemoryTable.getId() + "\" loading....]");

			String debugFilePath = config.getArgs().getSf1_home() + FileUtil.fileseperator + "submemory" + FileUtil.fileseperator
					+ "submemory-" + config.getSrcid() + "-" + useMemoryTable.getId() + ".debugdata";

			FileUtil.makeDirForPath(debugFilePath);

			// query 결과 map 에 저장
			HashMap subMemoryMap = dbjob.executeMemoryTableQuery(useMemoryTable, debugFilePath);

			subMemoryGroup.put(useMemoryTable.getId(), subMemoryMap);

			stopWatch.stop();

			Log2.out("[info] [SubMemoryManager][MemoryTable order=\"" + useMemoryTable.getId() + "\" load completed. time : "
					+ stopWatch.getTime() / 1000F + "]");
		}

		Log2.out("[info] [SubMemoryManager][All MemoryTable load completed.]");
	}

	/*
	private MemoryTable[] getMemoryTables(SubMemory subMemory) {
		LinkedHashMap map = new LinkedHashMap();

		Collection collection = subMemory.getMemorySelectMap().values();
		Iterator it = collection.iterator();
		while (it.hasNext()) {
			MemorySelect memorySelect = (MemorySelect) it.next();
			MemoryTable memoryTable = memorySelect.getRefMemoryTable();
			map.put(memoryTable.getId(), memoryTable);
		}

		MemoryTable[] memoryTable = (MemoryTable[]) map.values().toArray(new MemoryTable[map.values().size()]);

		return memoryTable;
	}
	*/

	/*
	private boolean isIncludeOrder(InfoSet infoSet, MemoryTable subMemory) {
		int[] orders = subMemory.getOrders();
		for (int i = 0; i < orders.length; i++) {
			if (infoSet.getCollumn() == orders[i]) {
				return true;
			}
		}
		return false;
	}
	*/


	/**
	 * 
	 * @param memoryTable
	 * @param mapKey
	 * @param useIdx
	 * @return
	 */
	public ArrayList getData(MemoryTable memoryTable, String mapKey, int useIdx) {

		// get order number from submemory data
		HashMap subMemoryMap = (HashMap) subMemoryGroup.get(memoryTable.getId());

		if (subMemoryMap == null) {
			throw new NullPointerException("subMemoryMap not found in subMemoryGroup. order=\"" + memoryTable.getId() + "\"");
		}

		//  get order number from submemory data
		// length is data area's field number.
		ArrayList[] dataValues = (ArrayList[]) subMemoryMap.get(mapKey);
		
		if(dataValues == null) {
			return null;
		}
		
		return (ArrayList) dataValues[useIdx - 1];

		/*
		if (dataValues == null) {
			dataValues = new ArrayList[memoryTable.getUseCount()];
			for (int i = 0; i < dataValues.length; i++) {
				dataValues[i] = new ArrayList();
			}
		}
		*/
	}

	/*
	public String currentUseIdx(MemoryTable subMemory, int order, int currentUseIdx) {
		ArrayList subCustomClassList = subMemory.getSubCustomClass(order);
		if (subCustomClassList == null) {
			return null;
		}
		
		String className = null;
		Iterator it = subCustomClassList.iterator();
		while (it.hasNext()) {
			SubCustomClass subCustomClass = (SubCustomClass) it.next();
			int[] useIdxes = subCustomClass.getUseIdxes();
			for (int i = 0; i < useIdxes.length; i++) {
				if (currentUseIdx == useIdxes[i]) {
					className = subCustomClass.getClassName();
					return className;
				}
			}
			
		}
		return null;
	}
	*/

}
