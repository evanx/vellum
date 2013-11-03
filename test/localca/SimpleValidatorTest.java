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

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.junit.Test;
import sun.security.validator.Validator;
import vellum.crypto.rsa.GenRsaPair;

/**
 *
 * @author evan
 */
public class SimpleValidatorTest {

    final static Logger logger = Logger.getLogger(SimpleValidatorTest.class);
    char[] pass = "test1234".toCharArray();
    Validator validator;
    
    @Test
    public void testExclusive() throws Exception {
        GenRsaPair serverPair = new GenRsaPair();
        serverPair.generate(null, new Date(), 1, TimeUnit.DAYS);
        KeyStore trustStore = createTrustStore("server", serverPair.getCertificate());
        this.validator = Validator.getInstance(Validator.TYPE_SIMPLE,
                Validator.VAR_GENERIC, trustStore);
    }
    
    private KeyStore createTrustStore(String alias, X509Certificate cert) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry(alias, cert);
        return keyStore;
    }
    
}
