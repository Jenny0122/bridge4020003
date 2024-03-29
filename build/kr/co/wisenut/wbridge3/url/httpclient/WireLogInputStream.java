/*
 * $Header: /home/cvs/Bridge/Standard/v3.8.1/WBridge/src/kr/co/wisenut/wbridge3/url/httpclient/WireLogInputStream.java,v 1.1.1.1 2009/03/10 07:02:41 wisenut Exp $
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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Logs all data read to the wire LOG.
 *
 * @author Ortwin Gl占폺k
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * 
 * @since 2.0
 */
class WireLogInputStream extends FilterInputStream {
     
    /** Original input stream. */
    private InputStream in;

    /** The wire log to use for writing. */
    private Wire wire;
    
    /**
     * Create an instance that wraps the specified input stream.
     * @param in The input stream.
     * @param wire The wire log to use.
     */
    public WireLogInputStream(InputStream in, Wire wire) {
        super(in);
        this.in = in;
        this.wire = wire;
    }
    /**
     * 
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
        int l = this.in.read(b,  off,  len);
        if (l > 0) {
            wire.input(b, off, l);
        }
        return l;
    }

    /**
     * 
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        int l = this.in.read();
        if (l > 0) { 
            wire.input(l);
        }
        return l;
    }

    /**
     * 
     * @see java.io.InputStream#read(byte[])
     */
    public int read(byte[] b) throws IOException {
        int l = this.in.read(b);
        if (l > 0) {
            wire.input(b, 0, l);
        }
        return l;
    }
}
