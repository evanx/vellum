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

import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author evan.summers
 */
public class DualControlRevoke {

    private final static Logger logger = Logger.getLogger(DualControlRevoke.class);
    private ExtendedProperties props;
    private MockableConsole console;
    private String username;
    private String keyAlias;
    private String keyStoreLocation;
    private String keyStoreType;
    private char[] keyStorePassword;
    private KeyStore keyStore;
    List<String> aliasList;

    public DualControlRevoke(Properties properties, MockableConsole console) {
        this.props = new ExtendedProperties(properties);
        this.console = console;
        username = props.getString("dualcontrol.username");
        keyAlias = props.getString("alias");
        keyStoreType = props.getString("storetype");
    }

    public void init() {
    }

    public void clear() {
        Arrays.fill(keyStorePassword, (char) 0);
    }
    
    public void call() throws Exception {
        keyStoreLocation = props.getString("keystore");
        keyStorePassword = console.readPassword("Keystore password: ");
        keyStore = DualControlKeyStores.loadLocalKeyStore(keyStoreLocation, 
                keyStoreType, keyStorePassword);
        handle(keyStore);
        keyStore.store(new FileOutputStream(keyStoreLocation), keyStorePassword);
    }
    
    public void handle(KeyStore keyStore) throws KeyStoreException {
        aliasList = Collections.list(keyStore.aliases());
        for (String alias : aliasList) {
            logger.debug("alias " + alias);
            if (matches(alias)) {
                logger.info("delete " + alias);
                keyStore.deleteEntry(alias);
            }
        }
    }

    boolean matches(String alias) {
        if (alias.startsWith(keyAlias + "-" + username + "-")) {
            return true;
        }
        if (alias.startsWith(keyAlias + "-") && alias.endsWith("-" + username)) {
            return true;
        }
        return false;
    }
    
    public static void main(String[] args) throws Exception {
        logger.info("main " + Arrays.toString(args));
        DualControlRevoke instance = new DualControlRevoke(System.getProperties(),
                new SystemConsole());
        try {
            instance.init();
            instance.call();
        } catch (DualControlException e) {
            logger.error(e.getMessage());
        } finally {
            instance.clear();
        }
    }
    
}
