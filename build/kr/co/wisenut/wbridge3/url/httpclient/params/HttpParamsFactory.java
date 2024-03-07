/*
 * $Header: /home/cvs/Bridge/Standard/v3.8.1/WBridge/src/kr/co/wisenut/wbridge3/url/httpclient/params/HttpParamsFactory.java,v 1.1.1.1 2009/03/10 07:02:42 wisenut Exp $
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
 
package kr.co.wisenut.wbridge3.url.httpclient.params;

/**
 * A factory for getting the default set of parameters to use when creating an instance of 
 * <code>HttpParams</code>.
 * 
 * @see kr.co.wisenut.wbridge3.url.httpclient.params.DefaultHttpParams#setHttpParamsFactory(HttpParamsFactory)
 * 
 * @since 3.0
 */
public interface HttpParamsFactory {

    /**
     * Gets the default parameters.  This method may be called more than once and is not required
     * to always return the same value.
     * 
     * @return an instance of HttpParams
     */
    HttpParams getDefaultParams();

}
