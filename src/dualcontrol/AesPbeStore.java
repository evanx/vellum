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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;
import vellum.pbestore.PbeStore;
import vellum.util.Args;

/**
 * @see PbeStore
 * @author evan.summers
 */
public class AesPbeStore implements PbeStore {
    private final static Logger logger = Logger.getLogger(AesPbeStore.class);

    private final int VERSION = 0xaebe0001;
    private String pbeAlg = "PBKDF2WithHmacSHA1";
    private String keyAlg = "AES";
    private String cipherTransform = "AES/CBC/PKCS5Padding";    
    private int saltLength = 32;
    private int iterationCount = 999999;
    private int keySize = 256;
    private SecretKey pbeKey;
    byte[] salt;
    byte[] iv = null;
    
    public AesPbeStore() {
    }

    public AesPbeStore(int iterationCount) {
        this.iterationCount = iterationCount;
    }
    
    @Override
    public void store(OutputStream stream, String type, String alias, 
            byte[] bytes, char[] password) throws Exception { 
        salt = new byte[saltLength];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        pbeKey = generateKey(password);
        byte[] encryptedBytes = encrypt(bytes);
        byte[] encryptedSalt = encrypt(salt);
        DataOutputStream dos = new DataOutputStream(stream);
        dos.writeInt(VERSION);
        dos.writeUTF(pbeAlg);
        dos.writeUTF(keyAlg);
        dos.writeUTF(cipherTransform);
        dos.writeUTF(type);
        dos.writeUTF(alias);
        dos.writeShort(keySize);
        dos.writeInt(iterationCount);
        dos.write(salt.length);
        dos.write(iv.length);
        dos.write(encryptedSalt.length);
        dos.writeShort(encryptedBytes.length);
        dos.write(salt);
        dos.write(iv);
        dos.write(encryptedSalt);
        dos.write(encryptedBytes);
        dos.flush();
    }
    
    @Override
    public byte[] load(InputStream stream, String type, String alias, char[] password) 
        throws Exception {
        DataInputStream dis = new DataInputStream(stream);
        if (dis.readInt() != VERSION) {
            throw new Exception("Invalid version");
        }
        pbeAlg = dis.readUTF();
        keyAlg = dis.readUTF();
        cipherTransform = dis.readUTF();
        if (!dis.readUTF().equals(type)) {
            throw new Exception("Invalid store type");
        }
        if (!dis.readUTF().equals(alias)) {
            throw new Exception("Invalid alias");
        }
        keySize = dis.readShort();
        iterationCount = dis.readInt();
        salt = new byte[dis.read()];
        iv = new byte[dis.read()];
        byte[] encryptedSalt = new byte[dis.read()];
        byte[] encryptedBytes = new byte[dis.readShort()];
        dis.readFully(salt);
        dis.readFully(iv);
        dis.readFully(encryptedSalt);
        dis.readFully(encryptedBytes);
        pbeKey = generateKey(password);
        logger.debug(Args.format(salt.length, iterationCount, keySize));
        if (!Arrays.equals(salt, decrypt(encryptedSalt))) {
            throw new Exception("Invalid password");    
        }
        return decrypt(encryptedBytes);
    }

    private SecretKey generateKey(char[] password) throws GeneralSecurityException  {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterationCount, keySize);
        SecretKey key = SecretKeyFactory.getInstance(pbeAlg).generateSecret(spec);
        return new SecretKeySpec(key.getEncoded(), keyAlg);
    }
    
    private byte[] encrypt(byte[] bytes) throws GeneralSecurityException  {
        Cipher cipher = Cipher.getInstance(cipherTransform);
        if (iv != null) {
            cipher.init(Cipher.ENCRYPT_MODE, pbeKey, new IvParameterSpec(iv));
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, pbeKey);    
            AlgorithmParameters params = cipher.getParameters();
            iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        }
        return cipher.doFinal(bytes);
    }    
    
    private byte[] decrypt(byte[] bytes) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(cipherTransform);
        cipher.init(Cipher.DECRYPT_MODE, pbeKey, new IvParameterSpec(iv));
        return cipher.doFinal(bytes);
    }
}
