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

import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import vellum.datatype.Millis;

/**
 *
 * @author evan
 */
public class MillisTest {

    @Test
    public void breakingBad() {
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date(0)));
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));
        System.out.println(Millis.formatAsSeconds(System.currentTimeMillis() % Millis.fromDays(1)));
    }
    
    @Test
    public void testIntervalSeconds() {
        Assert.assertEquals(Millis.formatAsSeconds(1000), "00:00:01");
        Assert.assertEquals(Millis.formatAsSeconds(60000), "00:01:00");
        Assert.assertEquals(Millis.formatAsSeconds(3600000), "01:00:00");
    }

    @Test
    public void testIntervalMillis() {
        Assert.assertEquals(Millis.format(1001), "00:00:01,001");
        Assert.assertEquals(Millis.format(60888), "00:01:00,888");
        Assert.assertEquals(Millis.format(3600999), "01:00:00,999");
    }
    
    @Test
    public void testParse() {
        Assert.assertEquals(Millis.parse("1 SECONDS"), 1000);
        Assert.assertEquals(Millis.parse("1m"), 60000);
        Assert.assertEquals(Millis.parse("60m"), 3600000);
        Assert.assertEquals(Millis.parse("60m"), Millis.parse("1h"));
        Assert.assertEquals(Millis.parse("24h"), Millis.parse("1d"));
    }
    
}
