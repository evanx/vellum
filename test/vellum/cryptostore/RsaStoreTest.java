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
package vellum.cryptostore;

import static junit.framework.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.junit.Test;
import vellum.pbestore.RsaKeyStore;
import vellum.crypto.asymmetricstore.RsaStore;
import vellum.data.Millis;

/**
 *
 * @author evan
 */
public class RsaStoreTest {

    static Logger logger = Logger.getLogger(RsaStoreTest.class);
    
    char[] password = "test1234".toCharArray();
    String type = "adhoc";
    String alias = "test2013";
    int keySize = 2048;
    String text = "all your base all belong to us";
    
    @Test 
    public void testGenerate() throws Exception {
        testGenerate(1000);
        testGenerate(10000);
        testGenerate(100000);
    }
    
    public void testGenerate(int iterationCount) throws Exception {
        long millis = System.currentTimeMillis();
        RsaKeyStore ks = new RsaKeyStore();
        ks.generate(alias, keySize);
        ByteArrayOutputStream kos = new ByteArrayOutputStream();
        ks.storePublic(kos);
        ByteArrayInputStream kis = new ByteArrayInputStream(kos.toByteArray());
        PublicKey loadedPublicKey = ks.loadPublic(kis);
        System.out.printf("loaded public key %s %s: %s\n", alias, 
                loadedPublicKey.getAlgorithm(), 
                Base64.encodeBase64String(loadedPublicKey.getEncoded()));
        assertTrue("loaded public key", Arrays.equals(ks.getKeyPair().getPublic().getEncoded(),
                loadedPublicKey.getEncoded()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new RsaStore().store(baos, type, alias, text.getBytes(), ks.getKeyPair().getPublic());
        millis = Millis.elapsed(millis);
        System.out.printf("store %s %d %dms: %s\n", alias, iterationCount, millis, text);
        millis = System.currentTimeMillis();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        kos = new ByteArrayOutputStream();
        ks.storePrivate(kos, password);
        kis = new ByteArrayInputStream(kos.toByteArray());
        PrivateKey loadedPrivateKey = ks.loadPrivate(kis, alias, password);
        assertTrue("loaded private key", Arrays.equals(ks.getKeyPair().getPrivate().getEncoded(),
                loadedPrivateKey.getEncoded()));
        millis = Millis.elapsed(millis);
        System.out.printf("loaded private key %s %d %dms: %s\n", alias, iterationCount, 
                millis, loadedPrivateKey.getAlgorithm());
        millis = System.currentTimeMillis();
        byte[] loadBytes = new RsaStore().load(bais, type, alias, loadedPrivateKey);
        millis = Millis.elapsed(millis);
        System.out.printf("load %s %d %dms: %s\n", alias, iterationCount, millis, 
                new String(loadBytes));
        assertTrue("loaded bytes", Arrays.equals(loadBytes, text.getBytes()));
    }
}
