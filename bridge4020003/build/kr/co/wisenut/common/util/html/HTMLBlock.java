package kr.co.wisenut.common.util.html;

// $Id: HTMLBlock.java,v 1.2 2009/06/10 05:20:34 wisenut Exp $

import java.io.*;
import java.util.*;

/** This class is used to describe a piece of a HTML page.
 *  A block is composed of Tokens and a rest (unparseable data,
 *  unfinished tags etc).
 */
public class HTMLBlock {

    private List tokens = new ArrayList ();
    private int currentToken = 0;

    private byte[] realpage = null;
    private int length = -1;
    //private String rest = "";
    private int restStart = -1;

    /** Create a HTMLBLock from the given byte array.
     * @param page the byte array that is the real page
     */
    public HTMLBlock (byte[] page, int length) {
        this.realpage = page;
        this.length = length;
        restStart = length;
    }

    /** Set the rest of the page to start at given position.
     * @param reststart the new index of the rest of the page.
     */
    public void setRest (int reststart) {
        this.restStart = reststart;
    }

    /** Get the rest of this block.
     * @return the rest part of the block
     */
    public String getRest () {
        if (realpage != null)
            return new String (realpage, restStart, length-restStart);
        return "";
    }

    /** Get the number of bytes that the rest is.
     * @return the length of the rest.
     */
    public int restSize () {
        return length - restStart;
    }

    /** Copy the rest into the given byte array.
     * @param b the byte array to copy the rest into.
     */
    public void insertRest (byte[] b) {
        System.arraycopy (realpage, restStart, b, 0, length - restStart);
    }

    /** Add a Token to this block.
     * @param t the Token to add.
     */
    public void addToken (Token t) {
        tokens.add (t);
    }

    /** Does this block have more tokens?
     * @return true if there is unfetched tokens, false otherwise.
     */
    public boolean hasMoreTokens () {
        return (tokens.size () > currentToken);
    }

    /** Get the next Token.
     * @return the next Token or null if there are no more tokens.
     */
    public Token nextToken () {
        if (hasMoreTokens ())
            return (Token)tokens.get (currentToken++);
        return null;
    }

    /** Get a List of the Tokens.
     * @return a List with the Tokens for this block.
     */
    public List getTokens () {
        return tokens;
    }

    /** Insert a token at given position.
     * @param t the Token to insert.
     * @param pos the position to insert the token at.
     */
    public void insertToken (Token t, int pos) {
        t.setChanged (true);
        if (pos < tokens.size ()) {
            Token moved = (Token) tokens.get (pos);
            t.setStartIndex (moved.getStartIndex());
            tokens.add (pos, t);
        } else {
            t.setStartIndex (length - 1);
            tokens.add (t);
        }
    }

    /** Remove a Token at the given position.
     * @param pos the position of the token to remove.
     */
    public void removeToken (int pos) {
        Token t = (Token)tokens.get (pos);
        t.empty ();
        t.setChanged (true);
    }

    /** Get a String representation of this block.
     * @return a String with the content of this block.
     */
    public String toString () {
        StringBuffer res = new StringBuffer();
        int start = 0;
        int tsize = tokens.size ();
        for (int i = 0; i < tsize; i++) {
            Token t = (Token)tokens.get (i);
            if (t.getChanged ()) {
                res.append (new String (realpage, start, t.getStartIndex () - start));
                res.append (t.toString ());
                if (tokens.size () > i + 1)
                    start = ((Token)tokens.get (i+1)).getStartIndex ();
                else
                    start = length - 1;
            }
        }
        res.append (new String (realpage, start, restStart - start));
        return res.toString ();
    }

    /** Send this block (but not the rest part) on the given stream.
     * @param out the Stream to send the data on.
     * @throws IOException if writing the data fails.
     */
    public void send (OutputStream out) throws IOException {
        int start = 0;
        int tsize = tokens.size ();
        for (int i = 0; i < tsize; i++) {
            Token t = (Token)tokens.get (i);
            if (t.getChanged ()) {
                int d = t.getStartIndex () - start;
                if (d > 0)
                    out.write (realpage, start, d);
                byte[] b = t.toString ().getBytes ();
                if (b.length > 0)
                    out.write (b);
                if (tokens.size () > i + 1)
                    start = ((Token)tokens.get (i+1)).getStartIndex ();
                else
                    start = length - 1;
            }
        }
        if (start < restStart)
            out.write (realpage, start, restStart - start);
    }

    /** Send the rest of the data on the given stream.
     * @param out the Stream to send the data on.
     * @throws IOException if writing the data fails.
     */
    public void sendRest (OutputStream out) throws IOException {
        if (restStart < length) {
            out.write (realpage, restStart, length - restStart);
        }
    }
}
