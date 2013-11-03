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
package localca;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Set;
import java.util.TreeSet;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import org.apache.log4j.Logger;

/**
 *
 * @author evan.summers
 */
public class RevocableSSLContexts {

    static Logger logger = Logger.getLogger(RevocableSSLContexts.class);
    static Set<String> revokedNames; 
    
    public static SSLContext create(KeyStore keyStore, char[] keyPass,
            KeyStore trustStore, Set<BigInteger> revokedSerialNumbers) 
            throws GeneralSecurityException {
        X509TrustManager revocableTrustManager = new RevocableTrustManager(
                KeyStores.findPrivateKeyCertificate(keyStore),
                KeyStores.findX509TrustManager(trustStore),
                new TreeSet(),
                revokedSerialNumbers);
        return SSLContexts.create(keyStore, keyPass, revocableTrustManager);
    }
    
    static Set<BigInteger> readRevokedSerialNumbers(String crlFile)
            throws FileNotFoundException, IOException {
        Set<BigInteger> revocationList = new TreeSet();
        BufferedReader reader = new BufferedReader(new FileReader(crlFile));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                return revocationList;
            }
            revocationList.add(new BigInteger(line.trim()));
        }    
    }
}