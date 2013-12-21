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
package dualcontrol;

import vellum.pbestore.AesPbeStore;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import javax.crypto.SecretKey;

/**
 *
 * @see AesPbeStore 
 * 
 * @author evan.summers
 */
public class RecryptedKeyStore {
    int iterationCount = 500000;

    public RecryptedKeyStore() {
    }
    
    public RecryptedKeyStore(int iterationCount) {
        this.iterationCount = iterationCount;
    }

    public void storeKey(SecretKey secretKey, 
            String keyStoreLocation, String keyStoreType, 
            String alias, char[] password) throws Exception {
        File file = new File(keyStoreLocation);
        if (file.exists()) {
            throw new Exception("Encrypted keystore file already exists: " + keyStoreLocation);
        }
        storeKeyForce(secretKey, keyStoreLocation, keyStoreType, alias, password);
    }
    
    public void storeKeyForce(SecretKey secretKey, String keyStoreLocation,
            String keyStoreType, String alias, char[] password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, password);
        KeyStore.Entry entry = new KeyStore.SecretKeyEntry(secretKey);
        KeyStore.ProtectionParameter prot = new KeyStore.PasswordProtection(password);
        keyStore.setEntry(alias, entry, prot);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        keyStore.store(baos, password);
        new AesPbeStore(iterationCount).
                store(new FileOutputStream(keyStoreLocation), keyStoreType, 
                alias, baos.toByteArray(), password);
    }
    
    public static SecretKey loadKey(String keyStoreLocation, String keyStoreType, 
            String alias, char[] password) throws Exception {
        File file = new File(keyStoreLocation);
        if (!file.exists()) {
            throw new Exception("Encrypted keystore file not found: " + keyStoreLocation);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(
                new AesPbeStore().load(new FileInputStream(file), keyStoreType, 
                alias, password));
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(bais, password);
        return (SecretKey) keyStore.getKey(alias, password);
    }
}
