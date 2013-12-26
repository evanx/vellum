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
package vellum.crypto;

import vellum.util.Base64;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;
import static junit.framework.Assert.*;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan
 */
public class PasswordHashTest {

    private static Logr logger = LogrFactory.getLogger(PasswordHashTest.class);
    private int keySize = Passwords.KEY_SIZE;
    private int iterationCount = Passwords.ITERATION_COUNT;

    @Test
    public void testPasswordHash30k() throws Exception {
        testPasswordHash(30000, 160);
    }

    @Test
    public void testPasswordHash300k() throws Exception {
        testPasswordHash(300000, 160);
    }

    @Test
    public void testPasswordHash512b() throws Exception {
        testPasswordHash(1000, 512);
    }

    @Test
    public void testPasswordHashMinimumKeySize() throws Exception {
        testPasswordHash(30000, 128);
    }
    
    public void testPasswordHashKeySize() throws Exception {
        testPasswordHash(10000, 128);
        testPasswordHash(10000, 160);
        testPasswordHash(10000, 192);
        testPasswordHash(10000, 256);
        testPasswordHash(10000, 512);
        testPasswordHash(10000, 1024);
        testPasswordHash(10000, 1536);
    }

    //@Test(expected = IOException.class)
    public void testPasswordHash2048b() throws Exception {
        testPasswordHash(1000, 2048);
    }

    private void testPasswordHash(int iterationCount, int keySize) throws Exception {
        char[] password = "12345678".toCharArray();
        PasswordHash passwordHash = new PasswordHash(password, iterationCount, keySize);
        byte[] hashBytes = passwordHash.getBytes();
        String encodedString = Base64.encode(hashBytes);
        passwordHash = new PasswordHash(hashBytes);
        assertEquals("iterationCount", iterationCount, passwordHash.getIterationCount());
        assertEquals("keySize", keySize, passwordHash.getKeySize());
        assertTrue(PasswordHash.verifyBytes(hashBytes));
        assertFalse(passwordHash.matches("wrong".toCharArray()));
        assertTrue(passwordHash.matches(password));
        System.out.printf("iterationCount: %d\n", iterationCount);
        System.out.printf("keySize: %d\n", keySize);
        System.out.printf("byte array length: %d\n", hashBytes.length);
        System.out.printf("encoded string: %s\n", encodedString);
        System.out.printf("encoded length: %d\n", encodedString.length());
        System.out.printf("millis: %d\n", passwordHash.getMillis());
    }

    @Test
    public void testPasswordHashMore() throws Exception {
        char[] password = "12345678".toCharArray();
        PasswordHash passwordHash = new PasswordHash(password, iterationCount, keySize);
        assertTrue(new PasswordHash(passwordHash.getBytes()).matches(password));
        assertFalse(new PasswordHash(passwordHash.getBytes()).matches("wrong".toCharArray()));
        passwordHash = new PasswordHash(password, iterationCount * 2, 256);
        System.out.printf("millis %d\n", passwordHash.getMillis());
        assertTrue(new PasswordHash(passwordHash.getBytes()).matches(password));
        System.out.printf("millis %d\n", passwordHash.getMillis());
        assertFalse(new PasswordHash(passwordHash.getBytes()).matches("wrong".toCharArray()));
    }

    private void assertPassword(byte[] packedBytes, char[] password) throws Exception {
        PasswordHash passwordHash = new PasswordHash(packedBytes);
        assertTrue(passwordHash.matches(password));
        assertFalse(passwordHash.matches("wrong".toCharArray()));
    }

    @Test
    public void testPacked() throws Exception {
        char[] password = "12345678".toCharArray();
        byte[] hashBytes = PackedPasswords.hashPassword(password);
        String hashString = Base64.encode(hashBytes);
        System.out.printf("testPacked: %s\n", hashString);
        System.out.printf("testPacked: byte array length %d, encoded length %d\n", hashBytes.length, hashString.length());
        assertTrue(PackedPasswords.matches(password, hashBytes));
        assertFalse(PackedPasswords.matches("wrong".toCharArray(), hashBytes));
    }

    @Test
    public void testRevision() throws Exception {
        char[] password = "12345678".toCharArray();
        byte[] hash0 = PackedPasswords.hashPassword(password);
        byte[] hash1 = PackedPasswords.hashPassword(password, 2000, 96);
        System.out.println(Base64.encode(hash0));
        System.out.println(Base64.encode(hash1));
        assertFalse(Arrays.equals(hash0, hash1));
        assertTrue(PackedPasswords.matches(password, hash0));
        assertTrue(PackedPasswords.matches(password, hash1));
        assertTrue(matches("evanx", password, hash0));
        assertTrue(matches("evanx", password, hash1));
        assertFalse(PackedPasswords.matches("wrong".toCharArray(), hash0));
    }

    public boolean matchesUnsalted(char[] password, byte[] passwordHash) throws Exception {
        return PackedPasswords.matches(password, passwordHash);
    }

    @Test
    public void testProto() throws Exception {
        char[] password = "12345678".toCharArray();
        byte[] hash0 = PackedPasswords.hashPassword(password);
        byte[] hash1 = PackedPasswords.hashPassword(password, 2000, 96);
        assertTrue(matches("evanx", password, hash0));
        assertTrue(matches("evanx", password, hash1));
        assertTrue(PackedPasswords.matches(password, hash0));
        assertTrue(PackedPasswords.matches(password, hash1));
    }

    public boolean matches(String user, char[] password, byte[] packedBytes) throws Exception {
        if (PasswordHash.verifyBytes(packedBytes)) {
            PasswordHash passwordHash = new PasswordHash(packedBytes);
            if (passwordHash.matches(password)) {
                monitor(passwordHash.getMillis());
                if (passwordHash.getIterationCount() != Passwords.ITERATION_COUNT
                        || passwordHash.getKeySize() != Passwords.KEY_SIZE) {
                    passwordHash = new PasswordHash(password,
                            Passwords.ITERATION_COUNT, Passwords.KEY_SIZE);
                    persistRevisedPasswordHash(user, passwordHash.getBytes());
                }
                return true;
            }
            return false;
        } else {
            if (matchesUnsalted(password, packedBytes)) {
                packedBytes = PackedPasswords.hashPassword(password);
                persistRevisedPasswordHash(user, packedBytes);
                return true;
            }
        }
        return false;
    }

    private void persistRevisedPasswordHash(String user, byte[] passwordHash) {
        logger.info("persistNewPasswordHash", user, Base64.encode(passwordHash));
    }

    private void monitor(long millis) {
        if (millis > Passwords.HASH_MILLIS) {
            logger.warn("password hashing millis", millis);
        } else if (millis < Passwords.HASH_MILLIS / 10) {
            logger.warn("matches millis", millis);
        }
    }
}
