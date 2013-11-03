/*
 Source https://code.google.com/p/vellum by @evanxsummers

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
import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLContext;
import org.apache.log4j.Logger;

/**
 *
 * @author evan.summers
 */
public class DualControlGenSecKey {

    final static Logger logger = Logger.getLogger(DualControlGenSecKey.class);
    private int submissionCount;
    private String keyAlias;
    private String keyStoreLocation;
    private String keyStoreType;
    private String keyAlg;
    private int keySize;
    private char[] keyStorePassword;
    private ExtendedProperties props;
    private MockableConsole console;
    private SSLContext sslContext;

    public DualControlGenSecKey(Properties properties, MockableConsole console) {
        this.props = new ExtendedProperties(properties);
        this.console = console;
        submissionCount = props.getInt("dualcontrol.submissions", 3);
        keyAlias = props.getString("alias");
    }

    public void init(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public void init() throws Exception {
        sslContext = SSLContexts.create(true, "dualcontrol.ssl",
                props, console);
    }

    public static void main(String[] args) throws Exception {
        logger.info("main " + Arrays.toString(args));
        DualControlGenSecKey instance = new DualControlGenSecKey(
                new ExtendedProperties(System.getProperties()), 
                new MockableConsoleAdapter(System.console()));
        try {
            instance.init();
            instance.call();
        } catch (DualControlException e) {
            instance.console.println(e.getMessage());
        } finally {
            instance.clear();
        }
    }
        
    public void call() throws Exception {
        keyStoreLocation = props.getString("keystore");
        if (new File(keyStoreLocation).exists()) {
            throw new Exception("Keystore file already exists: " + keyStoreLocation);
        }
        keyStorePassword = props.getPassword("storepass", null);
        if (keyStorePassword == null) {
            keyStorePassword = console.readPassword(
                    "Enter passphrase for keystore for new key %s: ", keyAlias);
            if (keyStorePassword == null) {
                throw new Exception("No keystore passphrase from console");
            }
        }
        KeyStore keyStore = createKeyStore();
        keyStore.store(new FileOutputStream(keyStoreLocation), keyStorePassword);
    }

    public KeyStore createKeyStore() throws Exception {
        String purpose = "new key " + keyAlias;
        DualControlManager manager = new DualControlManager(props, 
                submissionCount, purpose);
        manager.init(sslContext);
        manager.call();
        return buildKeyStore(manager.getDualMap());
    }

    public KeyStore buildKeyStore(Map<String, char[]> dualMap) throws Exception {
        keyAlias = props.getString("alias");
        keyStoreType = props.getString("storetype");
        keyAlg = props.getString("keyalg");
        keySize = props.getInt("keysize");
        KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlg);
        keyGenerator.init(keySize);
        SecretKey secretKey = keyGenerator.generateKey();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        setEntries(keyStore, secretKey, keyAlias, dualMap);
        return keyStore;
    }

    private static void setEntries(KeyStore keyStore, SecretKey secretKey,
            String keyAlias, Map<String, char[]> dualMap) throws Exception {
        KeyStore.Entry entry = new KeyStore.SecretKeyEntry(secretKey);
        for (String dualAlias : dualMap.keySet()) {
            char[] dualPassword = dualMap.get(dualAlias);
            String alias = keyAlias + "-" + dualAlias;
            logger.info("alias: " + alias);
            KeyStore.PasswordProtection passwordProtection =
                    new KeyStore.PasswordProtection(dualPassword);
            keyStore.setEntry(alias, entry, passwordProtection);
            passwordProtection.destroy();
            Arrays.fill(dualPassword, (char) 0);
        }
    }

    private void clear() {
        if (keyStorePassword != null) {
            Arrays.fill(keyStorePassword, (char) 0);
        }
    }
}
