/*
 * Source https://code.google.com/p/vellum by @evanxsummers

       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements. See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.  
 */
package localca;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class DelegatingClientTrustManager implements X509TrustManager {
    static Logger logger = LoggerFactory.getLogger(DelegatingClientTrustManager.class);
    TrustManagerDelegate delegate;
    
    public DelegatingClientTrustManager(TrustManagerDelegate delegate) throws GeneralSecurityException {
        this.delegate = delegate;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
    
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) 
            throws CertificateException {
        if (chain.length == 0) {
            delegate.check();
        } else {
            delegate.check(chain[0]);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) 
            throws CertificateException {
        throw new CertificateException("Server authentication not supported");        
    }    
}