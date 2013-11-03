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

import java.net.InetAddress;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author evan.summers
 */
public class Systems {
    public static Logr logger = LogrFactory.getLogger(Systems.class);

    public static final String osName = System.getProperty("os.name");
    public static final String userDir = System.getProperty("user.dir");
    public static final String homeDir = System.getProperty("user.home");

    public static boolean isLinux() {
        return osName.toLowerCase().startsWith("linux");
    }
    
    public static void sleep(long duration, TimeUnit timeUnit) {
        sleep(timeUnit.toMillis(duration));
    }
    
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            logger.warn(e, null);
        }
    }
   
    public static String getHostName() {
        String hostName = System.getProperty("hostName");
        if (hostName == null) {
            try {
                hostName = InetAddress.getLocalHost().getHostName();
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        int index = hostName.indexOf(".");
        if (index > 0) {
            hostName = hostName.substring(0, index);
        }
        return hostName;
    }

    public static String getPath(String fileName) {
        if (fileName == null) {
            throw new RuntimeException(fileName);
        }
        if (Character.isLetter(fileName.charAt(0))) {
            return homeDir + "/" + fileName;
        }
        return fileName;
    }
    
}
