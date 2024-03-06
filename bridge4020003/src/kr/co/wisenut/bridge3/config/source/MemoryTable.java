package kr.co.wisenut.bridge3.config.source;

/**
 * SubMemory node info class
 *
 */
public class MemoryTable {
	private String id = "";
	private String keySeperator = " ";
	private int keyCount = 0;
	private int useCount = 0;
	private boolean fileYn = false;
	private String sql = "";

	public boolean equals(Object obj) {
		if (obj instanceof MemoryTable) {
			MemoryTable mt = (MemoryTable) obj;
			if (this.id.equals(mt.getId())) {
				return true;
			} else {
				return false;
			}
		} else {
			return super.equals(obj);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKeySeperator() {
		return keySeperator;
	}

	public void setKeySeperator(String keySeperator) {
		this.keySeperator = keySeperator;
	}

	public int getKeyCount() {
		return keyCount;
	}

	public void setKeyCount(int keyCount) {
		this.keyCount = keyCount;
	}

	public int getUseCount() {
		return useCount;
	}

	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}

	public boolean isFileYn() {
		return fileYn;
	}

	public void setFileYn(boolean fileYn) {
		this.fileYn = fileYn;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

}
