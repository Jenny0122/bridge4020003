package kr.co.wisenut.bridge3.config.source.node;

public class RefColl {
	private String id = "";
	private String prefix = "";
	
	/**
	 * Getting Reference Collection ID
	 * 
	 * @return source id
	 */
	public String getId() {
		return id;
	}
	/**
	 * Setting Reference Collection ID
	 * 
	 * @param id source id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Getting DOCID's prefix value
	 * 
	 * @return prefix
	 */
	public String getPrefix() {
		return prefix;
	}
	/**
	 * Setting DOCID's prefix value
	 * 
	 * @param prefix prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
