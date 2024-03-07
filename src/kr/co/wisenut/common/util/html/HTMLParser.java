package kr.co.wisenut.common.util.html;
import kr.co.wisenut.common.util.StringUtil;

import java.io.*;
import java.util.ArrayList;

/**
 * $Id: HTMLParser.java,v 1.5 2009/06/10 05:20:34 wisenut Exp $
 * This is a class that is used to parse a block of HTML code into
 *  separate tokens. This parser uses a recursive descent approach.
 */
public class HTMLParser implements Serializable {
    /** The actual data to parse. */
    protected byte[] pagepart;
    /** The size of the data to parse. */
    protected int length;
    /** The type of the next token. */
    protected int nextToken = START;
    /** Index of the parse. */
    protected int index = 0;
    /** The current tag started here. */
    protected int tagStart = 0;
    /** The current value as a String. */
    protected String stringValue = null;
    /** the current start of string. */
    protected int stringLength = -1;

    /** True if were in a Tag, false otherwise. */
    protected boolean tagmode = false;
    /** The last tag started here. */
    protected int lastTagStart = 0;
    /** The block we have. */
    protected HTMLBlock block;
    /** A pending comment (script or style data). */
    protected Token pendingComment = null;

    /** This indicates the start of a block. */
    public final static int START        = 0;
    /** This indicate a String value was found. */
    public final static int STRING       = 1;
    /** This is a Single Quoted String a 'string' */
    public final static int SQSTRING     = 2;
    /** This is a Double Quoted String a &quot;string&quot; */
    public final static int DQSTRING     = 3;
    /** This is the character ''' */
    public final static int SINGELQUOTE  = 4;
    /** This is the character '"' */
    public final static int DOUBLEQUOTE  = 5;
    /** Less Than '<' */
    public final static int LT           = 6;
    /** More Than '>' */
    public final static int MT           = 7;
    /** Equals '=' */
    public final static int EQUALS       = 8;
    /** A HTML comment &quot;&lt;&#33-- some text --&gt;&quot; */
    public final static int COMMENT      = 9;
    /** A HTML script */
    public final static int SCRIPT       = 10;

    /** This indicates the end of a block. */
    public final static int END          = 100;
    /** Unknown token. */
    public final static int UNKNOWN      = 1000;

    public final static String UTF8_ENCODE = "UTF-8";

    /** Create a new HTMLParser */
    public HTMLParser () {
        this.pagepart = null;
    }

    /** Create a new HTMLParser for the given page.
     * @param page the block to parse.
     */
    public HTMLParser (byte[] page) {
        //page = parseToHTML(page);

        setText (page);
    }

    /** Set the data block to parse.
     * @param page the block to parse.
     */
    public void setText (byte[] page) {
        setText (page, page.length);
    }

    /** Set the data block to parse.
     * @param page the block to parse.
     * @param length the length of the data.
     */
    public void setText (byte[] page, int length) {
        this.pagepart = page;
        this.length = length;
        index = 0;
    }

    /** Set the data to parse.
     * @param page the block to parse.
     */
    public void setText (String page) {
        setText (page.getBytes ());
    }

    /** Get a String describing the token.
     * @param token the token type (like STRING).
     * @return a String describing the token (like &quot;STRING&quot;)
     */
    protected String getTokenString (int token) {
        switch (token) {
            case START:
                return "START";
            case STRING:
                return "STRING";
            case SQSTRING:
                return "SQSTRING";
            case DQSTRING:
                return "DQSTRING";
            case SINGELQUOTE:
                return "SINGELQUOTE";
            case DOUBLEQUOTE:
                return "DOUBLEQUOTE";
            case LT:
                return "LT";
            case MT:
                return "MT";
            case EQUALS:
                return "EQUALS";
            case COMMENT:
                return "COMMENT";
            case END:
                return "END";
            case UNKNOWN:
                return "UNKNOWN";
            default:
                return "unknown";
        }
    }

    /** Scan a String from the block.
     * @throws HTMLParseException if an error occurs.
     * @return STRING
     */
    protected int scanString () throws HTMLParseException {
        int endindex = length;
        int startindex = index - 1;

        if (tagmode) {
            loop:
            while (index < length) {
                // basically a switch ought to be more effective than an if...
                // if there are lots of cases (>4?)
                switch (pagepart[index]) {
                    case (int)' ':
                    case (int)'\t':
                    case (int)'\n':
                    case (int)'\r':
                        //case (int)'\"':
                    case (int)'\'':
                    case (int)'<':
                    case (int)'>':
                        endindex = index;
                        break loop;
                    case (int)'=':

                    default:
                        index++;
                }
            }
        } else {        // tagmode false
            while (index < length) {
                if (pagepart[index] == '<') {
                    endindex = index;
                    break;
                } else {
                    index++;
                }

            }
        }
        if (tagmode) {
            try {
//                stringValue = new String (pagepart, startindex, (endindex - startindex), "ISO-8859-1");
                stringValue = new String (pagepart, startindex, (endindex - startindex), UTF8_ENCODE);
            } catch (Exception e) {
                throw new HTMLParseException ("doh: " + e);
            }
        } else {
            stringLength = (endindex - startindex);
        }
        return STRING;
    }

    /** Scan a quoted tring from the block. The first character is
     *  treated as the quotation character.
     * @throws HTMLParseException if an error occurs.
     * @return SQSTRING, DQSTRING or UNKNOWN (for strange quotes).
     */
    protected int scanQuotedString () throws HTMLParseException {
        int endindex = -1;
        int startindex = index - 1;
        byte start = pagepart[startindex];
        while (index < length) {
            if (pagepart[index++] == start) {
                endindex = index;
                break;
            }
        }
        if (endindex == -1) {
            block.setRest (lastTagStart);
            return END;
        }
        int l = (endindex < length ? endindex : length) - startindex;
        try {
//            stringValue = new String (pagepart, startindex, l, "ISO-8859-1");
            // Tag Value 값을 저장할때 한글이 깨지기 때문에 String 을 생성 할때 UTF-8 로 생성
            stringValue = new String (pagepart, startindex, l, UTF8_ENCODE);
        } catch (Exception e) {
            throw new HTMLParseException ("doh: " + e);
        }
        switch (start) {
            case (int)'\'':
                return SQSTRING;
            case (int)'"':
                return DQSTRING;
            default:
                return UNKNOWN;
        }
    }

    /** Is this tag a comment?
     * @return true if the block(at current index) starts with !--,
     *  false otherwise.
     */
    protected boolean isComment () {
        int startvalue = index - 1;
        if (index + 3 >= length)
            return false;
        return (pagepart[index]     == '!' &&
                pagepart[index + 1] == '-' &&
                pagepart[index + 2] == '-');
    }

    /** Scan a comment from the block, that is the string up to and
     *	including &quot;-->&quot;.
     * @return COMMENT or END.
     */
    protected int scanComment () throws HTMLParseException {
        int startvalue = index - 1;
        int i = -1;
        int j = index;
        while (j+2 < length) {
            if (pagepart[j]     == '-' &&
                    pagepart[j + 1] == '-' &&
                    pagepart[j + 2] == '>') {
                i = j;
                break;
            }
            j++;
        }
        if (i > -1) {
            index = i + 2;
            nextToken = MT;
            match (MT);
            stringLength = index - startvalue;
            return COMMENT;
        }
        block.setRest (startvalue);
        return END;
    }

    /** Match the token with next token and scan the (new)next token.
     * @param token the token to match.
     * @return the next token.
     */
    protected  int match (int token) throws HTMLParseException {
        int ts;

         if (nextToken != token)
            throw new HTMLParseException ("Token: " + getTokenString (token) + " != " + getTokenString (nextToken));

        if (pendingComment != null) {
            nextToken = LT;
            pendingComment = null;
            return SCRIPT;
        }
        while (index < length) {
            tagStart = index;
            stringValue = null;
            switch (pagepart[index++]) {
                case (int)' ':
                case (int)'\t':
                case (int)'\n':
                case (int)'\r':
                    // TODO evaluate strategy here, a continue here may result
                    // in RabbIT cutting out whitespaces from the html-page.
                    continue;
                case (int)'<':
                    //System.out.println("[index]" + index);
                    ts = tagStart;
                    if (isComment ()) {
                        nextToken = scanComment ();
                        tagStart = ts;
                        return nextToken;
                    } else{
                        return nextToken = LT;
                    }
                case (int)'>':
                    return nextToken = MT;
                case (int)'=':
                	if(index < length && pagepart[index] == '=') { //2020.5 bug fix
                		return nextToken = scanString();
                	} else {
                        //2021.12
//                		stringValue = "=";
//                        return nextToken = EQUALS;
                        return nextToken = scanString();
                	}
                case (int)'"':
                    if (tagmode)
                        return nextToken = scanQuotedString ();
                    // else fallthrough...
                case (int)'\'':
                    if (tagmode)
                        return nextToken = scanQuotedString ();
                    // else fallthrough...
                default:
                    return nextToken = scanString ();
            }
        }

        return nextToken = END;
    }

    /** Scan a value from the block.
     * @return the value or null.
     */
    protected String value () throws HTMLParseException {
        while (nextToken == EQUALS) {
            match (EQUALS);
            if (nextToken == STRING ||
                    nextToken == SQSTRING ||
                    nextToken == DQSTRING) {
                String val = stringValue;
                match (nextToken);
                return val;
            }
            return "";
        }
        return null;
    }

    protected void setPendingComment (Token comment) {
        nextToken = SCRIPT;
        this.pendingComment = comment;
        tagStart = pendingComment.getStartIndex ();
        stringLength = pendingComment.getLength ();
    }

    /** Scan an argument list from the block.
     * @param tag the Tag that have the arguments.
     */
    protected void arglist (Tag tag) throws HTMLParseException {
        String key = null;

        //System.err.println ("parsing arglist for tag: '" + tag + "'");
        while (true) {
            //System.err.println ("nextToken: " + nextToken + " => " + getTokenString (nextToken));
            switch (nextToken) {
                case MT:
                    tagmode = false;
                    // ok, this is kinda ugly but safer this way
                    if (tag.getLowerCaseType () != null &&
                            (tag.getLowerCaseType ().equals ("script") ||
                                    tag.getLowerCaseType ().equals ("style"))) {
                        Token text = scanCommentUntilEnd (tag.getLowerCaseType ());
                        if (text != null) {
                            setPendingComment (text);
                        } else {
                            tagmode = false;
                            return;
                        }
                    } else {
                        match (MT);
                    }
                    return;
                case STRING:
                    key = stringValue;
                    match (STRING);
                    String value = value ();
                    tag.addArg (key, value, false);
                    break;
                case END:
                    return;
                case DQSTRING:
                    String ttype = tag.getType ();
                    if (ttype != null && ttype.charAt (0) == '!') {
                        // Handle <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
                        // and similar cases.
                        tag.addArg (stringValue, null, false);
                        match (nextToken);
                    } else {
                        //System.err.println ("hmmm, strange arglist..: " + tag);
                        // this is probably due to something like:
                        // ... framespacing="0"">
                        // we backstep and change the second '"' to a blank and restart from that point...
                        index -= stringValue.length ();
                        pagepart[index] = (byte)' ';
                        match (nextToken);
                        tag.getToken ().setChanged (true);
                    }
                    break;
                case LT:   // I consider this an error (in the html of the page) but handle it anyway.
                    String type = tag.getLowerCaseType ();
                    if (type == null ||                             // <<</font.... etc
                            (type.equals ("table") &&    // <table.. width=100% <tr>
                                    stringValue == null)) {
                        tagmode = false;
                        return;
                    }
                    // fall through.
                default:
                    // this is probably due to something like:
                    // <img src=someimagead;ad=40;valu=560>
                    // we will break at '=' and split the tag to something like:
                    // <img src=someimagead;ad = 40;valu=560> if we change it.
                    // the html is already broken so should we fix it? ignore for now..
                    if (stringValue != null)
                        tag.addArg (stringValue, null, false);
                    match (nextToken);
            }
        }
    }

    protected boolean sameTag (String tag, int j) {
        byte[] b = tag.getBytes ();
        int i = -1;
        for (i = 0; i < b.length && i < length; i++) {
            // upper and lower case, only ascii were dealing with here
            if (b[i] != pagepart[j + i] &&
                    b[i] != pagepart[j + i] + 32)
                return false;
        }
        return i == b.length;
    }

    protected Token scanCommentUntilEnd (String tag) throws HTMLParseException {
        int len = tag.length ();
        int startvalue = index;
        int i = -1;
        int j = index;
        while (j + 1 + len < length) {
            if (pagepart[j]     == '<' &&
                    pagepart[j + 1] == '/' &&
                    sameTag (tag, j + 2)) {
                i = j;
                break;
            }
            j++;
        }
        if (i > -1) {
            stringLength = j - startvalue;
            index = j;
            Token text = new Token (pagepart, Token.COMMENT, startvalue, stringLength);
            return text;
        }
        block.setRest (startvalue);
        return null;
    }

    /** Scan a tag from the block.
     * @param ltagStart the index of the last tag started.
     */
    protected void tag (int ltagStart) throws HTMLParseException {
        Tag tag = new Tag ();
        Token token = new Token (tag, false);
        switch (nextToken) {
            case STRING:
                tag.setType (stringValue);
                match (STRING);
                arglist (tag);
                if (tagmode) {
                    block.setRest (lastTagStart);
                } else {
                    token.setStartIndex (ltagStart);
                    //block.addToken (token);
                    // 2009.05.28 by in-koo cho
                    addTokenCheck(token);
                }
                break;
            case MT:
                tagmode = false;
                match (MT);
                break;
            case END:
                block.setRest (lastTagStart);
                tagmode = false;
                return;
            default:
                arglist (tag);
        }
    }

    /** Scan a page from the block.
     */
    protected void page () throws HTMLParseException {
        while (block.restSize () == 0) {
            switch (nextToken) {
                case END:
                    return;
                case LT:
                    lastTagStart = tagStart;
                    tagmode = true;
                    match (LT);
                    tag (lastTagStart);
                    break;
                case COMMENT:
                    //block.addToken (new Token (pagepart, Token.COMMENT, tagStart, stringLength));
                    // 2009.05.28 by in-koo cho
                    addTokenCheck(new Token (pagepart, Token.COMMENT, tagStart, stringLength));
                    match (COMMENT);
                    break;
                case SCRIPT:
                    //block.addToken (new Token (pagepart, Token.SCRIPT, tagStart, stringLength));
                    addTokenCheck(new Token (pagepart, Token.SCRIPT, tagStart, stringLength));
                    match (SCRIPT);
                    break;
                case STRING:
                    if(stringLength > -1){
                        //block.addToken (new Token (pagepart, Token.TEXT, tagStart, stringLength));
                        // 2009.05.28 by in-koo cho
                        addTokenCheck(new Token (pagepart, Token.TEXT, tagStart, stringLength));
                    }
                    match (nextToken);
                    break;
                case MT:
                    if(!tagmode) {
                        scanString();
                    }
                default:
                    if(stringLength > -1){
                        //block.addToken (new Token (pagepart, Token.TEXT, tagStart, stringLength));
                        // 2009.05.28 by in-koo cho
                        addTokenCheck(new Token (pagepart, Token.TEXT, tagStart, stringLength));
                    }
                    match (nextToken);
            }
        }
    }

    // 분석된 HTML 에서 유효한 Token 인지 확인하여 배재룰에 포함된 token은 제외한다.
    public void addTokenCheck(Token token) {
        block.addToken(token);
    }

    /** Get a HTMLBlock from the pagepart given.
     * @return
     */
    public  HTMLBlock parse () throws HTMLParseException {
        block = new HTMLBlock (pagepart, length);
        nextToken = START;
        match (START);
        page ();

        return block;
    }

    /**
     * A method to parse a string.
     * Html Pasing Main Function
     * @throws Exception
     * @return
     */
    public String  htmlParsing(boolean isImgAltTag, String[] denyId, String[] denyClass, boolean onlyHead) throws Exception {
        HTMLBlock block = parse();

        boolean isAppend = true, isDenyClass = false;
        String tagType;
        String startTag = "";
        ArrayList htmlList = new ArrayList();
        
        while(block.hasMoreTokens()) {
            Token token = block.nextToken();

            if(token.getTag() != null && token.getType() != Token.TEXT) {
                tagType = token.getTag().getLowerCaseType();
                
                String className = token.getTag().getAttributeValue("class");
                for(int i=0; className != null && denyClass != null && i < denyClass.length; i++) {
                    if(denyClass[i].equals(className)) {
                        isAppend = false;
                        isDenyClass = true;
                        startTag = token.getTag().getLowerCaseType();
                        break;
                    }
                }

                if(tagType.equals("div")) {
                    String id = token.getTag().getAttributeValue("id");
                    for(int i=0; denyId != null && i < denyId.length; i++) {
                        if(denyId[i].equals(id)) {
                            isAppend = false;
                            break;
                        }
                    }
                }

                if(tagType.equals("/div")) {
                    isAppend = true;
                }
                if(isDenyClass && token.getTag().getLowerCaseType().equals("/"+startTag)) {
                    isDenyClass = false;
                    isAppend = true;
                    startTag = "";
                }
            }

            if(isAppend) {
                htmlList.add(token);
            }
        }

        StringBuffer res = new StringBuffer();
        
        if(onlyHead) {
        	int bodyIndex = 0;
        	
        	for(int i=0; i<htmlList.size(); i++) {
        		Token t = (Token) htmlList.get(i);
        		
        		if(t.getTag() != null && t.getTag().getLowerCaseType().equals("body")) {
        			bodyIndex = i;
        			
        			break;
        		}
        	}
        	
        	while(true) {
        		Token t = (Token) htmlList.get(bodyIndex);
        		
        		String strHtml = t.toString();
    			strHtml = strHtml.replace('\t',' ');
                strHtml = strHtml.replace('\r',' ');
                strHtml = strHtml.replace('\n',' ');
                strHtml = strHtml.trim();
    			
    			res.append(strHtml);
    			
    			if(t.getTag() != null && t.getTag().getLowerCaseType().equals("/body")) {
    				break;
    			} else {
    				bodyIndex++;
    			}
        	}
        	/*
        	 if(t.getTag().getLowerCaseType().equals("body") && onlyHead) {
                		for(int idx=i; idx < htmlList.size(); idx++) {
                			t = (Token) htmlList.get(idx);
                			
                			String strHtml = t.toString();
                			strHtml = strHtml.replace('\t',' ');
                            strHtml = strHtml.replace('\r',' ');
                            strHtml = strHtml.replace('\n',' ');
                            strHtml = strHtml.trim();
                			
                			res.append(strHtml);
                			
                			if(t.getTag() != null && t.getTag().getLowerCaseType().equals("/body"))
                				break;
                		}
                		break;
                	}
        	 */
        } else {
        	for (int i=0 ; i < htmlList.size(); i++) {
                Token t = (Token) htmlList.get(i);
                //파싱된 데이터가 text이면 ...
                if(t.getType() == STRING) {
                    String strHtml = t.getText();
                    //strHtml = convertFormattedTextToPlaintext(strHtml);
                    strHtml = strHtml.replace('\t',' ');
                    strHtml = strHtml.replace('\r',' ');
                    strHtml = strHtml.replace('\n',' ');
                    strHtml = StringUtil.trimDuplecateSpace(strHtml);
                    strHtml = addSpace(strHtml);
                    res.append(strHtml);
                } else if(t.getType() == Token.TAG) {
                    if(isImgAltTag && (t.getTag().getLowerCaseType().equals("img") || t.getTag().getLowerCaseType().equals("area"))) {
                        if(t.getTag().getAttributeValue("alt") != null) {
                            res.append(t.getTag().getAttributeValue("alt"));
                        }
                    }
                }
            }
        }
        
        return res.toString();
    }

    private String addSpace(String word) {
        if(!word.startsWith(" ") && !word.endsWith(" "))  {
            word = " " + word + " ";
        }
        return word;
    }

    /**
     *
     * @param tag
     * @return
     * @throws Exception
     */
    public synchronized String getTagValue (String tag) throws Exception {
        int counter = 0 ;
        //Vector tagValues = new Vector();
        int check = -1;
        String title = "";
        for (HTMLBlock block = parse(); block.hasMoreTokens();) {
            Token t = block.nextToken();
            if (counter++ % 1 == 0) {
                if (t.getType() == Token.TEXT) {
                    if( check == 0 ) {
                        title =  t.getText();
                        //tagValues.add(tempStr);
                        check = -1;
                    }
                } else if (t.getType() == Token.TAG){
                    String tagName = t.getTag().getType().toLowerCase();
                    if(tagName.equals(tag)) {
                        //System.out.println(": "+ tagName);
                        check = 0;
                    }
                }
            }
        }
        return title;
    }

    public String getTagValue (String tag, int count) throws Exception {
        String strRes = "";
        String tempStr = "";
        int counter = 0 , chkCnt =1 ;
        for (HTMLBlock block = parse(); block.hasMoreTokens();) {
            Token t = block.nextToken();
            if (counter++ % 1 == 0) {
                if (t.getType() == Token.TEXT)
                    ;
                    // t.setText ("[" + t.getText () + "]");
                else if (t.getType() == Token.TAG)
                    ;
                //t.getTag ().addArg ("is", "new");
            }
            if(t.getType() == 1) {
                tempStr = t.getText();
            }
            if (t.getType() ==2
                    && t.getTag().getType().toLowerCase().equals(tag) ) {
                if(chkCnt == count){
                    strRes = tempStr ;
                    break;
                }
                chkCnt ++;
            }
        }
        return strRes;
    }

    /** Simple self test function.
     */
    public static void main (String[] args) {
        try {
            BufferedReader in = new BufferedReader(
                       new InputStreamReader(
                                  new FileInputStream(new File("D:\\bitext.html")), "UTF-8"));

            String s;
            StringBuffer b = new StringBuffer();
            while( (s = in.readLine()) != null) {
                b.append(s );
            }
            String page= b.toString();
            HTMLParser parser = new HTMLParser(page.getBytes("UTF-8"));
            //parser.getTagValue ("meta");
            //System.out.println();

            System.out.println(parser.htmlParsing (false, null, null, false));
            //System.out.println(htmlParsing (page3, "euc-kr"));


        } catch (Throwable t) {
            t.printStackTrace ();
        }
    }


}