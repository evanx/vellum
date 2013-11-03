/*
 Source https://code.google.com/p/vellum by @evanxsummers

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
package vellum.pbestore;

import dualcontrol.AesPbeStore;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.log4j.Logger;
import vellum.pbestore.PbeStore;

/**
 *
 * @see PbeStore
 * 
 * @author evan.summers
 */
public class RsaKeyStore {

    private final static Logger logger = Logger.getLogger(RsaKeyStore.class);
    private static final String alg = "RSA";
    private String alias;
    private KeyPair keyPair;

    public void generate(String alias, int keySize) throws NoSuchAlgorithmException {
        this.alias = alias;
        KeyPairGenerator generator = KeyPairGenerator.getInstance(alg);
        generator.initialize(keySize);
        this.keyPair = generator.generateKeyPair();
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
    
    public void store(String location, char[] password) throws Exception {
        storePublic(location);
        storePrivate(location, password);
    }
    
    public void storePublic(String location) throws IOException {
        location = location + "." + alias + ".pub";
        OutputStream stream = new FileOutputStream(location);
        storePublic(stream);
        stream.close();
    }
    
    public void storePublic(OutputStream stream) throws IOException {
        DataOutputStream dos = new DataOutputStream(stream);
        byte[] bytes = keyPair.getPublic().getEncoded();
        dos.writeShort(bytes.length);
        dos.write(bytes);
        dos.flush();
    }

    public void storePrivate(String location, char[] password) throws Exception {
        location = location + "." + alias + ".key";
        OutputStream stream = new FileOutputStream(location);
        storePrivate(stream, password);
        stream.close();
    }
    
    public void storePrivate(OutputStream stream, char[] password) throws Exception {
        byte[] keyBytes = keyPair.getPrivate().getEncoded();
        PbeStore pbeStore = new AesPbeStore();
        pbeStore.store(stream, alg, alias, keyBytes, password);
    }
    
    public PublicKey loadPublic(InputStream stream) throws Exception {
        DataInputStream dis = new DataInputStream(stream);
        byte[] bytes = new byte[dis.readShort()];
        dis.readFully(bytes);
        return KeyFactory.getInstance(alg).generatePublic(new X509EncodedKeySpec(bytes));
    }
    
    public PrivateKey loadPrivate(InputStream stream, String alias, char[] password) 
            throws Exception {
        PbeStore pbeStore = new AesPbeStore();
        byte[] keyBytes = pbeStore.load(stream, alg, alias, password);
        return KeyFactory.getInstance(alg).generatePrivate(
                new PKCS8EncodedKeySpec(keyBytes));
    }
}
