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
package vellum.security;

import java.io.FileInputStream;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.*;
import vellum.exception.Exceptions;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 *
 * @author evan.summers
 */
public class KeyStores {

    static Logr logger = LogrFactory.getLogger(KeyStores.class);

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
    
    public static TrustManagerFactory loadTrustManagerFactory(KeyStore trustStore) {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(trustStore);
            return tmf;
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static KeyManagerFactory loadKeyManagerFactory(KeyStore keyStore, char[] keyPassword) {
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, keyPassword);
            return kmf;
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static KeyStore loadKeyStore(String type, String filePath, char[] keyStorePassword) {
        try {
            KeyStore keyStore = KeyStore.getInstance(type);
            FileInputStream inputStream = new FileInputStream(filePath);
            keyStore.load(inputStream, keyStorePassword);
            return keyStore;
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
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
    
}
