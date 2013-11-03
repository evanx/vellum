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
package vellum.crypto;

import vellum.crypto.api.Encrypted;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import vellum.datatype.Millis;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class PasswordHash {
    private static Logr logger = LogrFactory.getLogger(PasswordHash.class);
    private static final int VERSION_OBJECT_STREAM = 120;
    private static final int VERSION = VERSION_OBJECT_STREAM;
    int iterationCount;
    int keySize;
    byte[] hash;
    byte[] salt;
    byte[] iv;
    long millis;
    
    public PasswordHash(char[] password, int iterationCount, int keySize) 
            throws GeneralSecurityException {
        this.iterationCount = iterationCount;
        this.keySize = keySize;
        this.salt = PasswordSalts.nextSalt();
        this.hash = Passwords.hashPassword(password, salt, iterationCount, keySize);
        this.iv = new byte[0];
    }

    public PasswordHash(byte[] bytes) throws IOException {
        InputStream stream = new ByteArrayInputStream(bytes);
        readObject(stream);
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        writeObject(stream);
        return stream.toByteArray();
    }
    
    private void writeObject(OutputStream stream) throws IOException {
        if (hash.length > 255) {
            throw new IOException("Hash length not supported");
        }
        if (VERSION == VERSION_OBJECT_STREAM) {
            stream.write(VERSION);
            writeObject(new ObjectOutputStream(stream));
        } else {
            MinimalPasswordHashSerializer.writeObject(this, stream);
        }
    }

    private void readObject(InputStream stream) throws IOException {
        int version = stream.read();
        if (version == VERSION_OBJECT_STREAM) {
            readObject(new ObjectInputStream(stream));
        } else {    
            MinimalPasswordHashSerializer.readObject(this, stream, version);
        }
    }
    
    public byte[] getHash() {
        return hash;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getIv() {
        return iv;
    }

    public int getIterationCount() {
        return iterationCount;
    }

    public int getKeySize() {
        return keySize;
    }

    public boolean isEncrypted() {
        return iv.length > 0;
    }
        
    public void encryptSalt(PBECipher cipher) throws GeneralSecurityException {
        assert iv.length == 0;
        Encrypted encryptedSalt = cipher.encrypt(salt);
        salt = encryptedSalt.getEncryptedBytes();
        iv = encryptedSalt.getIv();
    }

    public void decryptSalt(PBECipher cipher) throws GeneralSecurityException {
        assert iv.length > 0;
        salt = cipher.decrypt(salt, iv);
        iv = new byte[0];
    }

    public boolean matches(char[] password) throws GeneralSecurityException {
        assert iv.length == 0;
        millis = System.currentTimeMillis();
        try {
            return Arrays.equals(hash, 
                    Passwords.hashPassword(password, salt, iterationCount, keySize));
        } finally {
            millis = Millis.elapsed(millis);
        }
    }

    public long getMillis() {
        return millis;
    }
                    
    public static boolean verifyBytes(byte[] bytes) {
        logger.info("verifyBytes", bytes.length, (int) bytes[0]);
        return bytes.length >= 48 && bytes[0] == VERSION;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeInt(iterationCount);
        stream.writeShort(keySize);
        stream.write(hash.length);
        stream.write(salt.length);
        stream.write(iv.length);
        stream.write(hash);
        stream.write(salt);
        stream.write(iv);
        stream.flush();
    }

    private void readObject(ObjectInputStream stream) throws IOException {
        iterationCount = stream.readInt();
        keySize = stream.readShort();
        hash = new byte[stream.read()];
        salt = new byte[stream.read()];
        iv = new byte[stream.read()];
        stream.read(hash);
        stream.read(salt);
        stream.read(iv);
    }
}
