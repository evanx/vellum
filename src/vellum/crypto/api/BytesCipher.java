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
package vellum.crypto.api;

import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author evan.summers
 */
public class BytesCipher implements VellumCipher {
    private String cipherTransform;    
    private Key key;
    
    public BytesCipher(Key key, String cipherTransform) {
        this.key = key;
        this.cipherTransform = cipherTransform;
    }
    
    @Override
    public Encrypted encrypt(byte[] bytes) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(cipherTransform);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        return new Encrypted(iv, cipher.doFinal(bytes));
    }

    @Override
    public byte[] encrypt(byte[] bytes, byte[] iv) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(cipherTransform);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(bytes);
    }

    @Override
    public byte[] decrypt(Encrypted encrypted) throws GeneralSecurityException {
        return decrypt(encrypted.getEncryptedBytes(), encrypted.getIv());
    }

    @Override
    public byte[] decrypt(byte[] bytes, byte[] iv) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(cipherTransform);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(bytes);
    }
}
