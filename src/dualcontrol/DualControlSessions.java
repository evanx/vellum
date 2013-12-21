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

/**
 *
 * @author evan.summers
 */
public class DualControlSessions {

    public static SecretKey loadKey(String keyStoreLocation, String keyStoreType, 
            char[] keyStorePass, String keyAlias, String purpose) throws Exception {
        KeyStore keyStore = DualControlKeyStores.loadKeyStore(keyStoreLocation, 
                keyStoreType, keyStorePass);
        purpose = "key " + keyAlias + " for " + purpose;
        Map.Entry<String, char[]> entry = DualControlManager.readDualEntry(purpose);
        String dualAlias = entry.getKey();
        char[] splitPassword = entry.getValue();
        keyAlias = keyAlias + "-" + dualAlias;
        SecretKey key = (SecretKey) keyStore.getKey(keyAlias, splitPassword);
        Arrays.fill(splitPassword, (char) 0);
        return key;
    }
    
}
