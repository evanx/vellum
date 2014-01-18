/*
 Source https://code.google.com/p/vellum by @evanxsummers

       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements. See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership. The ASF licenses this file to
       you under the Apache License, Version 2.0 (the "License").
       You may not use this file except in compliance with the
       License. You may obtain a copy of the License at:

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.  
 */
package vellumx.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellumcert.GenKeyPair;

/**
 *
 *
 * @author evan.summers
 */
public class KeyStores {

    static Logger logger = LoggerFactory.getLogger(KeyStores.class);

    public static KeyStore loadKeyStore(String type, String filePath, char[] keyStorePassword) 
        throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(type);
        FileInputStream inputStream = new FileInputStream(filePath);
        keyStore.load(inputStream, keyStorePassword);
        return keyStore;
    }
    
    public static X509TrustManager loadTrustManager(TrustManagerFactory trustManagerFactory) 
            throws Exception {
        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        throw new RuntimeException();
    }
    
    public static SSLSocketFactory createSSLSocketFactory(String keyStoreLocation, 
            String keyStoreType, char[] keyStorePassword, char[] keyPassword,
            String trustStoreLocation, String trustStoreType, 
            char[] trustStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(new FileInputStream(keyStoreLocation), keyStorePassword);
        KeyStore trustStore = loadKeyStore(trustStoreType, trustStoreLocation, trustStorePassword);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keyPassword);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(trustStore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), 
                trustManagerFactory.getTrustManagers(), new SecureRandom());
        return sslContext.getSocketFactory();
    }
    
    public static SSLContext createSSLContext(KeyManagerFactory keyManagerFactory, 
            TrustManagerFactory trustManagerFactory) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), 
                trustManagerFactory.getTrustManagers(), new SecureRandom());
        return sslContext;
    }
    
    public static TrustManagerFactory loadTrustManagerFactory(KeyStore trustStore)
            throws GeneralSecurityException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(trustStore);
        return tmf;
    }

    public static KeyManagerFactory loadKeyManagerFactory(KeyStore keyStore, char[] keyPassword)
            throws GeneralSecurityException {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keyStore, keyPassword);
        return kmf;
    }
    
    public static KeyStore createEmptyKeyStore(String type) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(type);
        keyStore.load(null, null);
        return keyStore;
    }

    public static Map<String, X509Certificate> mapTrustStore(KeyStore trustStore) 
            throws KeyStoreException {
        Map<String, X509Certificate> clientCertificateMap = new HashMap();
        for (String alias : Collections.list(trustStore.aliases())) {
            clientCertificateMap.put(alias, (X509Certificate) 
                    trustStore.getCertificate(alias));
        }
        return clientCertificateMap;
    }    
    
    public static KeyStore createKeyStore(String type, String commonName, char[] keyPassword,
            int validityDays, GenKeyPair keyPair) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(type);
        keyStore.load(null, null);
        keyPair.generate("CN=" + commonName, new Date(), validityDays, TimeUnit.DAYS);
        X509Certificate[] chain = new X509Certificate[]{keyPair.getCertificate()};
        keyStore.setKeyEntry(commonName, keyPair.getPrivateKey(), keyPassword, chain);
        return keyStore;
    }

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
