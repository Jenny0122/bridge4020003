/*
 * $Header: /home/cvs/Bridge/Standard/v3.8.1/WBridge/src/kr/co/wisenut/wbridge3/url/httpclient/ProtocolException.java,v 1.1.1.1 2009/03/10 07:02:40 wisenut Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2009/03/10 07:02:40 $
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

/**
 * Signals that an HTTP protocol violation has occurred.  For example, HttpClient
 * detected a malformed status line or headers, a missing message body, etc.
 * 
 * @author <a href="mailto:laura@lwerner.org">Laura Werner</a>
 * 
 * @since 3.0
 */
public class ProtocolException extends HttpException {

    /**
     * Creates a new ProtocolException with a <tt>null</tt> detail message. 
     */
    public ProtocolException() {
        super();
    }

    /**
     * Creates a new ProtocolException with the specified detail message.
     * 
     * @param message The exception detail message
     */
    public ProtocolException(String message) {
        super(message);
    }

    /**
     * Creates a new ProtocolException with the specified detail message and cause.
     * 
     * @param message the exception detail message
     * @param cause the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
     * if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
     */
    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}
