package kr.co.wisenut.common.scdreceiver.stream;



import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * MessageOutput
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public abstract class MessageOutput extends DataOutputStream {
    protected MessageOutput (OutputStream out) {
        super (out);
    }

    public abstract void send () throws IOException;

    public void send (String[] dsts) throws IOException {
        throw new IOException ("send[] not supported");
    }

    public void send (String dst) throws IOException {
        String[] dsts = { dst };
        send (dsts);
    }
}
