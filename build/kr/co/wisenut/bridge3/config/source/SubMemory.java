package kr.co.wisenut.bridge3.config.source;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SubMemory {

	private LinkedHashMap memorySelectMap = new LinkedHashMap();

	private ArrayList availableMemoryTable = new ArrayList();

	public LinkedHashMap getMemorySelectMap() {
		return memorySelectMap;
	}

	public void putMemorySelect(MemorySelect memorySelect) throws Exception {
		if (memorySelectMap.containsKey(memorySelect.getOrder())) {
			throw new Exception("MemorySelect order id duplicated. <" + memorySelect.getOrder() + ">");
		}
		memorySelectMap.put(memorySelect.getOrder(), memorySelect);
	}

	public void putAbailableMemoryTable(MemoryTable memoryTable) {
		if (!availableMemoryTable.contains(memoryTable)) {
			availableMemoryTable.add(memoryTable);
		}

	}

	public ArrayList getAvailableMemoryTable() {
		return availableMemoryTable;
	}
	
	

}
