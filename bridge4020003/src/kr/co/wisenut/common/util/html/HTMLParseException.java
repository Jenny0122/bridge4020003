package kr.co.wisenut.common.util.html;

// $Id: HTMLParseException.java,v 1.3 2009/06/10 05:20:34 wisenut Exp $

/** This Exception indicates an error in the parsing of an HTML block.
 */
public class HTMLParseException extends Exception {

    /** Create a new HTMLParseException with the given string.
     * @param s the reason for the Exception.
     */
    public HTMLParseException (String s) {
	super (s);
    }

    /** Create a new HTMLParseException with the given Throwable.
     * @param t the reason for the Exception.
     */
    public HTMLParseException (Throwable t) {
	super (t.toString ());
    }
}

