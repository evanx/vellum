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
package vellum.crypto.aes;

import java.security.GeneralSecurityException;
import java.security.Key;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import vellum.crypto.api.BytesCipher;

/**
 *
 * @author evan.summers
 */
public class AESCiphers {
    private static String keyAlg = "AES";
    private static final String cipherTransform = "AES/CBC/PKCS5Padding";    
    
    public static SecretKey generateKey(int keySize) 
            throws GeneralSecurityException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlg);
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }
    
    public static BytesCipher getCipher(Key key) {
        return new BytesCipher(key, cipherTransform);
    }
}
