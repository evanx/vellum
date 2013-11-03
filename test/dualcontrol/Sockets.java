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
package dualcontrol;

import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class Sockets {
    static final private Logger logger = LoggerFactory.getLogger(Sockets.class);
    
    public static boolean portAvailable(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            socket.close();
            logger.debug("port not available: {}", port);
            return false;
        } catch (Exception e) {
            logger.debug("port available: {}", port);
            return true;
        }
    }

    public static boolean waitPort(String host, int port, long timeoutMillis, long sleepMillis)
            throws InterruptedException {
        long time = System.currentTimeMillis() + timeoutMillis;
        while (!portAvailable(host, port) && System.currentTimeMillis() < time) {
            sleep(sleepMillis);
        }
        return true;
    }
    
    private static void sleep(long millis) throws InterruptedException {
        logger.debug("sleep: {}", millis);
        Thread.sleep(millis);
    }
}
