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

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

/**
 *
 * @author evan.summers
 */
public class DefaultKeyStores {
    static final String keyStoreLocation = 
            System.getProperty("javax.net.ssl.keyStore");
    static final char[] keyStorePassword = 
            System.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
    static final char[] keyPassword = 
            System.getProperty("javax.net.ssl.keyPassword").toCharArray();
    static final String trustStoreLocation = 
            System.getProperty("javax.net.ssl.trustStore");
    static final char[] trustStorePassword = 
            System.getProperty("javax.net.ssl.trustStorePassword").toCharArray();
    public static final KeyStore keyStore;
    public static final KeyStore trustStore;
    public static final KeyManagerFactory keyManagerFactory; 
    public static final TrustManagerFactory trustManagerFactory;

    static {
        try {
            keyStore = KeyStores.loadKeyStore("JKS", keyStoreLocation, keyStorePassword);
            trustStore = KeyStores.loadKeyStore("JKS", trustStoreLocation, trustStorePassword);
            keyManagerFactory = KeyStores.loadKeyManagerFactory(keyStore, keyStorePassword);
            trustManagerFactory = KeyStores.loadTrustManagerFactory(trustStore);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static PrivateKey getPrivateKey(String alias) throws Exception {
        return (PrivateKey) keyStore.getKey(alias, keyPassword);
    }

    public static X509Certificate getCert(String alias) throws Exception {
        return (X509Certificate) keyStore.getCertificate(alias);
    }

    public static X509TrustManager loadTrustManager() throws Exception {
        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        throw new RuntimeException();
    }

    public static SSLSocketFactory createSSLSocketFactory() throws Exception {
        return createSSLContext().getSocketFactory();
    }
    
    public static SSLContext createSSLContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), 
                trustManagerFactory.getTrustManagers(), new SecureRandom());
        return sslContext;
    }

    public static SSLContext createSSLContext(KeyManagerFactory keyManagerFactory, 
            TrustManagerFactory trustManagerFactory) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), 
                trustManagerFactory.getTrustManagers(), new SecureRandom());
        return sslContext;
    }
    
    public static SSLContext createSSLContext(TrustManager trustManager) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = new TrustManager[]{trustManager};
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, new SecureRandom());
        return sslContext;
    }

    
}
