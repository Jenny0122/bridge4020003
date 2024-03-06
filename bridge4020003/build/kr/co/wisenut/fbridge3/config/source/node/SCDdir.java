package kr.co.wisenut.fbridge3.config.source.node;

public class SCDdir {
	private String path = "";
	private String idx = "00";
	
	/**
	 * Getting SCD Save Directory Path
	 *  
	 * @return directory path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * Setting SCD Save Directory Path
	 *  
	 * @param path directory path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * Getting SCD File Source Index
	 * 
	 * @return source Index
	 */
	public String getIdx() {
		return idx;
	}
	/**
	 * Setting  SCD File Source Index
	 * 
	 * @param idx source Index
	 */
	public void setIdx(String idx) {
		this.idx = idx;
	}
	
}
