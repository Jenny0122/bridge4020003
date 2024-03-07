package kr.co.wisenut.common.scdreceiver.stream;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * MessageInput
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */

public abstract class MessageInput extends DataInputStream {
  protected MessageInput (InputStream in) {
    super (in);
  }

  public abstract void receive () throws IOException;
}
