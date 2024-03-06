package kr.co.wisenut.common.util.html;
import kr.co.wisenut.common.util.html.Tag;

import java.io.UnsupportedEncodingException;
import java.io.Serializable;

// $Id: Token.java,v 1.3 2009/06/10 05:20:34 wisenut Exp $

/** This class is used to describe a small part of a html page.
 *  A small part is the text between the tags, a tag, a comment or a script.
 */
public class Token  {
    /** This token is empty */
    public final static int EMPTY   = 0;
    /** This token is text. */
    public final static int TEXT    = 1;
    /** This token is a tag. */
    public final static int TAG     = 2;
    /** This token is a comment. */
    public final static int COMMENT = 3;
    /** This token is a script */
    public final static int SCRIPT  = 4;

    private String text;
    private byte[] page;
    private Tag tag;
    private int type;
    private boolean changed = false;
    private int startindex = 0;
    private int len = 0;

    /** Create a new Token of type TEXT with given text.
     * @param text the String of this Token.
     */
    public Token (String text) {
        this (text, TEXT, -1);
        setChanged (true);
    }

    /** Create a new Token of type Tag with given Tag.
     * @param tag the Tag of this Token.
     */
    public Token (Tag tag) {
        this (tag, -1);
        setChanged (true);
    }

    /** Create a new Token of type Tag with given Tag.
     * @param tag the Tag of this Token.
     * @param changed if true this tag is considered changed, if false this tag is unmodified.
     */
    public Token (Tag tag, boolean changed) {
        this (tag, -1);
        setChanged (changed);
    }

    /** Create a new Token with given arguments.
     * @param text the text of this Token.
     * @param type the type of this token.
     * @param startindex the start index of this token.
     */
    public Token (String text, int type, int startindex) {
        this.text = text;
        this.type = type;
        this.startindex = startindex;
    }

    /** Create a new Token with given arguments.
     * @param page the page this token is from.
     * @param type the type of this token.
     * @param startindex the start index of this token.
     * @param len the length of this token.
     */
    public Token (byte[] page, int type, int startindex, int len) {
        this.page = page;
        this.type = type;
        this.startindex = startindex;
        this.len = len;
    }

    /** Create a new Token with given arguments and of type TAG.
     * @param tag the Tag of this Token.
     * @param startindex the start index of this token.
     */
    public Token (Tag tag, int startindex) {
        this.tag = tag;
        this.type = TAG;
        tag.setToken (this);
        this.startindex = startindex;
    }

    /** Get the tag of this token.
     * @return the Tag or null if type is other than TAG.
     */
    public Tag getTag () {
        return tag;
    }

    /** Set the tag of this token, also set the type to TAG.
     * @param tag the Tag to hold.
     */
    public void setTag (Tag tag) {
        this.tag = tag;
        this.type = TAG;
        tag.setToken (this);
        this.changed = true;
    }

    /** Get the text of this token.
     * @return the text or null if type is other than TEXT.
     */
    public String getText () throws UnsupportedEncodingException{
        if ((type == TEXT || type == COMMENT) && text == null  ){
            if( !(startindex > page.length - len) ) {
                text = new String (page, startindex, len, "UTF-8");
            } else {
                text = "";
            }
        }
        //else System.out.println(new String (page));
        return text;
    }

    /** Set the text of this Token, also sets the type to TEXT.
     * @param text the text of this token.
     */
    public void setText (String text) {
        this.text = text;
        this.type = TEXT;
        this.changed = true;
    }

    /** Get the type of this token.
     * @return the type of this token.
     */
    public int getType () {
        return type;
    }

    /** Has this Token changed since it was created?
     * @return true if this Token has changed, false otherwise.
     */
    public boolean getChanged () {
        return changed;
    }

    /** Set the change value of this Token.
     * @param changed the new change value of this Token.
     */
    public void setChanged (boolean changed) {
        this.changed = changed;
    }

    /** Get the start index of this Token.
     * @return the start index.
     */
    public int getStartIndex () {
        return startindex;
    }

    /** Set the start index of this Token.
     * @param startindex the new startindex.
     */
    public void setStartIndex (int startindex) {
        this.startindex = startindex;
    }

    /** Get the length of this token.
     */
    public int getLength () {
        return len;
    }

    /** Empty this token, That is set its type to EMPTY and set the
     *	text and tag to null.
     */
    public void empty () {
        type = EMPTY;
        setChanged (false);
        text = null;
        tag = null;
    }

    /** Get the String representation of this object.
     * @return a String representation of this object.
     */
    public String toString () {
        if (text == null && page != null && type != TAG)
            text = new String (page, startindex, len);
        switch (type) {
            case EMPTY:
                return "";
            case TEXT:
                return text;
            case TAG:
                return tag.toString ();
            case COMMENT:
                return text;
            case SCRIPT:
                return text;
            default:
                return text;
        }
    }
}
