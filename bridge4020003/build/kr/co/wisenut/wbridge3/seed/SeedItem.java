/*
 * @(#)SeedItem.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.seed;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * SeedItem
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All RightsReserved.
 * This software is the proprietary information of WISEnut,Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
/**
 * 
 * @Company : WISENut 
 * @Date : 2011. 6. 20.
 */
public class SeedItem{
    private String source;
    private String section;
    private String section1;
    private String section2;
    private String section3;
    private List skipmenu;
    private List  tailmenu;
    private String tailsrc;
    private String homeurl;
    private String homeurl2;
    private String base_url;
    private String locale;
    private String charset;
    private String customJS;
    private List usertag;
    private int maxDepth ;
    private int maxUrl;
    private int nth;
    private List  url;
    private List  skip_url ;
    private List  write_allow_url;
    private List only_view_url;
    private String[] denyId;
    private String[] denyClass;
    private boolean isList = false;
    
    /** �닔吏묓븷 div id 媛믩뱾 */
	private String[] collectDivIds;
	/** �닔吏묓븷 tag �뿉 class �냽�꽦 媛믩뱾 */
	private String[] collectClasses;
	
	private String basicauthid;
    private String basicauthpw;

    /**
     * return the maxDepth of Url
     * @return  maxDepth depth Number
     */
    public int getMaxDepth() {  return maxDepth;  }

    public String getSection() {
        return section;
    }
    public String getSection1() {
    	return section1;
    }
    public String getSection2() {
    	return section2;
    }
    public String getSection3() {
    	return section3;
    }

    public void setSection(String section) {
        this.section = section;
    }
    public void setSection1(String section1) {
    	this.section1 = section1;
    }
    public void setSection2(String section2) {
    	this.section2 = section2;
    }
    public void setSection3(String section3) {
    	this.section3 = section3;
    }

    public String getTailsrc() {
        return tailsrc;
    }

    public void setTailsrc(String tailsrc) {
        this.tailsrc = tailsrc;
    }
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
    /**
     *
     * @param maxDepth URL Depth
     */
    public void setMaxDepth( int maxDepth ) {  this.maxDepth = maxDepth;  }

    /**
     * return the max Number of Crawl Url
     * @return maxUrl  url Number
     */
    public int getMaxUrl() { return maxUrl;  }
    /**
     *
     * @param maxUrl  Crawl Url
     */
    public void setMaxUrl(int maxUrl) {  this.maxUrl = maxUrl; }
    /**
     *
     * @param element skip url
     */
    public void addElement(String element){ this.skip_url.add(element); }
    /**
     *
     * @param element  allow url
     */
    public void addWriteAllowElement(String element){this.write_allow_url.add(element); }

    public void addOnlyViewElement(String element){this.only_view_url.add(element); }
    /**
     *
     * @param element Crawl url
     */
    public void addUrlElement(String element){this.url.add(element); }

    public List getUsertag() {return usertag; }

    public void addUsertag(String usertag) {  this.usertag.add(usertag);  }

    /**
     *
     * @param url  Crawl Base Url
     */
    public void setBaseUrl(String url) {  base_url = url;  }
    /**
     *
     * @return Return the Base URL of Crawl Url
     */
    public String getBaseUrl() {  return base_url; }
    /**
     *
     * @return  Return the Source of Crawl Url
     */
    public String getSource() { return source; }
    /**
     *
     * @param source
     */
    public void setSource(String source) { this.source = source;}
    /**
     *
     * @return Return the URL of Crawl Url
     */
    public List getUrl() { return url;}
    /**
     *
     * @return  Return the Skip URL URL of Crawl Url
     */
    public List getSkipmenu() { return skipmenu;}
    /**
     *
     * @param skipmenu HTML Content�뿉�꽌 Skip�븯�뒗 Menu
     */
    public void addSkipmenu(String skipmenu) {this.skipmenu.add(skipmenu); }
    /**
     *
     * @return  Return the Skip Tail Menu of Crawl Url
     */
    public List getTailmenu() { return tailmenu;}
    /**
     *
     * @param tailmenu   HTML Content�뿉�꽌 Skip�븯�뒗 Tail Menu
     */
    public void addTailmenu(String tailmenu) { this.tailmenu.add(tailmenu);}
    /**
     *
     * @return  Return the Home Url of Crawl Url
     */
    public String getHomeurl() { return homeurl;  }

    /**
     *
     * @param homeurl  Crawl HomeUrl
     */
    public void setHomeurl(String homeurl) { this.homeurl = homeurl; }

    /**
     *
     * @return  Return the Source Count of Seed
     */
    public int getNth() { return nth;}
    /**
     *
     * @param nth Source
     */
    public void setNth(int nth) { this.nth = nth;  }

    public String getCustomJS() {  return customJS; }

    public void setCustomJS(String customJS) {  this.customJS = customJS;  }

    /**
     *   SeedItem Constructor
     */
    public SeedItem() {
        source = new String();
        section = new String();
        section1 = new String();
        section2 = new String();
        section3 = new String();
        tailsrc = new String();
        url = new ArrayList();
        skip_url = new ArrayList();
        write_allow_url = new ArrayList();
        only_view_url = new ArrayList();
        skipmenu = new ArrayList();
        tailmenu = new ArrayList();
        usertag = new ArrayList();
        homeurl = new String();
        homeurl2 = new String();
        base_url = new String();
        customJS = new String();
        maxDepth =0;
        maxUrl = 0;
        nth = -1;
        charset = "";
        locale = new String();
        basicauthid = new String();
        basicauthpw = new String();
    }

    /**
     * Seed Item Mapping Function
     * @param item SeedItem Class
     * @return  Source�쓽 Count
     */
    public int assign(SeedItem item) {
        source = item.source;
        section = item.section;
        section1 = item.section1;
        section2 = item.section2;
        section3 = item.section3;
        tailsrc = item.tailsrc;
        url = item.url;
        skip_url = item.skip_url;
        write_allow_url = item.write_allow_url;
        only_view_url = item.only_view_url;
        skipmenu = item.skipmenu;
        tailmenu = item.tailmenu;
        usertag = item.usertag;
        homeurl = item.homeurl;
        homeurl2 = item.homeurl2;
        base_url = item.base_url;
        nth = item.nth;
        maxDepth = item.maxDepth ;
        maxUrl = item.maxUrl ;
        customJS = item.customJS;
        locale = item.locale;
        charset = item.charset;
        isList = item.isList;
        denyId = item.denyId;
        denyClass = item.denyClass;
        this.collectDivIds = item.collectDivIds;
        this.collectClasses = item.collectClasses;
        this.basicauthid = item.basicauthid;
        this.basicauthpw = item.basicauthpw;
        
        return nth;
    }

    /**
     * Skip URL Check Function
     * @param url  URL Address
     * @return  IS Skip? true/false
     */
    public boolean isSkipURL(String url) {
        for ( int i = 0; i < skip_url.size(); i++) {
            if ( url.indexOf((String)skip_url.get(i)) != -1  ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Allow URL Check Function
     * @param url URL Address
     * @return   IS AllowURL? true/false
     */
    public boolean isWriteAllowURL(String url) {
        if(write_allow_url.size() > 0  ){
            for ( int i = 0; i < write_allow_url.size(); i++) {
                if (url.indexOf((String)write_allow_url.get(i)) > -1 && !((String)write_allow_url.get(i)).equals("")) {
                    return true;
                }
            }
        }else{
             if (url.indexOf(this.homeurl) > -1 ) {
                    return true;
             }
        }
        return false;
    }

    public boolean isOnlyViewURL(String url) {
        if(only_view_url.size() > 0  ){
            for ( int i = 0; i < only_view_url.size(); i++) {
                if (url.indexOf((String)only_view_url.get(i)) > -1 && !((String)only_view_url.get(i)).equals("")) {
                    return true;
                }
            }
        }

        return false;
    }

    public String[] getDenyId() {
        return denyId;
    }

    public void setDenyId(String[] denyId) {
        this.denyId = denyId;
    }

    public String[] getDenyClass() {
        return denyClass;
    }

    public void setDenyClass(String[] denyClass) {
        this.denyClass = denyClass;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

	public void setCollectDivIds(String[] collectDivIds) {
		this.collectDivIds = collectDivIds;
		
	}

	public String[] getCollectDivIds() {
		return collectDivIds;
	}

	public String[] getCollectClasses() {
		return collectClasses;
	}

	public void setCollectClasses(String[] collectClasses) {
		this.collectClasses = collectClasses;
	}
	
	public void setBasicAuthID(String basicauthid) {
		this.basicauthid = basicauthid;
	}
	
	public String getBasicAuthID() {
		return basicauthid;
	}
	
	public void setBasicAuthPW(String basicauthpw) {
		this.basicauthpw = basicauthpw;
	}
	
	public String getBasicAuthPW() {
		return basicauthpw;
	}
	
	
	
}
