/*
 * $Header: /home/cvs/Bridge/Standard/v3.8.1/WBridge/src/kr/co/wisenut/wbridge3/url/httpclient/WireLogOutputStream.java,v 1.1.1.1 2009/03/10 07:02:41 wisenut Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2009/03/10 07:02:41 $
 *
 * ====================================================================
 *
 *  Copyright 2002-2004 The Apache Software Foundation
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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Logs all data written to the wire LOG.
 *
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * 
 * @since 2.0beta1
 */
class WireLogOutputStream extends FilterOutputStream {

    /** Original input stream. */
    private OutputStream out;
    
    /** The wire log to use. */
    private Wire wire;

    /**
     * Create an instance that wraps the specified output stream.
     * @param out The output stream.
     * @param wire The Wire log to use.
     */
    public WireLogOutputStream(OutputStream out, Wire wire) {
        super(out);
        this.out = out;
        this.wire = wire;
    }
    
    /**
     * 
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write(b,  off,  len);
        wire.output(b, off, len);
    }

    /**
     * 
     * @see java.io.OutputStream#write()
     */
    public void write(int b) throws IOException {
        this.out.write(b);
        wire.output(b);
    }

    /**
     * 
     * @see java.io.OutputStream#write(byte[])
     */
    public void write(byte[] b) throws IOException {
        this.out.write(b);
        wire.output(b);
    }
}
