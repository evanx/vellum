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

import java.util.Arrays;
import javax.crypto.SecretKey;
import org.apache.log4j.Logger;

/**
 *
 * @author evan.summers
 */
public class DualControlDemoApp {
    private static final Logger logger = Logger.getLogger(DualControlDemoApp.class);
    private SecretKey dek; 
    
    public static void main(String[] args) throws Exception {
        logger.debug("main invoked with args: " + Arrays.toString(args));
        if (args.length == 3) {
            new DualControlDemoApp().loadKey(args[0], args[1], args[2].toCharArray());
        } else if (args.length == 2) {
            new DualControlDemoApp().loadKey(args[0], args[1]);
        } else {
            System.err.println("usage: keyStoreLocation alias storePass");
        }
    }
    
    public void loadKey(String keyStoreLocation, String alias, char[] keyStorePass) 
            throws Exception {
        dek = DualControlSessions.loadKey(keyStoreLocation, "JCEKS", keyStorePass, alias,
                "DualControlDemoApp");
        logger.debug("loaded key " + dek.getAlgorithm());
    }
    
    public void loadKey(String keyStoreLocation, String alias) throws Exception {
        char[] keyStorePass = System.console().readPassword(
                "Enter keystore password for %s: ", alias);
        dek = DualControlSessions.loadKey(keyStoreLocation, "JCEKS", keyStorePass, alias,
                "DualControlDemoApp");
        logger.debug("loaded key " + dek.getAlgorithm());
    }
    
}

