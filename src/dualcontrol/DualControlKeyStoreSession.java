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
package dualcontrol;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.Map;
import javax.crypto.SecretKey;
import org.apache.log4j.Logger;

/**
 *
 * @author evan.summers
 */
public class DualControlKeyStoreSession {
    private final static Logger logger = Logger.getLogger(DualControlKeyStoreSession.class);

    private KeyStore dualKeyStore;
    private char[] dualPass;
    private String dualAlias;

    public void configure(String keyStoreLocation, char[] storePass, String prompt) 
            throws Exception {
        logger.debug("keyStore " + keyStoreLocation);
        this.dualKeyStore = DualControlKeyStores.loadKeyStore(keyStoreLocation, 
                "JCEKS", storePass);
        Map.Entry<String, char[]> entry = DualControlManager.readDualEntry(prompt);
        this.dualAlias = entry.getKey();
        this.dualPass = entry.getValue();
        logger.debug("alias " + dualAlias);
    }
    
    public void clear() {
        Arrays.fill(dualPass, (char) 0);
    }

    public SecretKey loadKey(String alias) throws Exception {
        alias += "-" + dualAlias;
        logger.debug("loadKey " + alias);
        return (SecretKey) dualKeyStore.getKey(alias, dualPass);
    }
}
