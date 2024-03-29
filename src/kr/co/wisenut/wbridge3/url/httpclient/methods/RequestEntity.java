/*
 * $Header: /home/cvs/Bridge/Standard/v3.8.1/WBridge/src/kr/co/wisenut/wbridge3/url/httpclient/methods/RequestEntity.java,v 1.1.1.1 2009/03/10 07:02:41 wisenut Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2009/03/10 07:02:41 $
 *
 * ====================================================================
 *
 *  Copyright 2004 The Apache Software Foundation
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

package kr.co.wisenut.wbridge3.url.httpclient.methods;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @since 3.0
 */
public interface RequestEntity {

    /**
     * Tests if {@link #writeRequest(OutputStream)} can be called more than once.
     * 
     * @return <tt>true</tt> if the entity can be written to {@link OutputStream} more than once, 
     * <tt>false</tt> otherwise.
     */
    boolean isRepeatable();

    /**
     * Writes the request entity to the given stream.
     * @param out
     * @throws IOException
     */
    void writeRequest(OutputStream out) throws IOException;
    
    /**
     * Gets the request entity's length. This method should return a non-negative value if the content 
     * length is known or a negative value if it is not. In the latter case the
     * {@link kr.co.wisenut.wbridge3.url.httpclient.methods.EntityEnclosingMethod} will use chunk encoding to
     * transmit the request entity.
     *  
     * @return a non-negative value when content length is known or a negative value when content length 
     * is not known  
     */
    long getContentLength();
    
    /**
     * Gets the entity's content type.  This content type will be used as the value for the
     * "Content-Type" header.
     * @return the entity's content type
     * @see kr.co.wisenut.wbridge3.url.httpclient.HttpMethod#setRequestHeader(String, String)
     */
    String getContentType();
    
}
