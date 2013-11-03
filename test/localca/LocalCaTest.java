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

import vellumtest.util.Invoker;
import vellumtest.util.Exec;
import vellum.crypto.rsa.GenRsaPair;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.pkcs.PKCS10;

/**
 *
 * @author evan
 */
public class LocalCaTest {

    private final static Logger logger = LoggerFactory.getLogger(LocalCaTest.class);
    private final int port = 4446;
    private char[] pass = "test1234".toCharArray();
    private SSLEndPoint ca = new SSLEndPoint("ca");
    private SSLEndPoint server = new SSLEndPoint("server.com");
    private SSLEndPoint client = new SSLEndPoint("client");
    static int serialNumber = 1000;
    
    class SSLEndPoint {

        String alias;
        GenRsaPair pair;
        KeyStore keyStore;
        KeyStore trustStore;
        SSLContext sslContext;
        X509Certificate cert;
        SSLEndPoint signer;
        X509Certificate signedCert;
        KeyStore signedKeyStore;
        SSLContext signedContext;

        SSLEndPoint(String alias) {
            this.alias = alias;
        }

        void init() throws Exception {
            pair = new GenRsaPair();
            pair.generate("CN=" + alias, new Date(), 365, TimeUnit.DAYS);
            cert = pair.getCertificate();
            keyStore = createKeyStore(alias, pair);
        }

        void sign(SSLEndPoint signer) throws Exception {
            sign(signer, ++serialNumber, null);
        }

        void signServer(SSLEndPoint signer) throws Exception {
            sign(signer, ++serialNumber, KeyUsageType.KEY_ENCIPHERMENT);
        }

        void signClient(SSLEndPoint signer) throws Exception {
            sign(signer, ++serialNumber, KeyUsageType.DIGITAL_SIGNATURE);
        }
        
        void sign(SSLEndPoint signer, int serialNumber, KeyUsageType keyUsage) 
                throws Exception {
            PKCS10 certRequest = pair.getCertRequest("CN=" + alias);
            logger.info("sign {}", signer.cert.getSubjectDN());
            signedCert = Certificates.sign(signer.pair.getPrivateKey(),
                    signer.pair.getCertificate(), certRequest, new Date(), 365,
                    serialNumber, false, 0, keyUsage);
            this.signer = signer;
            if (signer.signer == null) {
                signedKeyStore = createKeyStore(alias, pair.getPrivateKey(),
                        signedCert, signer.cert);
            } else if (signer.signedCert != null) {
                signedKeyStore = createKeyStore(alias, pair.getPrivateKey(),
                        signedCert, signer.signedCert, signer.signer.cert);
            }
            signedKeyStore.store(createOutputStream(alias), pass);
        }

        void trust(X509Certificate trustedCert) throws Exception {
            trustStore = createTrustStore(alias, trustedCert);
            trustStore.store(createOutputStream(alias + ".trust"), pass);
            sslContext = SSLContexts.create(keyStore, pass, trustStore);
            if (signedKeyStore != null) {
                signedContext = SSLContexts.create(signedKeyStore, pass,
                        trustStore);
            }
        }
    }

    public LocalCaTest() {
    }

    @Test
    public void testExclusive() throws Exception {
        ca.init();
        server.init();
        client.init();
        server.trust(client.cert);
        client.trust(server.cert);
        testConnection(server.keyStore, server.trustStore,
                client.keyStore, client.trustStore);
        server.sign(ca);
        client.sign(server);
        testConnection(server.keyStore, server.trustStore,
                client.signedKeyStore, client.trustStore, 
                "peer not authenticated");
    }

    @Test
    public void testDynamicRevocation() throws Exception {
        ca.init();
        server.init();
        client.init();
        server.signServer(ca);
        client.signClient(ca);
        server.trust(ca.cert);
        client.trust(ca.cert);
        testDynamicNameRevocation(server.signedKeyStore, server.trustStore,
                client.signedKeyStore, client.trustStore, "client");
    }

    private void testConnection(KeyStore serverKeyStore, KeyStore serverTrustStore,
            KeyStore clientKeyStore, KeyStore clientTrustStore) throws Exception {
        SSLContext serverSSLContext = SSLContexts.create(serverKeyStore, pass, serverTrustStore);
        SSLContext clientSSLContext = SSLContexts.create(clientKeyStore, pass, clientTrustStore);
        ServerThread serverThread = new ServerThread();
        try {
            serverThread.start(serverSSLContext, port, 1);
            String clientErrorMessage = ClientThread.connect(clientSSLContext, port);
            Assert.assertNull(clientErrorMessage);
            Assert.assertNull(serverThread.getErrorMessage());
        } finally {
            serverThread.close();
        }
    }

    private void testConnection(KeyStore serverKeyStore, KeyStore serverTrustStore,
            KeyStore clientKeyStore, KeyStore clientTrustStore,
            String expectedErrorMessage) throws Exception {
        SSLContext serverSSLContext = SSLContexts.create(serverKeyStore, pass, serverTrustStore);
        SSLContext clientSSLContext = SSLContexts.create(clientKeyStore, pass, clientTrustStore);
        ServerThread serverThread = new ServerThread();
        try {
            serverThread.start(serverSSLContext, port, 1);
            String clientErrorMessage = ClientThread.connect(clientSSLContext, port);
            Assert.assertNotNull(clientErrorMessage);
            Assert.assertEquals(expectedErrorMessage, serverThread.getErrorMessage());
        } finally {
            serverThread.close();
        }
    }

    private void testDynamicNameRevocation(KeyStore serverKeyStore, KeyStore serverTrustStore,
            KeyStore clientKeyStore, KeyStore clientTrustStore,
            String revokeName) throws Exception {
        logger.info("testRevoke: " + revokeName);
        Set<String> revokedNames = new ConcurrentSkipListSet();
        SSLContext serverSSLContext = RevocableNameSSLContexts.create(
                serverKeyStore, pass, serverTrustStore, revokedNames);
        SSLContext clientSSLContext = SSLContexts.create(clientKeyStore, pass, clientTrustStore);
        ServerThread serverThread = new ServerThread();
        try {
            serverThread.start(serverSSLContext, port, 2);
            Assert.assertNull(ClientThread.connect(clientSSLContext, port));
            Assert.assertNull(serverThread.getErrorMessage());
            revokedNames.add(revokeName);
            logger.debug("revokedNames: " + revokedNames);
            Thread.sleep(1000);
            Assert.assertNotNull(ClientThread.connect(clientSSLContext, port));
            Assert.assertNotNull(serverThread.getErrorMessage());
        } finally {
            serverThread.close();
            serverThread.join(1000);
        }
    }

    public static void assertContains(String expected, String string) throws Exception {
        if (string == null) {
            throw new Exception("Expected to contain: [" + string
                    + "] but as null");
        } else if (!string.contains(expected)) {
            throw new Exception("Expected to contain: [" + string
                    + "] but was [" + string + "]");
        }
    }

    private FileOutputStream createOutputStream(String alias) throws IOException {
        String fileName = "/tmp/" + alias + ".jks";
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        logger.debug("createOutputStream {}", fileName);
        return new FileOutputStream(file);
    }

    private KeyStore createKeyStore(String keyAlias, GenRsaPair keyPair) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        X509Certificate[] chain = new X509Certificate[]{keyPair.getCertificate()};
        keyStore.setKeyEntry(keyAlias, keyPair.getPrivateKey(), pass, chain);
        return keyStore;
    }

    private KeyStore createTrustStore(String alias, X509Certificate cert) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry(alias, cert);
        return keyStore;
    }

    private KeyStore createKeyStore(String alias, PrivateKey privateKey,
            X509Certificate ... chain) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        for (int i = 1; i < chain.length; i++) {
            String commonName = Certificates.getCommonName(chain[i].getSubjectDN());
            keyStore.setCertificateEntry(commonName, chain[i]);
        }
        keyStore.setKeyEntry(alias, privateKey, pass, chain);
        return keyStore;
    }

    static void accept(KeyStore keyStore, char[] keyPassword, KeyStore trustStore,
            int port) throws GeneralSecurityException, IOException {
        SSLContext sslContext = SSLContexts.create(keyStore, keyPassword, trustStore);
        SSLServerSocket serverSocket = (SSLServerSocket) sslContext.
                getServerSocketFactory().createServerSocket(port);
        try {
            serverSocket.setNeedClientAuth(true);
            SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
            javax.security.cert.X509Certificate peer =
                    clientSocket.getSession().getPeerCertificateChain()[0];
            logger.info("peer: " + peer.getSubjectDN().getName());
            ServerThread.handle(clientSocket);
        } finally {
            serverSocket.close();
        }
    }

    //@Test
    public void testOpenssl() throws Exception {
        System.setProperty("Xjavax.net.debug", "ssl:trustmanager");
        server.init();
        new Invoker(new Object() {
            public void run() throws Exception {
                accept(server.keyStore, pass, server.trustStore, port);
            }
        }).start();
        logger.info(new Exec().exec("openssl s_client -connect localhost:4446"));
    }

    public static void main0(String[] args) throws Exception {
        try {
            new LocalCaTest().testOpenssl();
        } catch (Exception e) {
            logger.warn("", e);
        }
    }
}
