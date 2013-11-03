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
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class KeyStores {
    static Logger logger = LoggerFactory.getLogger(KeyStores.class);
    
    public static X509Certificate findPrivateKeyCertificate(KeyStore keyStore, 
            String keyAlias) throws KeyStoreException {
        if (!keyStore.entryInstanceOf(keyAlias, KeyStore.PrivateKeyEntry.class)) {
            throw new KeyStoreException("Not private key entry: " + keyAlias);
        }
        return (X509Certificate) keyStore.getCertificate(keyAlias);
    }

    public static X509Certificate findPrivateKeyCertificate(KeyStore keyStore) 
            throws KeyStoreException {
        if (countKeys(keyStore) == 1) {
            for (String alias : Collections.list(keyStore.aliases())) {
                if (keyStore.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class)) {
                    return (X509Certificate) keyStore.getCertificate(alias);
                }
            }
        }
        throw new KeyStoreException("No sole private key found in keystore");
    }

    public static X509Certificate findSoleTrustedCertificate(KeyStore trustStore) 
            throws KeyStoreException {        
        if (Collections.list(trustStore.aliases()).size() == 1) {
            return (X509Certificate) trustStore.getCertificate(
                    trustStore.aliases().nextElement());
        }
        throw new KeyStoreException("No sole trusted certificate found in keystore");
    }

        public static int countCerts(KeyStore trustStore) throws KeyStoreException {
        int count = 0;
        for (String alias : Collections.list(trustStore.aliases())) {
            logger.debug("countCerts {}", alias);
            if (trustStore.entryInstanceOf(alias, KeyStore.TrustedCertificateEntry.class)) {
                count++;
            }
        }
        return count;
    }
    
    public static int countKeys(KeyStore keyStore) throws KeyStoreException {
        int count = 0;
        for (String alias : Collections.list(keyStore.aliases())) {
            logger.debug("countKeys {}", alias);
            if (keyStore.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class)) {
                count++;
            }
        }
        return count;
    }

    public static X509TrustManager findX509TrustManager(KeyStore trustStore)
            throws GeneralSecurityException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(trustStore);
        if (trustManagerFactory.getTrustManagers().length != 1) {
            throw new GeneralSecurityException("Multiple default trust managers");
        }
        if (trustManagerFactory.getTrustManagers()[0] instanceof X509TrustManager) {
            return (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
        }
        throw new GeneralSecurityException("Default X509TrustManager not found");
    }
}