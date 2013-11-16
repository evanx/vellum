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

import dualcontrol.MockableConsole;
import dualcontrol.ExtendedProperties;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Properties;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class SSLContexts {

    static Logger logger = LoggerFactory.getLogger(SSLContexts.class);

    public static X509TrustManager createTrustManager(KeyStore trustStore)
            throws GeneralSecurityException {
        if (trustStore == null) {
            return new OpenTrustManager();
        } else {
            return new ExplicitTrustManager(trustStore);
        }
    }

    public static SSLContext create(String sslPrefix, Properties properties,
            MockableConsole console) throws Exception {
        ExtendedProperties props = new ExtendedProperties(properties);
        sslPrefix = props.getString(sslPrefix, sslPrefix);
        sslPrefix = props.getString(sslPrefix, sslPrefix);
        String keyStoreLocation = props.getString(sslPrefix + ".keyStore");
        if (keyStoreLocation == null) {
            throw new Exception("Missing keystore property for " + sslPrefix);
        }
        char[] pass = props.getPassword(sslPrefix + ".pass", null);
        if (pass == null) {
            pass = console.readPassword("Enter passphrase for %s: ", sslPrefix);
        }
        String trustStoreLocation = props.getString(sslPrefix + ".trustStore"); 
        if (keyStoreLocation.equals(trustStoreLocation)) {
            throw new KeyStoreException("Require separate truststore");
        }
        KeyStore keyStore = KeyStores.loadKeyStore("JKS", keyStoreLocation, pass);
        KeyStore trustStore = KeyStores.loadKeyStore("JKS", trustStoreLocation, pass);
        SSLContext sslContext = create(keyStore, pass, createTrustManager(trustStore));
        Arrays.fill(pass, (char) 0);
        return sslContext;
    }

    public static SSLContext create(String keyStoreLocation, char[] pass) 
            throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(keyStoreLocation), pass);
        return create(keyStore, pass, new OpenTrustManager());
    }

    public static SSLContext create(KeyStore keyStore, char[] pass) 
            throws GeneralSecurityException, IOException {
        return create(keyStore, pass, new OpenTrustManager());
    }
    
    public static SSLContext create(String keyStoreLocation, char[] pass,
            String trustStoreLocation) throws GeneralSecurityException, IOException {
        return create(keyStoreLocation, pass, pass, trustStoreLocation, pass);
    }

    public static SSLContext create(String keyStoreLocation,
            char[] keyStorePassword, char[] keyPass, String trustStoreLocation, 
            char[] trustStorePassword) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(keyStoreLocation), keyStorePassword);
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(new FileInputStream(trustStoreLocation), trustStorePassword); 
        return create(keyStore, keyPass, createTrustManager(trustStore));
    }

    public static SSLContext create(KeyStore keyStore, char[] keyPass,
            KeyStore trustStore) throws GeneralSecurityException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keyPass);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(trustStore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(), new SecureRandom());
        return sslContext;
    }

    public static SSLContext create(ExtendedProperties properties, 
            X509TrustManager trustManager) throws GeneralSecurityException, IOException {
        char[] pass = properties.getPassword("pass");
        KeyStore keyStore = KeyStores.loadKeyStore(properties.getString("keyStoreType", "JKS"),
                properties.getString("keyStoreLocation"), pass);
        return create(keyStore, pass, trustManager);
    }
    
    public static SSLContext create(KeyStore keyStore, char[] keyPass,
            X509TrustManager trustManager) throws GeneralSecurityException {
        return create(keyStore, keyPass, new X509TrustManager[] {trustManager});
    }
    
    public static SSLContext create(KeyStore keyStore, char[] keyPass,
            X509TrustManager[] trustManagers) throws GeneralSecurityException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keyPass);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, 
                new SecureRandom());
        return sslContext;
    }
}
