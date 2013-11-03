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
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.security.Certificates;

/**
 *
 * @author evan.summers
 */
public class ExplicitTrustManager implements X509TrustManager {
    static Logger logger = LoggerFactory.getLogger(ExplicitTrustManager.class);

    X509TrustManager delegate;
    Map<String, X509Certificate> certificateMap = new HashMap();
    
    public ExplicitTrustManager(KeyStore trustStore) throws GeneralSecurityException {
        this.delegate = KeyStores.findX509TrustManager(trustStore);
        for (String alias : Collections.list(trustStore.aliases())) {
            certificateMap.put(alias, (X509Certificate) trustStore.getCertificate(alias));
        }
    }

    public ExplicitTrustManager(X509TrustManager delegate, 
            Map<String, X509Certificate> certificateMap) 
            throws GeneralSecurityException {
        this.delegate = delegate;
        this.certificateMap = certificateMap;
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
    
    private void checkTrusted(X509Certificate[] chain) 
            throws CertificateException {
        if (chain.length != 1) {
            throw new CertificateException("Invalid cert chain length");
        }
        X509Certificate trustedCertificate = certificateMap.get(
                Certificates.getCN(chain[0].getSubjectDN()));
        if (trustedCertificate == null) {
            throw new CertificateException("Untrusted peer certificate");
        }
        if (!Arrays.equals(chain[0].getPublicKey().getEncoded(),
                trustedCertificate.getPublicKey().getEncoded())) {
            throw new CertificateException("Invalid peer certificate");
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) 
            throws CertificateException {
        logger.debug("checkClientTrusted {} {}", chain[0].getSubjectDN().getName(), authType);
        checkTrusted(chain);
        delegate.checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) 
            throws CertificateException {
        logger.debug("checkServerTrusted {}", chain[0].getSubjectDN().getName());
        checkTrusted(chain);
        delegate.checkServerTrusted(chain, authType);
    }    
}