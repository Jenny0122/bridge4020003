/*
 * $Header: /home/cvs/Bridge/Standard/v3.8.1/WBridge/src/kr/co/wisenut/wbridge3/url/httpclient/methods/multipart/PartSource.java,v 1.1.1.1 2009/03/10 07:02:42 wisenut Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2009/03/10 07:02:42 $
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
 
package kr.co.wisenut.wbridge3.url.httpclient.methods.multipart;

import java.io.IOException;
import java.io.InputStream;

/**
 * An interface for providing access to data when posting MultiPart messages.
 * 
 * @see FilePart
 * 
 * @author <a href="mailto:becke@u.washington.edu">Michael Becke</a>
 *   
 * @since 2.0 
 */
public interface PartSource {

    /**
     * Gets the number of bytes contained in this source.
     * 
     * @return a value >= 0
     */
    long getLength();
    
    /**
     * Gets the name of the file this source represents.
     * 
     * @return the fileName used for posting a MultiPart file part
     */
    String getFileName();
    
    /**
     * Gets a new InputStream for reading this source.  This method can be 
     * called more than once and should therefore return a new stream every
     * time.
     * 
     * @return a new InputStream
     * 
     * @throws IOException if an error occurs when creating the InputStream
     */
    InputStream createInputStream() throws IOException;

}
