/*
 * @(#)HtmlUtil.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.util;

import kr.co.wisenut.common.logger.Log2;
import kr.co.wisenut.common.util.html.HTMLParser;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.apache.commons.lang3.*;

/**
 *
 * HtmlUtil
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class HtmlUtil {
    /**
     *
     * @return String
     */
    public static String getHtmlParse(byte[] htmlData, boolean imgAltTag, String[] denyId, String[] denyClass, boolean onlyHead) {
        String value = "";
        try {
            value = new String(htmlData, "UTF-8");
            //value = StringEscapeUtils.unescapeHtml4(value);

            /*
            String[] tagList = {"p" , "span", "table", "tr", "td", "div", "br", "font"};
            
            for(int idx=0; idx<tagList.length; idx++) {
            	value = value.replaceAll("&lt;("+tagList[idx]+")", "<" + tagList[idx]);
            	value = value.replaceAll("&lt;(/)("+tagList[idx]+ ")", "</" + tagList[idx] );
            	value = value.replaceAll("(" + tagList[idx] + ")&gt;", tagList[idx] + ">");
            	value = value.replaceAll("&gt;", ">");
            }
            */
            
            //value = value.replaceAll("&lt;(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^&gt;]*)?(\\s)*(/)?&gt;", "");
            
           /* 
            Pattern p = Pattern.compile("<(.*?)>");
            Matcher m = p.matcher(value);
            boolean isFind = false;
            while(m.find()) {
            	isFind = true;
            	String str = m.group(0);
            	value = value.replaceAll(str, "");
            }
            
//            if(!isFind) {
            	p = Pattern.compile("<(\\+\\*\\-\\.\\*?)>");
            	m = p.matcher(value);
            	while(m.find()) {
                	String str = m.group(0);
                	value = value.replaceAll(str, "");
                }
//            }
            */

//            Pattern	p = Pattern.compile("<(\\+\\*\\-\\.\\*?)>");
//            Matcher m = p.matcher(value);
//        	while(m.find()) {
//            	String str = m.group(0);
//            	value = value.replaceAll(str, "");
//            }

//			value = makeSingleHtml(value);

//            value = StringUtil.delete(value, "=");
            value = StringUtil.replace(value, "&nbsp;", " ");
            value = StringUtil.replace(value, "&#xd;", "");
            //value = StringUtil.replace(value, "&reg", " ");
            value = StringUtil.replace(value, "<<", " ");
            value = StringUtil.replace(value, ">>", " ");

            byte[] buffer = value.getBytes("UTF-8");
            HTMLParser parser = new HTMLParser(buffer);
            value = parser.htmlParsing(imgAltTag, denyId, denyClass, onlyHead);

            //value = value.replaceAll("<", "&lt;");
            //value = value.replaceAll(">", "&gt;");
            
            //value = convertFormattedTextToPlaintext(value);

        }  catch (Exception e) { 
        	e.printStackTrace();
        }
        return value;
    }

//	public static String makeSingleHtml(String value) {
//		String STT_HTML = "<html>";
//		String END_HTML = "</html>";
//		StringBuilder sb = new StringBuilder();
//		if(value.length() > 0) {
//			Pattern pat = Pattern.compile("^(.*?"+STT_HTML+")(.*"+END_HTML+".*)$", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
//			Matcher mat = pat.matcher(value);
//
//			StringBuilder pre = new StringBuilder();
//			StringBuilder suf = new StringBuilder();
//			String innerHTML = "";
//			int depth = 0;
//			while (mat.find() && mat.groupCount() == 2) {
//				if(depth == 0) {
//					innerHTML = mat.group(2);
//					innerHTML = innerHTML.substring(0, innerHTML.lastIndexOf(END_HTML));
//				}else{
//					//before <html>
//					String before = mat.group(1);
//					before = before.substring(0, before.length() - STT_HTML.length());
//
//					//<html> innerHTML </html>
//					String tmp = mat.group(2);
//					innerHTML = tmp.substring(0, tmp.lastIndexOf(END_HTML));
//
//					//</html> after
//					String after = tmp.substring(tmp.lastIndexOf(END_HTML)+END_HTML.length());
//
//					pre.append(before);
//					suf.insert(0, after);
//				}
//				//for next find()
//				mat = pat.matcher(innerHTML);
//				depth++;
//			}
//			if(depth == 0) innerHTML = value;
//			sb.insert(0,STT_HTML).append(pre).append(innerHTML).append(suf).append(END_HTML);
//		}
//		return sb.toString();
//	}
    
    public static String doRemoveHtml( String val )
    {
        val = val.replaceAll("\\<.*?\\>", "");
        val = val.replaceAll("\\&.*?\\;", " ");
        val = val.trim().trim();
        return val;
    }

    public static String getHtmlParse(byte[] htmlData) {
        return getHtmlParse(htmlData, false, null, null, false);
    }

    /**
     *
     * @param tag
     * @return  String
     */
    public static String getTagValue(byte[] htmlData, String tag) {
        String tagValues = "";
        try {
            //byte[] contents = htmlData.getBytes(charset);
            HTMLParser parser = new HTMLParser(htmlData);
            tagValues =  parser.getTagValue (tag) ;
        } catch (Exception e) {}
        return  tagValues;
    }

    /**
     *
     * @param htmlData
     * @param metaName
     * @return String
     */
    public static String getUserMetaTagValue(byte[] htmlData, String metaName) {
        try {
            String startTag = "<meta name=";
            String findString = "content=";
            String Content = new String(htmlData, "UTF-8");
            
            if(Content.length() > 1024) {
            	int endHead = Content.indexOf("</head>");
            	if(endHead == -1)
            		endHead = Content.length();
            	
                Content = Content.substring(0, endHead).toLowerCase();
            } else {
                Content = Content.toLowerCase();
            }
            if(Content == null) return "";
            int st_pos = Content.indexOf(startTag)+startTag.length();
            int ed_pos = Content.indexOf(">", st_pos);
            if(st_pos > 0 && ed_pos > 0){
                String temp = Content.substring(st_pos, ed_pos);
                if(temp.indexOf(metaName) > -1) {
                    String retStr =  Content.substring(st_pos, ed_pos);
                    retStr = StringUtil.replace(retStr, "\"", "");
                    int st_idx = retStr.indexOf(findString)+findString.length();
                    if( st_idx > -1 ) {
                        retStr =
                                StringUtil.replace(retStr.substring(st_idx, retStr.length()), "/", "");
                    }
                    
                    return retStr;
                }else {
                    String value = "";
                    byte[] tempByte = Content.substring(ed_pos, Content.length()).getBytes("UTF-8");
                    if(!(value =getUserMetaTagValue(tempByte, metaName)).equals("")){
                        return value;
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
        }
        return "";
    }


    public static String getTagValue (byte[] htmlData, String tag, int count) {
        String parseData = "";
        try {
            HTMLParser parser = new HTMLParser(htmlData);
            parseData = parser.getTagValue (tag, count) ;
        } catch (Exception e) {}
        return  parseData;
    }

    /**
     *
     * @param srcURL
     * @param htmlData
     * @return Vector

    public static Vector getImageLink(String srcURL, byte[] htmlData) {
    HtmlDocument documents = new HtmlDocument(srcURL, htmlData);
    Vector links  = documents.getImageLinks();
    if(links == null) {
    links = new Vector();
    }
    return  links;
    }

     */

    /**
     * Symbolic HTML entity convert function
     * @param value FormattedText
     * @return  String Plaintext
     */
    public static String convertFormattedTextToPlaintext(String value){
        if (value == null) return null;
        if (value.length() == 0) return "";

        String ref;
        char val;
        for (int i=0; i<M_htmlCharacterEntityReferences.length; i++)  {
            ref = M_htmlCharacterEntityReferences[i];
            if (value.indexOf(ref) >= 0)  {
                val = M_htmlCharacterEntityReferencesUnicode[i];
                value = value.replaceAll(ref, Character.toString(val));
            }
        }
        value = decodeNumericCharacterReferences(value);

        return value;
    }

    /**
     *
     * @param value text
     * @return String
     */
    private static String decodeNumericCharacterReferences(String value) {
        StringBuffer buf = null;
        final int valuelength = value.length();
        for (int i = 0; i < valuelength; i++){
            if (  (value.charAt(i) == '&' || value.charAt(i) == '^') && (i+2 < valuelength)
                    &&  (value.charAt(i+1) == '#' || value.charAt(i+1) == '^') ) {
                int pos = i+2;
                boolean hex = false;
                if ((value.charAt(pos) == 'x') || (value.charAt(pos) == 'X')){
                    pos++;
                    hex = true;
                }
                StringBuffer num = new StringBuffer(6);
                while (pos < valuelength && value.charAt(pos) != ';' && value.charAt(pos) != '^'){
                    num.append(value.charAt(pos));
                    pos++;
                }
                //
                if (pos < valuelength){
                    try{
                        int val = Integer.parseInt(num.toString(), (hex ? 16 : 10));
                        if (buf == null){
                            buf = new StringBuffer();
                            buf.append(value.substring(0, i));
                        }

                        buf.append((char)val);
                        i = pos;
                    }catch (Exception ignore){
                        if (buf != null) buf.append(value.charAt(i));
                    }
                }else{
                    if (buf != null) buf.append(value.charAt(i));
                }
            }else{
                if (buf != null) buf.append(value.charAt(i));
            }
        }

        if (buf != null) value = buf.toString();

        return value;
    }
    /**
     * Symbolic HTML entity define
     */
    private static final String[] M_htmlCharacterEntityReferences = {
            "&nbsp;", "&iexcl;", "&cent;", "&pound;","&curren;","&yen;","&brvbar;"
            ,"&sect;","&uml;","&copy;","&ordf;","&laquo;","&not;","&shy;","&reg;"
            ,"&macr;","&deg;","&plusmn;","&sup2;","&sup3;","&acute;","&micro;"
            ,"&para;","&middot;","&cedil;","&sup1;","&ordm;","&raquo;","&frac14;"
            ,"&frac12;","&frac34;","&iquest;","&Agrave;","&Aacute;","&Acirc;"
            ,"&Atilde;","&Auml;","&Aring;","&AElig;","&Ccedil;","&Egrave;"
            ,"&Eacute;","&Ecirc;","&Euml;","&Igrave;","&Iacute;","&Icirc;","&Iuml;"
            ,"&ETH;","&Ntilde;","&Ograve;","&Oacute;","&Ocirc;","&Otilde;","&Ouml;"
            ,"&times;","&Oslash;","&Ugrave;","&Uacute;","&Ucirc;","&Uuml;","&Yacute;"
            ,"&THORN;","&szlig;","&agrave;","&aacute;","&acirc;","&atilde;","&auml;"
            ,"&aring;","&aelig;","&ccedil;","&egrave;","&eacute;","&ecirc;","&euml;"
            ,"&igrave;","&iacute;","&icirc;","&iuml;","&eth;","&ntilde;","&ograve;"
            ,"&oacute;","&ocirc;","&otilde;","&ouml;","&divide;","&oslash;","&ugrave;"
            ,"&uacute;","&ucirc;","&uuml;","&yacute;","&thorn;","&yuml;","&fnof;"
            ,"&Alpha;","&Beta;","&Gamma;","&Delta;","&Epsilo;","&Zeta;","&Eta;"
            ,"&Theta;","&Iota;","&Kappa;","&Lambda;","&Mu;","&Nu;","&Xi;","&Omicro;"
            ,"&Pi;","&Rho;","&Sigma;","&Tau;","&Upsilo;","&Phi;","&Chi;","&Psi;"
            ,"&Omega;","&alpha;","&beta;","&gamma;","&delta;","&epsilo;","&zeta;"
            ,"&eta;","&theta;","&iota;","&kappa;","&lambda;","&mu;","&nu;","&xi;"
            ,"&omicro;","&pi;","&rho;","&sigmaf;","&sigma;","&tau;","&upsilo;","&phi;"
            ,"&chi;","&psi;","&omega;","&thetas;","&upsih;","&piv;","&bull;","&hellip;"
            ,"&prime;","&Prime;","&oline;","&frasl;","&weierp;","&image;","&real;"
            ,"&trade;","&alefsy;","&larr;","&uarr;","&rarr;","&darr;","&harr;","&crarr;"
            ,"&lArr;","&uArr;","&rArr;","&dArr;","&hArr;","&forall;","&part;","&exist;"
            ,"&empty;","&nabla;","&isin;","&notin;","&ni;","&prod;","&sum;","&minus;"
            ,"&lowast;","&radic;","&prop;","&infin;","&ang;","&and;","&or;","&cap;"
            ,"&cup;","&int;","&there4;","&sim;","&cong;","&asymp;","&ne;","&equiv;"
            ,"&le;","&ge;","&sub;","&sup;","&nsub;","&sube;","&supe;","&oplus;"
            ,"&otimes;","&perp;","&sdot;","&lceil;","&rceil;","&lfloor;","&rfloor;","&lang;"
            ,"&rang;","&loz;","&spades;","&clubs;","&hearts;","&diams;","&quot;"
            ,"&amp;","&lt;","&gt;","&OElig;","&oelig;","&Scaron;","&scaron;","&Yuml;"
            ,"&circ;","&tilde;","&ensp;","&emsp;","&thinsp;","&zwnj;","&zwj;","&lrm;"
            ,"&rlm;","&ndash;","&mdash;","&lsquo;","&rsquo;","&sbquo;","&ldquo;"
            ,"&rdquo;","&bdquo;","&dagger;","&Dagger;","&permil;","&lsaquo;","&rsaquo;","&euro;"
    };


    /**
     * Symbolic HTML entity Unicode define
     */
    private static final char[] M_htmlCharacterEntityReferencesUnicode = {
            160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,
            181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,
            202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,
            223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,239,240,241,242,243,
            244,245,246,247,248,249,250,251,252,253,254,255,402,913,914,915,916,917,918,919,920,
            921,922,923,924,925,926,927,928,929,931,932,933,934,935,936,937,945,946,947,948,949,
            950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,966,967,968,969,977,
            978,982,8226,8230,8242,8243,8254,8260,8472,8465,8476,8482,8501,8592,8593,8594,8595,
            8596,8629,8656,8657,8658,8659,8660,8704,8706,8707,8709,8711,8712,8713,8715,8719,8721,
            8722,8727,8730,8733,8734,8736,8743,8744,8745,8746,8747,8756,8764,8773,8776,8800,8801,
            8804,8805,8834,8835,8836,8838,8839,8853,8855,8869,8901,8968,8969,8970,8971,9001,9002,
            9674,9824,9827,9829,9830,34,38,60,62,338,339,352,353,376,710,732,8194,8195,8201,8204,
            8205,8206,8207,8211,8212,8216,8217,8218,8220,8221,8222,8224,8225,8240,8249,8250,8364
    };
    
    public static void main(String[] args) {
    	String colunmData ="<!--startfragment-->&nbsp;\n"
        		+"<p style=\"FONT-SIZE: 12pt: MARGIN: 0pt; COLOR;#000000; TEXT-INDENT: 0pt; LINE-HEIGHT: 160%;> 한글입니다. </p>";
    	
    	colunmData ="   	기안자서지은 대리 서지은의 결재문서 중 "
    			+"<P class=MsoNormal style=\"MARGIN: 0cm 0cm 10pt\" align=center>=&nbsp;아&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 래&nbsp;=</P>"
    			+"<TABLE style=\"WORD-WRAP: break-word; WIDTH: 712px; BORDER-COLLAPSE: collapse\" "
    			+"borderColor=#111111 cellSpacing=0 cellPadding=0 border=1>"
    			;
    	colunmData = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
    			+"<HTML><HEAD>"
    			+"<META content=\"text/html; charset=ks_c_5601-1987\" http-equiv=Content-Type>"
    			+"<STYLE type=text/css>TABLE {"
    			+"	BORDER-LEFT: 1px; FONT-SIZE: 10pt; BORDER-TOP: 1px; cellSpacing: 0; cellPadding: 0; borderColor: #000000"
    			+"}"
    			+"TD {"
    			+"	BORDER-BOTTOM: 1px; FONT-SIZE: 10pt; BORDER-RIGHT: 1px; cellSpacing: 0; cellPadding: 0; borderColor: #000000"
    			+"}"
    			+"BODY {"
    			+"	FONT-SIZE: 10pt"
    			+"}"
    			+"P {"
    			+"	MARGIN-TOP: 1px; MARGIN-BOTTOM: 1px; FONT-SIZE: 10pt"
    			+"}"
    			+"BLOCKQUOTE {"
    			+"	MARGIN-TOP: 1px; MARGIN-BOTTOM: 1px"
    			+"}"
    			+"</STYLE>"
    			+""
    			+"<META name=GENERATOR content=\"MSHTML 11.00.9600.18427\">"
    			+"<STYLE>v\\:* {"
    			+"	BEHAVIOR: url(#default#VML)"
    			+"}"
    			+"o\\:* {"
    			+"	BEHAVIOR: url(#default#VML)"
    			+"}"
    			+"x\\:* {"
    			+"	BEHAVIOR: url(#default#VML)"
    			+"}"
    			+".shape {"
    			+"	BEHAVIOR: url(#default#VML)"
    			+"}"
    			+"</STYLE>"
    			+"</HEAD>"
    			+"<BODY>"
    			+"<P><FONT face=\"맑은 고딕\"><STRONG>1. 귀 부서의 적극적인 업무협조에 감사드립니다</STRONG></FONT></P>"
    			+"<P class=MsoNormal style=\"MARGIN: 0cm 0cm 10pt\"><STRONG><FONT "
    			+"face=\"맑은 고딕\"></FONT></STRONG></P>"
    			+"<P class=MsoNormal style=\"MARGIN: 0cm 0cm 10pt\"><FONT face=\"맑은 고딕\"><STRONG>2. 당 "
    			+"기안자(서지은 대리, 서지은)의 결재문서 중 아래 문서들이 결재자 퇴사로 인하여 결재진행이 불가하기에 해당 문서삭제를 요청 "
    			+"드립니다.</STRONG></FONT></P>"
    			+"<P class=MsoNormal style=\"MARGIN: 0cm 0cm 10pt\"><STRONG><FONT "
    			+"face=\"맑은 고딕\"></FONT></STRONG></P>"
    			+"<P class=MsoNormal style=\"MARGIN: 0cm 0cm 10pt\" "
    			+"align=center>=&nbsp;아&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 래&nbsp;=</P>"
    			+"<P>"
    			+"<TABLE style=\"WORD-WRAP: break-word; WIDTH: 712px; BORDER-COLLAPSE: collapse\" "
    			+"borderColor=#111111 cellSpacing=0 cellPadding=0 border=1>"
    			+"  <TBODY>"
    			+"  <TR>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 48px\">"
    			+"      <P><STRONG>번호</STRONG></P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 76px\">"
    			+"      <P><STRONG>결재일자</STRONG></P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 142px\">"
    			+"      <P><STRONG>테스트</STRONG></P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 254px\">"
    			+"      <P><STRONG>제목</STRONG></P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 50px\">"
    			+"      <P><STRONG>요청자</STRONG></P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 136px\">"
    			+"      <P><STRONG>삭제요청사유</STRONG></P></TD></TR>"
    			+"  <TR>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 48px\">"
    			+"      <P>1</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 76px\">"
    			+"      <P>9999-12-12</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 142px\">"
    			+"      <P>부서결재</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 254px\">"
    			+"      <P>테스트</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 50px\">"
    			+"      <P>서지은</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 136px\">"
    			+"      <P>서지은</P></TD></TR>"
    			+"  <TR>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 48px\">"
    			+"      <P>2</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 76px\">"
    			+"      <P>9999-12-12</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 142px\">"
    			+"      <P>부서결재</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 254px\">"
    			+"      <P>서지은(DCB961204060018)</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 50px\">"
    			+"      <P>서지은</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 136px\">"
    			+"      <P>서지은</P></TD></TR>"
    			+"  <TR>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 48px\">"
    			+"      <P>3</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 76px\">"
    			+"      <P>9999-12-12</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 142px\">"
    			+"      <P>부서결재</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 254px\">"
    			+"      <P>서지은(DCB961203300023)</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 50px\">"
    			+"      <P>서지은</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 136px\">"
    			+"      <P>서지은</P></TD></TR>"
    			+"  <TR>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 48px\">"
    			+"      <P>4</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 76px\">"
    			+"      <P>9999-12-12</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 142px\">"
    			+"      <P>부서결재</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 254px\">"
    			+"      <P>서지은(DCB961203230024)</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 50px\">"
    			+"      <P>서지은</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 136px\">"
    			+"      <P>서지은</P></TD></TR>"
    			+"  <TR>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 48px\">"
    			+"      <P>5</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 76px\">"
    			+"      <P>9999-12-12</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 142px\">"
    			+"      <P>부서결재</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 254px\">"
    			+"      <P>서지은(DCB961202170019)</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 50px\">"
    			+"      <P>서지은</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 136px\">"
    			+"      <P>서지은</P></TD></TR>"
    			+"  <TR>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 48px\">"
    			+"      <P>6</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 76px\">"
    			+"      <P>9999-12-12</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 142px\">"
    			+"      <P>부서결재</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 254px\">"
    			+"      <P>서지은(DCB961202100024)</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 50px\">"
    			+"      <P>서지은</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 136px\">"
    			+"      <P>서지은</P></TD></TR>"
    			+"  <TR>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 48px\">"
    			+"      <P>7</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 76px\">"
    			+"      <P>9999-12-12</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 142px\">"
    			+"      <P>부서결재</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 254px\">"
    			+"      <P>서지은(DCB961110010013)</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 50px\">"
    			+"      <P>서지은</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 136px\">"
    			+"      <P>서지은</P></TD></TR>"
    			+"  <TR>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 48px\">"
    			+"      <P>8</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 76px\">"
    			+"      <P>9999-12-12</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 142px\">"
    			+"      <P>부서결재</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 254px\">"
    			+"      <P>서지은(DCB961110010012)</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 50px\">"
    			+"      <P>서지은</P></TD>"
    			+"    <TD style=\"HEIGHT: 20px; WIDTH: 136px\">"
    			+"      <P>서지은</P></TD></TR></TBODY></TABLE><STRONG>(끝)</STRONG></P></BODY></HTML>";
    	try {
			colunmData = HtmlUtil.getHtmlParse(colunmData.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		colunmData = colunmData.trim();
		System.out.println(colunmData);
    }
}
