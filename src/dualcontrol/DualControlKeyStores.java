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
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import org.apache.log4j.Logger;

/**
 *
 * @author evan.summers
 */
public class DualControlKeyStores {    
    static Logger logger = Logger.getLogger(DualControlKeyStores.class);
    
    public static KeyStore loadKeyStore(String keyStoreLocation, String keyStoreType,
            char[] keyStorePassword) 
            throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        if (keyStoreLocation.contains(":")) {
            String[] array = keyStoreLocation.split(":");
            String keyStoreHost = array[0];
            int keyStorePort = Integer.parseInt(array[1]);
            SSLContext sslContext = SSLContexts.create(false, 
                    "fileclient.ssl", System.getProperties(), 
                    new MockableConsoleAdapter(System.console()));
            Socket socket = sslContext.getSocketFactory().createSocket(
                    keyStoreHost, keyStorePort);
            keyStore.load(socket.getInputStream(), keyStorePassword);
            socket.close();
        } else if (new File(keyStoreLocation).exists()) {
            FileInputStream fis = new FileInputStream(keyStoreLocation);
            keyStore.load(fis, keyStorePassword);
            fis.close();
        } else {
            keyStore.load(null, null);
        }
        return keyStore;
    }

    public static KeyStore loadLocalKeyStore(String keyStoreLocation, String keyStoreType,
            char[] keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        if (new File(keyStoreLocation).exists()) {
            FileInputStream fis = new FileInputStream(keyStoreLocation);
            keyStore.load(fis, keyStorePassword);
            fis.close();
        } else {
            keyStore.load(null, null);
        }
        return keyStore;
    }
}
