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
package vellum.crypto.asymmetricstore;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;

/**
 *
 * @author evan.summers
 */
public class RsaStore {

    private final int VERSION = 0xbeaa0001;
    private final String alg = "RSA";
    private String transform = "RSA/ECB/PKCS1Padding";

    public void store(OutputStream stream, String type, String alias,
            byte[] bytes, PublicKey publicKey)
            throws Exception {
        bytes = encrypt(bytes, publicKey);
        DataOutputStream dos = new DataOutputStream(stream);
        dos.writeInt(VERSION);
        dos.writeUTF(alg);
        dos.writeUTF(transform);
        dos.writeUTF(type);
        dos.writeUTF(alias);
        dos.writeInt(bytes.length);
        dos.write(bytes);
    }
    
    public byte[] load(InputStream stream, String type, String alias,
            PrivateKey privateKey) throws Exception {
        DataInputStream dis = new DataInputStream(stream);
        if (dis.readInt() != VERSION) {
            throw new Exception("Invalid version");
        }
        if (!dis.readUTF().equals(alg)) {
            throw new Exception("Invalid algorithm");    
        }
        transform = dis.readUTF();
        if (!dis.readUTF().equals(type)) {
            throw new Exception("Invalid type");
        }
        String string = dis.readUTF();
        if (!string.equals(alias)) {
            throw new Exception("Invalid alias " + string);
        }
        byte[] bytes = new byte[dis.readInt()];
        dis.readFully(bytes);
        bytes = decrypt(bytes, privateKey);
        return bytes;
    }

    private byte[] encrypt(byte[] bytes, PublicKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(transform);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(bytes);
    }

    private byte[] decrypt(byte[] bytes, PrivateKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(transform);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(bytes);
    }
}
