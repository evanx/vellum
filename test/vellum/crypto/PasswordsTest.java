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
import vellum.data.Millis;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.util.Bytes;

/**
 *
 * @author evan
 */
public class PasswordsTest {

    private static Logr logger = LogrFactory.getLogger(PasswordsTest.class);

    @Test
    public void testSaltEncoding() throws Exception {
        byte[] saltBytes = PasswordSalts.nextSalt();
        String encodedSalt = Base64.encode(saltBytes);
        System.out.println(Bytes.formatHex(saltBytes));
        System.out.println(encodedSalt);
        assertEquals(encodedSalt.length(), 24);
        assertEquals(encodedSalt.substring(22, 24), "==");
    }

    @Test
    public void printEncodedLength() throws Exception {
        System.out.printf("testEncodedLength 128bit: %d\n", Base64.encode(new byte[128 / 8]).length());
        System.out.printf("testEncodedLength 160bit: %d\n", Base64.encode(new byte[160 / 8]).length());
        System.out.printf("testEncodedLength 256bit: %d\n", Base64.encode(new byte[256 / 8]).length());
        System.out.printf("testEncodedLength 512bit: %d\n", Base64.encode(new byte[512 / 8]).length());
    }

    @Test
    public void test() throws Exception {
        char[] password = "12345678".toCharArray();
        byte[] salt = PasswordSalts.nextSalt();
        byte[] hash = Passwords.hashPassword(password, salt);
        assertTrue(Passwords.matches(password, hash, salt));
        byte[] otherSaltBytes = Arrays.copyOf(salt, salt.length);
        otherSaltBytes[0] ^= otherSaltBytes[0];
        assertFalse(Passwords.matches(password, hash, otherSaltBytes));
        assertFalse(Passwords.matches("wrong".toCharArray(), hash, salt));
    }

    @Test
    public void testEffort() throws Exception {
        char[] password = "12345678".toCharArray();
        long startMillis = System.currentTimeMillis();
        byte[] saltBytes = PasswordSalts.nextSalt();
        Passwords.hashPassword(password, saltBytes);
        System.out.println("time " + Millis.elapsed(startMillis));
        if (Millis.elapsed(startMillis) < 10) {
            System.out.println("Ooooooo.... i'm not sure");
        } else if (Millis.elapsed(startMillis) > 500) {
            System.out.println("Ooooooo.... i don't know");
        }
    }

    @Test
    public void testMatchesEffort() throws Exception {
        char[] password = "12345678".toCharArray();
        byte[] saltBytes = PasswordSalts.nextSalt();
        long startMillis = System.currentTimeMillis();
        byte[] hashBytes = Passwords.hashPassword(password, saltBytes, 30000, 160);
        System.out.printf("hash duration (30k, 160bit): %dms\n", Millis.elapsed(startMillis));
        startMillis = System.currentTimeMillis();
        assertTrue(Passwords.matches(password, hashBytes, saltBytes, 30000, 160));
        System.out.printf("matches duration (30k, 160bit): %dms\n", Millis.elapsed(startMillis));
        startMillis = System.currentTimeMillis();
        Passwords.hashPassword(password, saltBytes, 100000, 160);
        System.out.printf("100k hash duration: %dms\n", Millis.elapsed(startMillis));
        startMillis = System.currentTimeMillis();
        Passwords.hashPassword(password, saltBytes, 300000, 160);
        System.out.printf("300k hash duration: %dms\n", Millis.elapsed(startMillis));
        assertFalse(Passwords.matches(password, hashBytes, saltBytes, 30001, 160));
        assertFalse(Passwords.matches(password, hashBytes, saltBytes, 30000, 128));
        assertFalse(Passwords.matches("wrong".toCharArray(), 
                hashBytes, saltBytes, 30000, 160));
    }

}
