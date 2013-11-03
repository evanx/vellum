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

import vellum.crypto.rsa.GenRsaPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;
import sun.security.pkcs.PKCS10;
import sun.security.validator.Validator;

/**
 *
 * @author evan
 */
public class RevocableTrustManagerTest {

    static Logger logger = Logger.getLogger(RevocableTrustManagerTest.class);
    private int port = 4446;
    private char[] pass = "test1234".toCharArray();
    GenRsaPair serverPair;
    X509Certificate serverCert;
    KeyStore serverKeyStore;
    SSLContext serverContext;
    GenRsaPair clientPair;
    SSLContext clientContext;
    KeyStore clientKeyStore;
    X509Certificate clientCert;
    PKCS10 certRequest;
    KeyStore signedKeyStore;
    X509Certificate signedCert;
    SSLContext signedContext;

    public RevocableTrustManagerTest() {
    }

    @Test
    public void test() throws Exception {
        serverPair = new GenRsaPair();
        serverPair.generate("CN=server", new Date(), 365, TimeUnit.DAYS);
        serverCert = serverPair.getCertificate();
        serverKeyStore = createKeyStore("server", serverPair);
        clientPair = new GenRsaPair();
        clientPair.generate("CN=client", new Date(), 365, TimeUnit.DAYS);
        clientKeyStore = createKeyStore("client", clientPair);
        clientCert = (X509Certificate) clientKeyStore.getCertificate("client");
        serverContext = SSLContexts.create(serverKeyStore, pass, clientKeyStore);
        clientContext = SSLContexts.create(clientKeyStore, pass, serverKeyStore);
        testConnection(serverContext, clientContext);
    }

    //@Test
    public void testAll() throws Exception {
        initServer();
        initClient();
        testSigned();
        testRevoked();
        testInvalidServerCertOrder();
        testInvalidServerCertClient();
        testInvalidServerCertSigned();
        testInvalidServerCertOther();
    }

    private void initServer() throws Exception {
        serverPair = new GenRsaPair();
        serverPair.generate("CN=server", new Date(), 1, TimeUnit.DAYS);
        serverCert = serverPair.getCertificate();
        serverKeyStore = createKeyStore("server", serverPair);
        Assert.assertEquals("CN=server", serverCert.getIssuerDN().getName());
        Assert.assertEquals("CN=server", serverCert.getSubjectDN().getName());
        Assert.assertEquals(1, Collections.list(serverKeyStore.aliases()).size());
        serverContext = createContext(serverKeyStore, "revokedName");
        testConnection(serverContext, serverContext,
                "java.security.cert.CertificateException: Invalid cert chain length");
    }

    private void initClient() throws Exception {
        clientPair = new GenRsaPair();
        clientPair.generate("CN=client", new Date(), 1, TimeUnit.DAYS);
        clientKeyStore = createKeyStore("client", clientPair);
        clientCert = (X509Certificate) clientKeyStore.getCertificate("client");
        Assert.assertEquals("CN=client", clientCert.getIssuerDN().getName());
        Assert.assertEquals("CN=client", clientCert.getSubjectDN().getName());
        Assert.assertEquals(1, Collections.list(clientKeyStore.aliases()).size());
        clientContext = SSLContexts.create(clientKeyStore, pass, clientKeyStore);
        testConnection(clientContext, clientContext);
        testConnectionClient(serverContext, clientContext,
                "sun.security.validator.ValidatorException: No trusted certificate found");

    }

    private void testSigned() throws Exception {
        certRequest = clientPair.getCertRequest("CN=client");
        signedCert = Certificates.sign(serverPair.getPrivateKey(),
                serverPair.getCertificate(), certRequest, new Date(), 365, 1234,
                false, 0, KeyUsageType.DIGITAL_SIGNATURE);
        Assert.assertEquals("CN=server", signedCert.getIssuerDN().getName());
        Assert.assertEquals("CN=client", signedCert.getSubjectDN().getName());
        signedKeyStore = createSSLKeyStore("client", clientPair.getPrivateKey(), signedCert,
                serverPair.getCertificate());
        Assert.assertEquals(2, Collections.list(signedKeyStore.aliases()).size());
        signedContext = SSLContexts.create(signedKeyStore, pass,
                signedKeyStore);
        testConnection(serverContext, signedContext,
                "java.security.cert.CertificateException: Invalid parent certificate");
    }
       
    private void testRevoked() throws Exception {
        SSLContext revokedContext = createContext(serverKeyStore,
                Certificates.getCommonName(signedCert.getSubjectDN()));
        testConnection(revokedContext, signedContext,
                "java.security.cert.CertificateException: Certificate CN revoked");
    }

    private void testInvalidServerCertClient() throws Exception {
        KeyStore invalidKeyStore = createSSLKeyStore("client", clientPair.getPrivateKey(),
                signedCert, clientCert);
        SSLContext invalidContext = createContext(invalidKeyStore, null);
        testConnection(serverContext, invalidContext,
                "Received fatal alert: certificate_unknown");
    }

    private void testInvalidServerCertOrder() throws Exception {
        KeyStore invalidKeyStore = createSSLKeyStore("client", clientPair.getPrivateKey(),
                serverCert, signedCert);
        SSLContext invalidContext = createContext(invalidKeyStore, null);
        testConnection(serverContext, invalidContext,
                "java.security.cert.CertificateException: Invalid server certificate");
    }

    private void testInvalidServerCertSigned() throws Exception {
        KeyStore invalidKeyStore = createSSLKeyStore("client", clientPair.getPrivateKey(),
                signedCert, signedCert);
        SSLContext invalidContext = createContext(invalidKeyStore, null);
        testConnection(serverContext, invalidContext,
                "Received fatal alert: certificate_unknown");
    }

    private void testInvalidServerCertOther() throws Exception {
        GenRsaPair otherPair = new GenRsaPair();
        otherPair.generate("CN=server", new Date(), 1, TimeUnit.DAYS);
        KeyStore invalidKeyStore = createSSLKeyStore("client", clientPair.getPrivateKey(),
                signedCert, otherPair.getCertificate());
        SSLContext invalidContext = createContext(invalidKeyStore, null);
        testConnection(serverContext, invalidContext,
                "Received fatal alert: certificate_unknown");
    }

    private SSLContext createContext(KeyStore keyStore, String revokedName)
            throws Exception {
        Set<String> revocationList = new TreeSet();
        if (revokedName != null) {
            revocationList.add(revokedName);
        }
        return RevocableNameSSLContexts.create(keyStore, pass, keyStore,
                revocationList);
    }

    private void testConnection(SSLContext serverContext, SSLContext clientContext)
            throws Exception {
        ServerThread serverThread = new ServerThread();
        try {
            serverThread.start(serverContext, port, 1);
            Assert.assertNull(ClientThread.connect(clientContext, port));
            Assert.assertNull(serverThread.getErrorMessage());
        } finally {
            serverThread.close();
        }
    }

    private void testConnection(SSLContext serverContext, SSLContext clientContext,
            String expectedExceptionMessage) throws Exception {
        ServerThread serverThread = new ServerThread();
        try {
            serverThread.start(serverContext, port, 1);
            ClientThread.connect(clientContext, port);
            if (!serverThread.getErrorMessage().contains(expectedExceptionMessage)) {
                Assert.assertEquals(expectedExceptionMessage, serverThread.getErrorMessage());
            }
        } finally {
            serverThread.close();
        }
    }

    private void testConnectionClient(SSLContext serverContext, SSLContext clientContext,
            String expectedExceptionMessage) throws Exception {
        ServerThread serverThread = new ServerThread();
        try {
            serverThread.start(serverContext, port, 1);
            String clientErrorMessage = ClientThread.connect(clientContext, port);
            if (!clientErrorMessage.contains(expectedExceptionMessage)) {
                Assert.assertEquals(expectedExceptionMessage, clientErrorMessage);
            }
        } finally {
            serverThread.close();
        }
    }

    private KeyStore createKeyStore(String keyAlias, GenRsaPair keyPair) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        X509Certificate[] chain = new X509Certificate[]{keyPair.getCertificate()};
        keyStore.setKeyEntry(keyAlias, keyPair.getPrivateKey(), pass, chain);
        return keyStore;
    }

    private KeyStore createSSLKeyStore(String alias, PrivateKey privateKey,
            X509Certificate signedCert, X509Certificate issuer) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        X509Certificate[] chain = new X509Certificate[]{signedCert, issuer};
        keyStore.setCertificateEntry("issuer", issuer);
        keyStore.setKeyEntry(alias, privateKey, pass, chain);
        return keyStore;
    }

    private KeyStore createTrustStore(String alias, X509Certificate cert) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        X509Certificate[] chain = new X509Certificate[]{cert};
        keyStore.setCertificateEntry(alias, cert);
        return keyStore;
    }

    void testValidator() {
        Validator validator = Validator.getInstance(Validator.TYPE_SIMPLE,
                Validator.VAR_GENERIC, serverKeyStore);
    }
}
