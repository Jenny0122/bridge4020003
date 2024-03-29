//jdk1.5

/*
 * @(#)AttVal.java   1.11 2000/08/16
 *
 */

package kr.co.wisenut.wbridge3.html.tidy;

/**
 *
 * Attribute/Value linked list node
 *
 * (c) 1998-2000 (W3C) MIT, INRIA, Keio University
 * See Tidy.java for the copyright notice.
 * Derived from <a href="http://www.w3.org/People/Raggett/tidy">
 * HTML Tidy Release 4 Aug 2000</a>
 *
 * @author  Dave Raggett <dsr@w3.org>
 * @author  Andy Quick <ac.quick@sympatico.ca> (translation to Java)
 * @version 1.0, 1999/05/22
 * @version 1.0.1, 1999/05/29
 * @version 1.1, 1999/06/18 Java Bean
 * @version 1.2, 1999/07/10 Tidy Release 7 Jul 1999
 * @version 1.3, 1999/07/30 Tidy Release 26 Jul 1999
 * @version 1.4, 1999/09/04 DOM support
 * @version 1.5, 1999/10/23 Tidy Release 27 Sep 1999
 * @version 1.6, 1999/11/01 Tidy Release 22 Oct 1999
 * @version 1.7, 1999/12/06 Tidy Release 30 Nov 1999
 * @version 1.8, 2000/01/22 Tidy Release 13 Jan 2000
 * @version 1.9, 2000/06/03 Tidy Release 30 Apr 2000
 * @version 1.10, 2000/07/22 Tidy Release 8 Jul 2000
 * @version 1.11, 2000/08/16 Tidy Release 4 Aug 2000
 */

public class AttVal extends Object implements Cloneable {

    public AttVal    next;
    public Attribute dict;
    public Node      asp;
    public Node      php;
    public int       delim;
    public String    attribute;
    public String    value;

    public AttVal()
    {
        this.next = null;
        this.dict = null;
        this.asp = null;
        this.php = null;
        this.delim = 0;
        this.attribute = null;
        this.value = null;
    }

    public AttVal(AttVal next, Attribute dict, int delim,
                  String attribute, String value)
    {
        this.next = next;
        this.dict = dict;
        this.asp = null;
        this.php = null;
        this.delim = delim;
        this.attribute = attribute;
        this.value = value;
    }

    public AttVal(AttVal next, Attribute dict, Node asp, Node php,
                  int delim, String attribute, String value)
    {
        this.next = next;
        this.dict = dict;
        this.asp = asp;
        this.php = php;
        this.delim = delim;
        this.attribute = attribute;
        this.value = value;
    }

    protected Object clone()
    {
        AttVal av = new AttVal();
        if (next != null) {
            av.next = (AttVal)next.clone();
        }
        if (attribute != null)
            av.attribute = attribute;
        if (value != null)
            av.value = value;
        av.delim = delim;
        if (asp != null) {
            av.asp = (Node)asp.clone();
        }
        if (php != null) {
            av.php = (Node)php.clone();
        }
        av.dict =
          AttributeTable.getDefaultAttributeTable().findAttribute(this);
        return av;
    }

    public boolean isBoolAttribute()
    {
        Attribute attribute = this.dict;
        if ( attribute != null ) {
            if (attribute.attrchk == AttrCheckImpl.getCheckBool() ) {
                return true;
            }
        }

        return false;
    }

    /* ignore unknown attributes for proprietary elements */
    public Attribute checkAttribute( Lexer lexer, Node node )
    {
        TagTable tt = lexer.configuration.tt;

        if (this.asp == null && this.php == null)
            this.checkUniqueAttribute(lexer, node);

        Attribute attribute = this.dict;
        if ( attribute != null ) {
            /* title is vers 2.0 for A and LINK otherwise vers 4.0 */
            if (attribute == AttributeTable.attrTitle &&
                    (node.tag == tt.tagA || node.tag == tt.tagLink))
                    lexer.versions &= Dict.VERS_ALL;
            else if ((attribute.versions & Dict.VERS_XML) != 0)
            {
                if (!(lexer.configuration.XmlTags || lexer.configuration.XmlOut))
                    Report.attrError(lexer, node, this.attribute, Report.XML_ATTRIBUTE_VALUE);
            }
            else
                lexer.versions &= attribute.versions;
        
            if (attribute.attrchk != null)
                attribute.attrchk.check(lexer, node, this);
        }
        else if (!lexer.configuration.XmlTags && !(node.tag == null) && this.asp == null &&
                 !(node.tag != null && ((node.tag.versions & Dict.VERS_PROPRIETARY) != 0)))
            Report.attrError(lexer, node, this.attribute, Report.UNKNOWN_ATTRIBUTE);

        return attribute;
    }

    /*
     the same attribute name can't be used
     more than once in each element
    */
    public void checkUniqueAttribute(Lexer lexer, Node node)
    {
        AttVal attr;
        int count = 0;

        for (attr = this.next; attr != null; attr = attr.next)
        {
            if (this.attribute != null &&
                attr.attribute != null &&
                attr.asp == null &&
                attr.php == null &&
                Lexer.wstrcasecmp(this.attribute, attr.attribute) == 0)
                ++count;
        }

        if (count > 0)
            Report.attrError(lexer, node, this.attribute, Report.REPEATED_ATTRIBUTE);
    }

    /* --------------------- DOM ---------------------------- */

    protected org.w3c.dom.Attr adapter = null;

    protected org.w3c.dom.Attr getAdapter()
    {
        if (adapter == null)
        {
            adapter = new DOMAttrImpl(this);
        }
        return adapter;
    }
    /* --------------------- END DOM ------------------------ */

    /**
     * Getter for <code>asp</code>.
     * @return Returns the asp.
     */
    public Node getAsp()
    {
        return this.asp;
    }

    /**
     * Setter for <code>asp</code>.
     * @param asp The asp to set.
     */
    public void setAsp(Node asp)
    {
        this.asp = asp;
    }

    /**
     * Getter for <code>attribute</code>.
     * @return Returns the attribute.
     */
    public String getAttribute()
    {
        return this.attribute;
    }

    /**
     * Setter for <code>attribute</code>.
     * @param attribute The attribute to set.
     */
    public void setAttribute(String attribute)
    {
        this.attribute = attribute;
    }

    /**
     * Getter for <code>delim</code>.
     * @return Returns the delim.
     */
    public int getDelim()
    {
        return this.delim;
    }

    /**
     * Setter for <code>delim</code>.
     * @param delim The delim to set.
     */
    public void setDelim(int delim)
    {
        this.delim = delim;
    }

    /**
     * Getter for <code>dict</code>.
     * @return Returns the dict.
     */
    public Attribute getDict()
    {
        return this.dict;
    }

    /**
     * Setter for <code>dict</code>.
     * @param dict The dict to set.
     */
    public void setDict(Attribute dict)
    {
        this.dict = dict;
    }

    /**
     * Getter for <code>next</code>.
     * @return Returns the next.
     */
    public AttVal getNext()
    {
        return this.next;
    }

    /**
     * Setter for <code>next</code>.
     * @param next The next to set.
     */
    public void setNext(AttVal next)
    {
        this.next = next;
    }

    /**
     * Getter for <code>php</code>.
     * @return Returns the php.
     */
    public Node getPhp()
    {
        return this.php;
    }

    /**
     * Setter for <code>php</code>.
     * @param php The php to set.
     */
    public void setPhp(Node php)
    {
        this.php = php;
    }

    /**
     * Getter for <code>value</code>.
     * @return Returns the value.
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * Setter for <code>value</code>.
     * @param value The value to set.
     */
    public void setValue(String value)
    {
        this.value = value;
    }    
}
