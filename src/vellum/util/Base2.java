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
package vellum.util;

import java.util.Arrays;

/**
 *
 * @author evan.summers
 */
public class Base2 {

    public static final int MAX_EXPONENT = 62;
    private static final long[] pow = new long[MAX_EXPONENT + 1];
    
    static {
        for (int i = 0; i < pow.length; i++) {
            pow[i] = pow(i); 
        }
    }

    public static long pow(int exponent) {
        return 1L<<exponent;
    }
    
    public static int log(long operand) {
        return Arrays.binarySearch(pow, operand);
    }
}
