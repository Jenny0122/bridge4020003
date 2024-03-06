package kr.co.wisenut.bridge3.config.source.node;

public class XmlQuery {
	private int order			= -1;
	private int wherecnt		= -1;
	private String seperator 	= "";
	private String query 		= "";
	private boolean isStatemet 	= false;
	private XmlValue[] xmlValues = null;
	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	
	public int getWherecnt() {
		return wherecnt;
	}
	public void setWherecnt(int wherecnt) {
		this.wherecnt = wherecnt;
	}
	
	public String getSeperator() {
		return seperator;
	}
	public void setSeperator(String seperator) {
		this.seperator = seperator;
	}
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	
	public boolean isStatemet() {
		return isStatemet;
	}
	public void setStatemet(boolean isStatemet) {
		this.isStatemet = isStatemet;
	}
	
	public XmlValue[] getXmlValues() {
		return xmlValues;
	}
	public void setXmlValues(XmlValue[] xmlValues) {
		this.xmlValues = xmlValues;
	}
	
}
