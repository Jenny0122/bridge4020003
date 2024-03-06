/*
 * @(#)HtmlDocument.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.html;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.StringUtil;
import kr.co.wisenut.wbridge3.html.tidy.Tidy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * HtmlDocument
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class HtmlDocument {

    private static final String COLLECT_DIVID = "divId";

	private static final String COLLECT_CLASS = "class";

	/** Content text as an array of bytes (this is how we get it from HTTP !) */
    private  byte[] content = null;

    /** the DOM representation of this HTML document */
    private Document domDoc = null;

    /** URL of this document */
    //private URL url = null;
    private String url = null;
    /**
     * initializes HTML document without content
     */
    private HtmlDocument(String url) {
        this.url = url;
    }
    /**
     * Initializes an HTML document with the given content.
     *
     * @param content some HTML text as an array of bytes
     */
    public HtmlDocument(String url,byte[] content) {
        this(url);
        this.content = content;
    }


    /**
     * Initalizes an HTML document from a String. Convert string to
     * bytes using default encoding
     */
    public HtmlDocument(String url,String contentStr) {
        this(url);
        this.content = new byte[contentStr.length()+1];
        for (int i=0; i<contentStr.length(); i++) {
            this.content[i] = (byte)contentStr.charAt(i);
        }

    }

    /**
     *
     * @param srcURL
     * @param htmlData
     * @return Vector
     */
    public static  Vector getLink(String srcURL, byte[] htmlData, boolean isAnchorText) {
        String  value ;
        Vector links = null;
        try {
            value = new String(htmlData, "UTF-8");
            value = StringUtil.replace(value, "&nbsp;", " ");
            byte[] buffer = value.getBytes("UTF-8");
            HtmlDocument documents = new HtmlDocument(srcURL, buffer);
            links = documents.getLinks(isAnchorText);
            if(links == null) {
                links = new Vector();
            }
        }  catch (Exception e) {}
        return  links;
    }

     /**
     * ADD FUNCTION BY NOCODE
     * 2009.02.23
     * @param srcURL
     * @param htmlData
     * @return Vector
     */
    public static  Vector getImageLink(String srcURL, byte[] htmlData) {
        String  value ;
        Vector links = null;
        try {
            value = new String(htmlData, "UTF-8");
            value = StringUtil.replace(value, "&nbsp;", " ");
            byte[] buffer = value.getBytes("UTF-8");
            HtmlDocument documents = new HtmlDocument(srcURL, buffer);
            links = documents.getImageLinks();
            if(links == null) {
                links = new Vector();
            }
        }  catch (Exception e) {}
        return  links;
    }
    
    protected Node hasChildNode(Node child) {
        Node nextChild ;
        if(child.hasChildNodes()){
            nextChild = hasChildNode(child.getLastChild());
        }else {
            nextChild = child;
        }
        return  nextChild;
    }

    /**
     *
     * @param htmlData
     * @return String
     */
    public static String getMetaContetCharset(byte[] htmlData) {
        try {
            String startTag = "<meta";
            String findString = "charset";
            String Content = new String(htmlData, "UTF-8");
            if(Content.length() > 2048) {
                Content = Content.substring(0, 2048).toLowerCase();
            } else {
                Content = Content.toLowerCase();
            }

            if(Content == null) return "";
            int st_pos = Content.indexOf(startTag)+startTag.length();
            int ed_pos = Content.indexOf(">", st_pos);
            if(st_pos > 0 && ed_pos > 0){
                String temp = Content.substring(st_pos, ed_pos);
                if(temp.indexOf(findString) > -1) {
                    String retStr =  Content.substring(st_pos, ed_pos);
                    int st_idx = retStr.indexOf(findString+"=")+(findString+"=").length();
                    int ed_idx = retStr.indexOf("\"", st_idx);
                    if(ed_idx == -1) {
                        ed_idx = retStr.length();
                    }
                    if(st_idx > -1 && ed_idx > -1) {
                        retStr = retStr.substring(st_idx, ed_idx).replaceAll("/", "");
                    }
                    return retStr;
                }else {
                    String value = "";
                    byte[] tempByte = Content.substring(ed_pos, Content.length()).getBytes("UTF-8");
                    if(!(value =getMetaContetCharset(tempByte)).equals("")){
                        return value;
                    }
                }
            }
        } catch (UnsupportedEncodingException e) { }
        return "";
    }


    public String getMetaContetCharset() {
        if (domDoc == null) {
            parseToDOM();
        }
        String charset = "";
        charset = extractMetaContetCharset(domDoc.getDocumentElement());
        return charset;
    }

    /**
     * Extracts all links to other documents from this HTML document.
     *
     * @return a Vector of URLs containing the included links
     */
    public Vector getLinks(boolean isAnchorText) {
        if (domDoc == null) {
            parseToDOM();
        }
        Vector links = new Vector();
        extractLinks(domDoc.getDocumentElement(),links, isAnchorText);
        return links;
    }


    /**
     * Extracts all links to included images from this HTML document.
     *
     * @return a Vector of URLs containing the included links
     */
    public Vector getImageLinks() {
        if (domDoc == null) {
            parseToDOM();
        }
        Vector links = new Vector();
        extractImageLinks(domDoc.getDocumentElement(),links);

        return links;
    }


    /**
     * gets all Element nodes of a given type as a Vector
     * @param type the type of elements to return. e.g. type="a"
     * will return all <A> tags. type must be lowercase
     * @return a Vector containing all element nodes of the given type
     */
    public Vector getElements(String type) {
        if (domDoc == null) {
            parseToDOM();
        }

        Vector links = new Vector();
        extractElements(domDoc.getDocumentElement(),type,links);

        return links;
    }

    /**
     *
     * @param element
     */
    protected String  extractMetaContetCharset(Element element) {
        // this should not happen !
        if (element==null) {
            return "";
        }

        String name = element.getNodeName();
        if (name.equals("meta")) {
            // META HTTP-EQUIV=REFRESH
            String equiv=element.getAttribute("http-equiv");
            if ((equiv != null) && (equiv.equalsIgnoreCase("Content-Type"))) {
                String content=element.getAttribute("content");
                if (content == null) {
                    content="";
                }
                StringTokenizer st=new StringTokenizer(content,";");
                while (st.hasMoreTokens()) {
                    String token=st.nextToken().trim();
                    AttribValuePair av = new AttribValuePair(token);
                    if (av.getAttrib().equals("charset")) {
                        return  av.getValue();
                    }
                }
            }
        }

        // recursive travel through all childs
        NodeList childs = element.getChildNodes();

        for (int i=0; i<childs.getLength(); i++) {
            String value = "";
            if (childs.item(i) instanceof Element) {
                if(!(value = extractMetaContetCharset((Element)childs.item(i))).equals("")) {
                    return value;
                }
            }
        }

        return "";
    }

    /**
     * Extract links from the given DOM subtree and put it into the given
     * vector.
     *
     * @param element the top level DOM element of the DOM tree to parse
     * @param links the vector that will store the links
     */
    protected void extractLinks(Element element, Vector links, boolean isAnchorText)  {
        // this should not happen !
        if (element==null) {
            return;
        }

        String name = element.getNodeName();
        if (name.equals("a")) {
            String str = "";
            if (isAnchorText) {
                NodeList n = element.getElementsByTagName("a");
                int size = n.getLength();
                for(int i=0; i < size; i++) {
                    Node child = hasChildNode(n.item( i ));
                    String anchorName = child.getNodeValue();
                    if(anchorName.equals(""))anchorName="NONE";
                    str  = "$"+anchorName;
                }
            }
            // A HREF=
            String extractUrl = element.getAttribute("href");
            addLink(extractUrl+str,links);
        } else if (name.equals("frame")) {
            // FRAME SRC=
            addLink(element.getAttribute("src"),links);

        } else if (name.equals("area")) {
            // AREA HREF=
            addLink(element.getAttribute("href"),links);

        } else if (name.equals("meta")) {
            // META HTTP-EQUIV=REFRESH
            String equiv=element.getAttribute("http-equiv");
            if ((equiv != null) && (equiv.equalsIgnoreCase("refresh"))) {
                String content=element.getAttribute("content");
                if (content == null) {
                    content="";
                }
                StringTokenizer st=new StringTokenizer(content,";");
                while (st.hasMoreTokens()) {
                    String token=st.nextToken().trim();
                    AttribValuePair av = new AttribValuePair(token);
                    if (av.getAttrib().equals("url")) {
                        addLink(av.getValue(),links);
                    }
                }
            }
        }
        // recursive travel through all childs
        NodeList childs = element.getChildNodes();

        for (int i=0; i<childs.getLength(); i++) {
            if (childs.item(i) instanceof Element) {
                extractLinks((Element)childs.item(i),links, isAnchorText);
            }
        }

    }

    /**
     * Extract links to includes images from the given DOM subtree and
     * put them into the given vector.
     *
     * @param element the top level DOM element of the DOM tree to parse
     * @param links the vector that will store the links
     */
    protected void extractImageLinks(Element element, Vector links) {

        // this should not happen !
        if (element==null) {
            return;
        }

        String name = element.getNodeName();

        if (name.equals("img")) {
            // IMG SRC=
            addLink(element.getAttribute("src"),links);
        }
        // recursive travel through all childs
        NodeList childs = element.getChildNodes();

        for (int i=0; i<childs.getLength(); i++) {
            if (childs.item(i) instanceof Element) {
                extractImageLinks((Element)childs.item(i),links);
            }
        }

    }


    /**
     * Extract elements from the given DOM subtree and put it into the given
     * vector.
     *
     * @param element the top level DOM element of the DOM tree to parse
     * @param type HTML tag to extract (e.g. "a", "form", "head" ...)
     * @param elementList the vector that will store the elements
     */
    protected void extractElements(Element element,
                                   String type,
                                   Vector elementList) {

        // this should not happen !
        if (element==null) {
            return;
        }

        String name = element.getNodeName();
        if (name.equals(type)) {
            elementList.add(element);
        }

        // recursive travel through all childs
        NodeList childs = element.getChildNodes();

        for (int i=0; i<childs.getLength(); i++) {
            if (childs.item(i) instanceof Element) {
                extractElements((Element)childs.item(i),type,elementList);
            }
        }
    }


    /**
     * parses the document to a DOM tree using Tidy
     */
    private void parseToDOM() {
        ByteArrayInputStream is = new ByteArrayInputStream(content);
        // set tidy parameters
        Tidy tidy = new Tidy();
        tidy.setUpperCaseTags(false);
        tidy.setUpperCaseAttrs(false);
        tidy.setTidyMark(false);
        //encoding latin
        tidy.setCharEncoding(3);
        tidy.setErrout(new PrintWriter(new NullWriter()));

        domDoc = tidy.parseDOM(is,null);

    }

    /**
     * adds a links to the given vector. ignores (but logs) possible errors
     * @param newURL
     * @param links
     */
    private void addLink(String newURL, Vector links) {
        int mailto = newURL.indexOf("mailto");
        if (mailto >=0 ) {
            return ;
        }
        int pos = newURL.indexOf("#");
        if (pos >=0 ) {
            newURL = newURL.substring(0,pos);
        }
        URL u ;
        try {
            u = constructUrl(newURL , url);
        } catch (MalformedURLException e) {
            links.add(newURL);
            return ;
        }
        links.add(u);
    }

    /**
     *
     * @param link
     * @param base
     * @return
     * @throws MalformedURLException
     */
    public URL constructUrl (String link, String base)
            throws MalformedURLException {
        String path = "";
        boolean modified = false;
        boolean absolute;
        int index;
        URL url = new URL (new URL (base), link);
        path = url.getFile ();
        absolute = link.startsWith ("/");
        if (!absolute) {
            while (path.startsWith ("/.")) {
                if (path.startsWith ("/../")) {
                    path = path.substring (3);
                    modified = true;
                } else if (path.startsWith ("/./") || path.startsWith("/.")) {
                    path = path.substring (2);
                    modified = true;
                } else {
                    break;
                }
            }
        }
        while (-1 != (index = path.indexOf ("/\\"))) {
            path = path.substring (0, index + 1) + path.substring (index + 2);
            modified = true;
        }
        if (modified) {
            url = new URL (url, path);
        }
        return (url);
    }

    /**
     * 수집대상 div id tag 안에 있는 contents 반환
     * @param collectDivIds
     * @param srcURL
     * @param htmlData
     * @return
     */
	public static String getDivIdContents(String[] collectDivIds, String srcURL, byte[] htmlData) {
		return getCollectContents(collectDivIds, srcURL, htmlData, COLLECT_DIVID);
	}

	/**
	 * 수집대상 class attribute 가 있는 tag 안에 contents 반환
	 * @param collectClasses
	 * @param srcURL
	 * @param htmlData
	 * @return
	 */
	public static String getClassContents(String[] collectClasses, String srcURL, byte[] htmlData) {
		return getCollectContents(collectClasses, srcURL, htmlData, COLLECT_CLASS);
	}

	private static String getCollectContents(String[] collectIds, String srcURL, byte[] htmlData, String collectType) {
		String result = "";
		try {
			String value = new String(htmlData, "UTF-8");
			value = StringUtil.replace(value, "&nbsp;", " ");
			byte[] buffer = value.getBytes("UTF-8");
			HtmlDocument documents = new HtmlDocument(srcURL, buffer);
			result = documents.getCollectContent(collectIds, collectType);
		} catch (Exception e) {
		}
		return result;
	}

	public String getCollectContent(String[] collectIds, String collectType) {
		if (domDoc == null) {
			parseToDOM();
		}
		StringBuffer sb = new StringBuffer();
		extractCollectContents(domDoc.getDocumentElement(), sb, collectIds, collectType);

		return sb.toString();
	}

	protected void extractCollectContents(Element element, StringBuffer sb, String[] collectIds, String collectType) {

		// this should not happen !
		if (element == null) {
			return;
		}

		String name = element.getNodeName();
		boolean isAdded = false;
		if (collectType.equals(COLLECT_DIVID)) {
			if (name.equals("div")) {
				isAdded = addCollectText(element, sb, collectIds, element.getAttribute("id"));
			}
		} else if (collectType.equals(COLLECT_CLASS)) {
			isAdded = addCollectText(element, sb, collectIds, element.getAttribute("class"));
		}
		
		if(isAdded) {
			return;
		}

		// recursive travel through all childs
		NodeList childs = element.getChildNodes();

		for (int i = 0; i < childs.getLength(); i++) {
			if (childs.item(i) instanceof Element) {
				extractCollectContents((Element) childs.item(i), sb, collectIds, collectType);
			}
		}

	}
	private boolean addCollectText(Element element, StringBuffer sb, String[] collectIds, String id) {
		for (int i = 0; i < collectIds.length; i++) {
			if (id.trim().equals(collectIds[i])) {
				addNodeValue((Node) element, sb);
				return true;
			}
		}
		return false;
	}

	/**
	 * Node 의 하위 node value 를 sb 에 append 한다.
	 * @param node
	 * @param sb
	 */
	private void addNodeValue(Node node, StringBuffer sb) {
		NodeList childs = node.getChildNodes();

		if (childs.getLength() > 0) {
			for (int i = 0; i < childs.getLength(); i++) {
				Node childNode = childs.item(i);
				
				// 주석을 제외한 모든 태그 수집.
				if(childNode.getNodeType() != 8 && !childNode.getParentNode().getNodeName().toLowerCase().equals("script"))
					addNodeValue(childNode, sb);
			}
		} else {
			String text = node.getNodeValue().replace((char) 12288, ' ').trim();
			if (!text.equals("")) {
				sb.append(text + " ");
			}
		}
	}
}
