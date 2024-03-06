package kr.co.wisenut.common.util.html;

import java.util.*;
/**
 * Created by KoreaWISENut
 * User: KoreaWisenut
 * Date: 2005. 5. 16.
 * Time: 오후 4:52:31
 */

// $Id: TagType.java,v 1.2 2009/06/10 05:20:34 wisenut Exp $

import java.util.*;

/** This is a class that holds common tagtypes.
 */
public class TagType {
    private static boolean initialized = false;
    private static Map types;
    private String type;

    public static final TagType A = new TagType ("a");
    public static final TagType SA = new TagType ("/a");
    public static final TagType IMG = new TagType ("img");
    public static final TagType SIMG = new TagType ("/img");
    public static final TagType LAYER = new TagType ("layer");
    public static final TagType SLAYER = new TagType ("/layer");
    public static final TagType SCRIPT = new TagType ("script");
    public static final TagType SSCRIPT = new TagType ("/script");
    public static final TagType BODY = new TagType ("body");
    public static final TagType SBODY = new TagType ("/body");
    public static final TagType TABLE = new TagType ("table");
    public static final TagType STABLE = new TagType ("/table");
    public static final TagType TR = new TagType ("tr");
    public static final TagType STR = new TagType ("/tr");
    public static final TagType TD = new TagType ("td");
    public static final TagType STD = new TagType ("/td");
    public static final TagType BLINK = new TagType ("blink");
    public static final TagType SBLINK = new TagType ("/blink");
    public static final TagType DOCTYPE = new TagType ("!doctype");
    public static final TagType JAVASCRIPT = new TagType ("javascript:");
    public static final TagType DIV = new TagType ("div");
    public static final TagType SDIV = new TagType ("/div");
    public static final TagType P = new TagType ("p");
    public static final TagType SP = new TagType ("/p");

    private synchronized static void initialize () {
        types = new HashMap ();
        types.put (A.toString (), A);
        types.put (SA.toString (), SA);
        types.put (IMG.toString (), IMG);
        types.put (SIMG.toString (), SIMG);
        types.put (LAYER.toString (), LAYER);
        types.put (SLAYER.toString (), SLAYER);
        types.put (SCRIPT.toString (), SCRIPT);
        types.put (SSCRIPT.toString (), SSCRIPT);
        types.put (BODY.toString (), BODY);
        types.put (SBODY.toString (), SBODY);
        types.put (TABLE.toString (), TABLE);
        types.put (STABLE.toString (), STABLE);
        types.put (TR.toString (), TR);
        types.put (STR.toString (), STR);
        types.put (TD.toString (), TD);
        types.put (STD.toString (), STD);
        types.put (BLINK.toString (), BLINK);
        types.put (SBLINK.toString (), SBLINK);
        types.put (JAVASCRIPT.toString (), JAVASCRIPT);
        types.put (DIV.toString (), DIV);
        types.put (SDIV.toString (), SDIV);
        types.put (P.toString (), P);
        types.put (SP.toString (), SP);
        initialized = true;
    }

    private TagType (String type) {
        this.type = type;
    }

    public static TagType getTagType (String type) {
        if (!initialized)
            initialize ();
        return (TagType)types.get (type);
    }

    public String toString () {
        return type;
    }
}

