/*
 * @(#)LinkNameBinding.java   3.8.1 2009/03/11
 *
 */

package kr.co.wisenut.wbridge3.bdb;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 *
 * LinkNameBinding
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class LinkNameBinding extends TupleBinding {
    // Implement this abstract method. Used to convert
    // a DatabaseEntry to an kr.co.wisenut.bdb.bind.LinkName object.
    public Object entryToObject(TupleInput ti) {
        String foreignkey = ti.readString();
        String strLinkName = ti.readString();

        RichURL linkName = new RichURL();
        linkName.setURL(foreignkey, strLinkName);
        return linkName;
    }

    // Implement this abstract method. Used to convert a
    // kr.co.wisenut.bdb.bind.LinkName object to a DatabaseEntry object.
    public void objectToEntry(Object object, TupleOutput to) {
        RichURL linkName = (RichURL)object;
        to.writeString(linkName.getURL());
        to.writeString(linkName.getSource(""));
    }
}
