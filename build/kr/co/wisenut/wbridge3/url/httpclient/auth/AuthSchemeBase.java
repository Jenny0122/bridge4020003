/*
 * $Header: /home/cvs/Bridge/Standard/v3.8.1/WBridge/src/kr/co/wisenut/wbridge3/url/httpclient/auth/AuthSchemeBase.java,v 1.1.1.1 2009/03/10 07:02:41 wisenut Exp $
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

package kr.co.wisenut.wbridge3.url.httpclient.auth;

/**
 * <p>
 * Abstract authentication scheme class that implements {@link AuthScheme}
 * interface and provides a default contstructor.
 * </p>
 * @deprecated No longer used
 *
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 */
public abstract class AuthSchemeBase implements AuthScheme {

    /**
     * Original challenge string as received from the server.
     */
    private String challenge = null;

    /**
     * Constructor for an abstract authetication schemes.
     * 
     * @param challenge authentication challenge
     * 
     * @throws MalformedChallengeException is thrown if the authentication challenge
     * is malformed
     * 
     * @deprecated Use parameterless constructor and {@link AuthScheme#processChallenge(String)} 
     *             method
     */
    public AuthSchemeBase(final String challenge) 
      throws MalformedChallengeException {
        super();
        if (challenge == null) {
            throw new IllegalArgumentException("Challenge may not be null"); 
        }
        this.challenge = challenge;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof AuthSchemeBase) {
            return this.challenge.equals(((AuthSchemeBase) obj).challenge);
        } else {
            return super.equals(obj);
        }
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.challenge.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.challenge;
    }
}
