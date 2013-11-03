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
package dualcontrol;

import vellum.security.KeyGenerators;
import vellum.crypto.aes.AESCiphers;
import javax.crypto.SecretKey;
import org.junit.Test;
import vellum.crypto.api.Encrypted;
import vellum.crypto.api.VellumCipher;
import vellum.datatype.Millis;
import vellum.logging.FormatLogger;
import vellum.util.Bytes;

/**
 *
 * @author evan.summers
 */
public class RecryptedKeyStoreTest {
    private final static FormatLogger logger = FormatLogger.getLogger(RecryptedKeyStoreTest.class);
    private final String keyAlg = "AES";
    private final int keySize = 256;
    private final String keyStoreType = "JCEKS";
    private String keyStoreLocation = "/tmp/test.jceks";
    private String keyAlias = "dek2013";
    private char[] keyPass = "test1234".toCharArray();

    public RecryptedKeyStoreTest() {
    }
        
    @Test
    public void test() throws Exception {
        test(999999);
    }
    
    private void test(int iterationCount) throws Exception {
        String data = "4000555500001111";
        long millis = System.currentTimeMillis();
        SecretKey dek = KeyGenerators.generateKey(keyAlg, keySize);
        logger.info("generate %dms", Millis.elapsed(millis));
        VellumCipher cipher = AESCiphers.getCipher(dek);
        millis = System.currentTimeMillis();
        Encrypted encrypted = cipher.encrypt(data.getBytes());
        logger.info("encrypt %dms", Millis.elapsed(millis));
        millis = System.currentTimeMillis();
        logger.info("decrypted %s", Bytes.toString(cipher.decrypt(encrypted)));
        logger.info("decrypt %dms", Millis.elapsed(millis));
        millis = System.currentTimeMillis();
        new RecryptedKeyStore(iterationCount).storeKeyForce(dek, keyStoreLocation, 
                keyStoreType, keyAlias, keyPass);
        logger.info("store %dms", Millis.elapsed(millis));
        millis = System.currentTimeMillis();
        dek = RecryptedKeyStore.loadKey(keyStoreLocation, 
                keyStoreType, keyAlias, keyPass);
        logger.info("load %dms", Millis.elapsed(millis));
        logger.info("keyAlias %s, alg %s", keyAlias, dek.getAlgorithm());
        millis = System.currentTimeMillis();
        encrypted = cipher.encrypt(data.getBytes());
        logger.info("encrypt %dms", Millis.elapsed(millis));
        logger.info(Bytes.toString(cipher.decrypt(encrypted)));
    }
}
