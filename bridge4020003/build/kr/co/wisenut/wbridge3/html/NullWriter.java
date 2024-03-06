/*
 * @(#)NullWriter.java   3.8.1 2009/03/11
 *
 */

package kr.co.wisenut.wbridge3.html;

import java.io.Writer;

/**
 *
 * NullWriter
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class NullWriter extends Writer{

  public NullWriter() {}

  public void close() {}

  public void flush() {}

  public void write(char[] cbuf, int off, int len) {}
}
