package kr.co.wisenut.bridge3.config.source;

public class MemorySelect {
	private String order;

	private MemoryTable refMemoryTable;

	private int useIdx;

	private String seperator;

	private String className;

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public int getUseIdx() {
		return useIdx;
	}

	public void setUseIdx(int useIdx) {
		this.useIdx = useIdx;
	}

	public String getSeperator() {
		return seperator;
	}

	public void setSeperator(String seperator) {
		this.seperator = seperator;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public MemoryTable getRefMemoryTable() {
		return refMemoryTable;
	}

	public void setRefMemoryTable(MemoryTable refMemoryTable) {
		this.refMemoryTable = refMemoryTable;
	}
	
	

}
