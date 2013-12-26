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

import org.junit.Assert;
import org.junit.Test;
import vellum.util.Base64;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan
 */
public class PBESaltTest {

    static Logr logger = LogrFactory.getLogger(PBESaltTest.class);
    private static final char[] PBE_PASSWORD = "ssh ssh".toCharArray();
    private static final int ITERATION_COUNT = 1024;
    private static final int KEY_SIZE = 128;
    private static final byte[] PBE_SALT = Base64.decode(
            "eKztAAV3KQAABAAAgBAQADe6I4N9V8gnNDPq7ouiWZUrpQ0UqeMuUyhEpjGSPSOw");

    @Test
    public void testGenerate() throws Exception {
        PasswordHash pbeSalt = new PasswordHash(PBE_PASSWORD, ITERATION_COUNT, KEY_SIZE);
        Assert.assertTrue(PasswordHash.verifyBytes(pbeSalt.getBytes()));
        pbeSalt = new PasswordHash(pbeSalt.getBytes());
        Assert.assertTrue(pbeSalt.matches(PBE_PASSWORD));
        Assert.assertEquals(ITERATION_COUNT, pbeSalt.getIterationCount());
        Assert.assertEquals(KEY_SIZE, pbeSalt.getKeySize());
        System.out.println("packed PBE salt et al: " + Base64.encode(pbeSalt.getBytes()));
        verify(PBE_PASSWORD, pbeSalt.getBytes());
    }

    @Test
    public void testVerify() throws Exception {
        verify(PBE_PASSWORD, PBE_SALT);
    }

    @Test(expected = AssertionError.class)
    public void testInvalidPasswordAssertion() throws Exception {
        verify("wrong password".toCharArray(), PBE_SALT);
    }

    public void verify(char[] pbePassword, byte[] pbeSaltBytes) throws Exception {
        PasswordHash pbeSalt = new PasswordHash(pbeSaltBytes);
        PBECipher cipher = new PBECipher(pbePassword, pbeSalt);
        Assert.assertTrue(pbeSalt.matches(pbePassword));
        pbeSalt.encryptSalt(cipher);
        pbeSalt.decryptSalt(cipher);
        Assert.assertTrue(pbeSalt.matches(pbePassword));
    }
}
