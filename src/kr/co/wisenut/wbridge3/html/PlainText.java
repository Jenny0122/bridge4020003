/*
 * @(#)PlainText.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.wbridge3.html;

/**
 *
 * PlainText
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class PlainText {
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
    private  static String decodeNumericCharacterReferences(String value) {
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
}
