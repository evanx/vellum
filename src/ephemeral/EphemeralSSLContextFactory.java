/*
 * Source https://github.com/evanx by @evanxsummers

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
package ephemeral;

import vellum.util.ExtendedProperties;
import java.io.FileInputStream;
import vellum.ssl.OpenTrustManager;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import vellum.ssl.SSLContextFactory;
import vellumx.rsa.RsaKeyStores;
import vellum.ssl.SSLContexts;

/**
 *
 *
 * @author evan.summers
 */
public class EphemeralSSLContextFactory implements SSLContextFactory {

    public EphemeralSSLContextFactory() {
    }

    @Override
    public SSLContext create(ExtendedProperties properties) throws Exception {
        String keyStoreLocation = properties.getString("keyStore", null);
        if (keyStoreLocation == null) {
            return create(properties.getString("domain", "localhost"));
        } else if (properties.containsKey("confParentClass")) {
            return createResource(properties.getClass("confParentClass"), properties);
        } else {
            char[] pass = properties.getPassword("pass");
            String trustStoreLocation = properties.getString("trustStore", null);
            if (trustStoreLocation == null) {
                return SSLContexts.create(keyStoreLocation, pass);
            } else {
                return SSLContexts.create(keyStoreLocation, pass, trustStoreLocation);
            }
        }
    }

    public SSLContext createResource(Class parentClass, ExtendedProperties properties)
            throws Exception {
        String keyStoreLocation = properties.getString("keyStore", null);
        if (keyStoreLocation == null) {
            return create(properties.getString("domain", "localhost"));
        } else {
            char[] pass = properties.getPassword("pass");
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(parentClass.getResourceAsStream(keyStoreLocation), pass);
            String trustStoreLocation = properties.getString("trustStore", null);
            if (trustStoreLocation == null) {
                return SSLContexts.create(keyStore, pass);
            } else {
                KeyStore trustStore = KeyStore.getInstance("JKS");
                trustStore.load(parentClass.getResourceAsStream(trustStoreLocation), pass);
                return SSLContexts.create(keyStore, pass, trustStore);
            }
        }
    }
    
    public SSLContext create(String commonName) throws GeneralSecurityException, IOException {
        char[] keyPassword = new EphemeralPasswords().create();
        try {
            KeyStore keyStore = RsaKeyStores.createKeyStore("JKS",
                    commonName, keyPassword, 365);
            return SSLContexts.create(keyStore, keyPassword,
                    new OpenTrustManager());
        } finally {
            Arrays.fill(keyPassword, (char) 0);
        }
    }
}
