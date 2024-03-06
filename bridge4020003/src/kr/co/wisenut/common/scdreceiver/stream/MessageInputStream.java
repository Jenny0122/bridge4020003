package kr.co.wisenut.common.scdreceiver.stream;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * MessageInputStream
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class MessageInputStream extends MessageInput {
    protected InputStream rawIn;
    protected DataInputStream dataIn;

    public MessageInputStream (InputStream in) {
        super (new ByteArrayInputStream (new byte[0]));
        rawIn = in;
        dataIn = new DataInputStream (rawIn);
    }

    public void receive () throws IOException {
        synchronized (rawIn) {
            int length = dataIn.readInt ();
            byte[] buffer = new byte[length];
            dataIn.readFully (buffer);
            in = new ByteArrayInputStream (buffer);
        }
    }
}
