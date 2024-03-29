/*
 * $Header: /home/cvs/Bridge/Standard/v3.8.1/WBridge/src/kr/co/wisenut/wbridge3/url/httpclient/Wire.java,v 1.1.1.1 2009/03/10 07:02:41 wisenut Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2009/03/10 07:02:41 $
 *
 * ====================================================================
 *
 *  Copyright 1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package kr.co.wisenut.wbridge3.url.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

/**
 * Logs data to the wire LOG.
 *
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * 
 * @since 2.0beta1
 */
class Wire {

 //   public static Wire HEADER_WIRE = new Wire(LogFactory.getLog("httpclient.wire.header"));
    
 //   public static Wire CONTENT_WIRE = new Wire(LogFactory.getLog("httpclient.wire.content"));
    
    /** Log for any wire messages. */
//    private Log log;
    
/*    private Wire(Log log) {
        this.log = log;
    }*/
    
    private void wire(String header, InputStream instream)
      throws IOException {
        StringBuffer buffer = new StringBuffer();
        int ch;
        while ((ch = instream.read()) != -1) {
            if (ch == 13) {
                buffer.append("[\\r]");
            } else if (ch == 10) {
                    buffer.append("[\\n]\"");
                    buffer.insert(0, "\"");
                    buffer.insert(0, header);
                    ////LOG.debug(buffer.toString());
                    buffer.setLength(0);
            } else if ((ch < 32) || (ch > 127)) {
                buffer.append("[0x");
                buffer.append(Integer.toHexString(ch));
                buffer.append("]");
            } else {
                buffer.append((char) ch);
            }
        } 
        if (buffer.length() > 0) {
            buffer.append("\"");
            buffer.insert(0, "\"");
            buffer.insert(0, header);
           // //LOG.debug(buffer.toString());
        }
    }


   /* public boolean enabled() {
        return log.isDebugEnabled();
    }  */
    
    public void output(InputStream outstream)
      throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output may not be null"); 
        }
        wire(">> ", outstream);
    }

    public void input(InputStream instream)
      throws IOException {
        if (instream == null) {
            throw new IllegalArgumentException("Input may not be null"); 
        }
        wire("<< ", instream);
    }

    public void output(byte[] b, int off, int len)
      throws IOException {
        if (b == null) {
            throw new IllegalArgumentException("Output may not be null"); 
        }
        wire(">> ", new ByteArrayInputStream(b, off, len));
    }

    public void input(byte[] b, int off, int len)
      throws IOException {
        if (b == null) {
            throw new IllegalArgumentException("Input may not be null"); 
        }
        wire("<< ", new ByteArrayInputStream(b, off, len));
    }

    public void output(byte[] b)
      throws IOException {
        if (b == null) {
            throw new IllegalArgumentException("Output may not be null"); 
        }
        wire(">> ", new ByteArrayInputStream(b));
    }

    public void input(byte[] b)
      throws IOException {
        if (b == null) {
            throw new IllegalArgumentException("Input may not be null"); 
        }
        wire("<< ", new ByteArrayInputStream(b));
    }

    public void output(int b)
      throws IOException {
        output(new byte[] {(byte) b});
    }

    public void input(int b)
      throws IOException {
        input(new byte[] {(byte) b});
    }

    public void output(final String s)
      throws IOException {
        if (s == null) {
            throw new IllegalArgumentException("Output may not be null"); 
        }
        output(s.getBytes());
    }

    public void input(final String s)
      throws IOException {
        if (s == null) {
            throw new IllegalArgumentException("Input may not be null"); 
        }
        input(s.getBytes());
    }
}
