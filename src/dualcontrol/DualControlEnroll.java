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

import localca.SSLContexts;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLContext;
import org.apache.log4j.Logger;

/**
 *
 * @author evan.summers
 */
public class DualControlEnroll {

    final static Logger logger = Logger.getLogger(DualControlEnroll.class);
    private ExtendedProperties props;
    private MockableConsole console;
    private SSLContext sslContext;
    private int submissionCount;
    private String username;
    private String keyAlias;
    private String keyStoreLocation;
    private String keyStoreType;
    private char[] keyStorePassword;
    private Map<String, char[]> dualMap;
    private KeyStore keyStore;
    private SecretKey secretKey;
    List<String> aliasList;

    public DualControlEnroll(Properties properties, MockableConsole console) {
        this.props = new ExtendedProperties(properties);
        this.console = console;
        submissionCount = props.getInt("dualcontrol.submissions", 3);
        username = props.getString("dualcontrol.username");
        keyAlias = props.getString("alias");
        keyStoreLocation = props.getString("keystore");
        keyStoreType = props.getString("storetype");
        
    }
    
    public void init() throws Exception {
        sslContext = SSLContexts.create(true, "dualcontrol.ssl", props, console);
    }

    public void clear() {
    }
        
    public static void main(String[] args) throws Exception {
        logger.info("main " + Arrays.toString(args));
        DualControlEnroll instance = new DualControlEnroll(System.getProperties(),
                    new MockableConsoleAdapter(System.console()));
        try {
            instance.init();
            instance.call();
        } catch (DualControlException e) {
            logger.error(e.getMessage());
        } finally {
            instance.clear();
        }
    }

    public void call() throws Exception {
        keyStorePassword = console.readPassword("Keystore password: ");
        keyStore = DualControlKeyStores.loadLocalKeyStore(keyStoreLocation, 
                keyStoreType, keyStorePassword);
        handle(keyStore);
        keyStore.store(new FileOutputStream(keyStoreLocation), keyStorePassword);
    }
    
    public void handle(KeyStore keyStore) throws Exception {
        String purpose = String.format("key %s to enroll %s", keyAlias, username);
        DualControlManager manager = new DualControlManager(props, 
                submissionCount, purpose);
        manager.init(sslContext);
        manager.call();
        dualMap = manager.getDualMap();
        aliasList = Collections.list(keyStore.aliases());
        secretKey = getKey();
        KeyStore.Entry entry = new KeyStore.SecretKeyEntry(secretKey);
        for (String dualAlias : dualMap.keySet()) {
            char[] dualPassword = dualMap.get(dualAlias);
            String alias = keyAlias + "-" + dualAlias;
            if (!aliasList.contains(alias)) {
                KeyStore.ProtectionParameter prot = 
                        new KeyStore.PasswordProtection(dualPassword);
                keyStore.setEntry(alias, entry, prot);
            }
        }
    }
    
    SecretKey getKey() throws Exception {
        for (String alias : aliasList) {
            logger.debug("alias " + alias);
            if (alias.contains(username)) {
                throw new DualControlException("Copy already exists " + alias);
            }
        }
        for (String dualAlias : dualMap.keySet()) {
            char[] dualPassword = dualMap.get(dualAlias);
            String alias = keyAlias + "-" + dualAlias;
            logger.debug("try " + alias);
            if (aliasList.contains(alias)) {
                return (SecretKey) keyStore.getKey(alias, dualPassword);
            }
        }  
        throw new DualControlException("Key not found");
    }
}
