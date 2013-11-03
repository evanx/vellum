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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * @author evan.summers
 */
public class Password {
    byte[] password;
    byte[] salt;
    int revisionIndex;

    public Password(byte[] packedBytes) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(packedBytes);
        if (packedBytes.length != stream.read()) {
            throw new IOException();
        }
        password = new byte[stream.read()];
        salt = new byte[stream.read()];
        revisionIndex = stream.read();
        if (packedBytes.length != salt.length + password.length + 4) {
            throw new IOException();
        }
        stream.read(password);
        stream.read(salt);
    }
    
    public Password(byte[] password, byte[] salt, int revisionIndex) {
        this.password = password;
        this.salt = salt;
        this.revisionIndex = revisionIndex;
    }

    public byte[] getPassword() {
        return password;
    }
    
    public byte[] getSalt() {
        return salt;
    }

    public int getRevisionIndex() {
        return revisionIndex;
    }
    
    public byte[] pack() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(salt.length + password.length + 4);
            stream.write(password.length);
            stream.write(salt.length);
            stream.write(revisionIndex);
            stream.write(password);
            stream.write(salt);
            return stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }   
}
