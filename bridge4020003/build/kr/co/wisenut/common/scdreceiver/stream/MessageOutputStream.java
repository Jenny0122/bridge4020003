package kr.co.wisenut.common.scdreceiver.stream;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * MessageOutputStream
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public class MessageOutputStream extends MessageOutput {
  protected OutputStream rawOut;
  protected DataOutputStream dataOut;
  protected ByteArrayOutputStream byteArrayOut;

  public MessageOutputStream (OutputStream out) {
    super (new ByteArrayOutputStream ());
    rawOut = out;
    dataOut = new DataOutputStream (rawOut);
    byteArrayOut = (ByteArrayOutputStream) super.out;
  }

  public void send () throws IOException {
    synchronized (rawOut) {
      dataOut.writeInt (byteArrayOut.size ());
      byteArrayOut.writeTo (rawOut);
    }
    byteArrayOut.reset ();
    rawOut.flush ();
  }
}