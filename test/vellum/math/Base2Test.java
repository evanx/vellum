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
package vellum.math;

import java.util.Arrays;
import org.junit.Test;
import vellum.util.Base2;
import static junit.framework.Assert.*;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan
 */
public class Base2Test {

    static Logr logger = LogrFactory.getLogger(Base2Test.class);

    @Test
    public void test() throws Exception {
        long[] pow2 = new long[63];
        for (int i = 0; i < pow2.length; i++) {
            pow2[i] = 1L<<i; 
            System.out.printf("%d, %d\n", i, pow2[i]);
        }
        assertEquals(4, Arrays.binarySearch(pow2, 16));
        assertEquals(1, Base2.pow(0));
        assertEquals(0, Base2.log(1));
        assertEquals(256, Base2.pow(8));
        assertEquals(8, Base2.log(256));
        assertEquals(256*256, Base2.pow(16));
        assertEquals(16, Base2.log(256*256));
        long value = 1;
        for (int i = 1; i < Base2.MAX_EXPONENT; i++) {
            value *= 2;
            assertEquals(value, Base2.pow(i));
            assertEquals(i, Base2.log(value));
        }
    }
}
