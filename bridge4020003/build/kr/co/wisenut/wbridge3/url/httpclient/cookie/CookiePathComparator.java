/*
 * $Header: /home/cvs/Bridge/Standard/v3.8.1/WBridge/src/kr/co/wisenut/wbridge3/url/httpclient/cookie/CookiePathComparator.java,v 1.1.1.1 2009/03/10 07:02:41 wisenut Exp $
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

package kr.co.wisenut.wbridge3.url.httpclient.cookie;

import java.util.Comparator;

import kr.co.wisenut.wbridge3.url.httpclient.Cookie;

/**
 * This cookie comparator ensures that multiple cookies satisfying 
 * a common criteria are ordered in the <tt>Cookie</tt> header such
 * that those with more specific Path attributes precede those with
 * less specific.
 *  
 * <p>
 * This comparator assumes that Path attributes of two cookies 
 * path-match a commmon request-URI. Otherwise, the result of the
 * comparison is undefined.
 * </p>
 * 
 * @author <a href="mailto:oleg at ural.ru">Oleg Kalnichevski</a>
 * 
 * @since 3.1
 */
public class CookiePathComparator implements Comparator {

    private String normalizePath(final Cookie cookie) {
        String path = cookie.getPath();
        if (path == null) {
            path = "/";
        }
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }
    
    public int compare(final Object o1, final Object o2) {
        Cookie c1 = (Cookie) o1;
        Cookie c2 = (Cookie) o2;
        String path1 = normalizePath(c1);
        String path2 = normalizePath(c2);
        if (path1.equals(path2)) {
            return 0;
        } else if (path1.startsWith(path2)) {
            return -1;
        } else if (path2.startsWith(path1)) {
            return 1;
        } else {
            // Does not really matter
            return 0;
        }
    }

}
