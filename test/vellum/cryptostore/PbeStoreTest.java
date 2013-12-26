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
import vellum.pbestore.AesPbeStore;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import org.junit.Test;
import vellum.data.Millis;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan
 */
public class PbeStoreTest {

    static Logr logger = LogrFactory.getLogger(PbeStoreTest.class);

    char[] password = "test1234".toCharArray();
    String alias = "dek2013";
    String type = "JCEKS";
    String text = "all your base all belong to us";
    
    @Test
    public void testGenerate() throws Exception {
        testGenerate(1000);
        testGenerate(10000);
        testGenerate(100000);
        testGenerate(500000);
        testGenerate(1000000);
    }
    
    public void testGenerate(int iterationCount) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long millis = System.currentTimeMillis();
        new AesPbeStore(iterationCount).store(baos, type, alias, text.getBytes(), password);
        millis = Millis.elapsed(millis);
        System.out.printf("store %s %d %dms: %s\n", alias, iterationCount, millis, text);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        millis = System.currentTimeMillis();
        byte[] loadBytes = new AesPbeStore().load(bais, type, alias, password);
        millis = Millis.elapsed(millis);
        System.out.printf("load %s %d %dms: %s\n", alias, iterationCount, millis, 
                new String(loadBytes));
        assertTrue(Arrays.equals(loadBytes, text.getBytes()));
    }
}
