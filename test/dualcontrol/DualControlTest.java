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

import vellum.crypto.rsa.GenRsaPair;
import localca.SSLContexts;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.SSLContext;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @see MockConsole
 * @see DualControlConsole
 * @see DualControlManager
 * @see DualControlGenSecKey
 * 
 * @author evan
 */
public class DualControlTest {
    private KeyStore trustStore;
    private char[] keyStorePass = "test1234".toCharArray();
    private ExtendedProperties properties = new ExtendedProperties();
    private Map<String, char[]> dualMap = new TreeMap();
    private Map<String, KeyStore> keyStoreMap = new TreeMap();
    private Map<String, SSLContext> sslContextMap = new TreeMap();

    public DualControlTest() {
        properties.put("dualcontrol.verifyPassphrase", false);
        properties.put("alias", "dek2013");
        properties.put("storetype", "JCEKS");
        properties.put("keyalg", "AES");
        properties.put("keysize", "192");
    }

    @Test
    public void testPassphraseVerifier() throws Exception {
        Properties props = new Properties();
        Assert.assertNotNull(new DualControlPassphraseVerifier(props).
                getInvalidMessage("bbbb".toCharArray()));
        Assert.assertNull(new DualControlPassphraseVerifier(props).
                getInvalidMessage("B bb bb 44 44 !".toCharArray()));
    }
    
    @Test
    public void testCombineSplitPassword() throws Exception {
        Assert.assertEquals("bbbb|eeee", new String(DualControlManager.combineSplitPassword(
                "bbbb".toCharArray(), "eeee".toCharArray())));
    }
        
    @Test
    public void testGenKeyStore() throws Exception {
        dualMap.put("brent-evanx", "bbbb|eeee".toCharArray());
        dualMap.put("brent-henty", "bbbb|hhhh".toCharArray());
        dualMap.put("evanx-henty", "eeee|hhhh".toCharArray());
        MockConsole appConsole = new MockConsole("app", keyStorePass);
        DualControlGenSecKey instance = new DualControlGenSecKey(properties, appConsole);
        KeyStore keyStore = instance.buildKeyStore(dualMap);
        Assert.assertEquals(3, Collections.list(keyStore.aliases()).size());
        Assert.assertEquals("dek2013-brent-evanx", asSortedSet(keyStore.aliases()).first());
        SecretKey key = getSecretKey(keyStore, "dek2013-brent-evanx", "bbbb|eeee".toCharArray());
        Assert.assertEquals("AES", key.getAlgorithm());
        Assert.assertTrue(Arrays.equals(key.getEncoded(), getSecretKey(keyStore, 
                "dek2013-brent-henty", "bbbb|hhhh".toCharArray()).getEncoded()));
    }

    @Test
    public void testGenSecKey() throws Exception {
        initSSL();
        MockConsole appConsole = new MockConsole("app", keyStorePass);
        GenSecKeyThread genSecKeyThread = new GenSecKeyThread(
                new DualControlGenSecKey(properties, appConsole));
        System.out.print("app console: " + appConsole.getLine(0));
        SubmitterThread brentThread = createSubmitterThread("brent", "bbbb".toCharArray());
        SubmitterThread evanxThread = createSubmitterThread("evanx", "eeee".toCharArray());
        SubmitterThread hentyThread = createSubmitterThread("henty", "hhhh".toCharArray());
        waitPort();
        genSecKeyThread.start();
        brentThread.start();
        evanxThread.start();
        hentyThread.start();
        genSecKeyThread.join(2000);
        assertOk(genSecKeyThread.exception);
        assertOk(evanxThread.exception);
        assertOk(brentThread.exception);
        assertOk(hentyThread.exception);
        Assert.assertEquals("Connected evanx", evanxThread.console.getLine(0));
        Assert.assertEquals("Enter passphrase for new key dek2013: ", 
                evanxThread.console.getLine(1));
        Thread.sleep(1000);
    }
    
    class GenSecKeyThread extends Thread  {
        DualControlGenSecKey genSecKey;
        KeyStore keyStore;
        Exception exception;

        public GenSecKeyThread(DualControlGenSecKey genSecKey) {
            this.genSecKey = genSecKey;
        }
        
        @Override
        public void run() {
            try {
                genSecKey.init(sslContextMap.get("app"));
                keyStore = genSecKey.createKeyStore();
            } catch (Exception e) {
                exception = e;
            }
        }
    }
    
    
    @Test
    public void testReader() throws Exception {
        initSSL();
        DualControlManager manager = new DualControlManager(properties, 2, "app");
        manager.init(sslContextMap.get("app"));
        DualReaderThread readerThread = new DualReaderThread(manager);
        SubmitterThread brentThread = createSubmitterThread("brent", "bbbb".toCharArray());
        SubmitterThread evanxThread = createSubmitterThread("evanx", "eeee".toCharArray());
        waitPort();
        readerThread.start();
        brentThread.start();
        evanxThread.start();
        readerThread.join(2000);
        assertOk(evanxThread.exception);
        assertOk(brentThread.exception);
        assertOk(readerThread.exception);
        Assert.assertEquals("Connected evanx", evanxThread.console.getLine(0));
        Assert.assertEquals("Enter passphrase for app: ", evanxThread.console.getLine(1));
        Assert.assertEquals("brent-evanx", readerThread.dualEntry.getKey());
        Assert.assertEquals("bbbb|eeee", new String(readerThread.dualEntry.getValue()));
        Thread.sleep(1000);
    }

    private SubmitterThread createSubmitterThread(String alias, char[] password) {
        return new SubmitterThread(properties, new MockConsole(alias, password), 
                sslContextMap.get(alias));
        
    }
    
    private void assertOk(Exception e) throws Exception {
        if (e != null) {    
            throw e;
        }
    }
    
    private void initSSL() throws Exception {
        trustStore = KeyStore.getInstance("JKS");
        trustStore.load(null, keyStorePass);
        buildKeyStore("app");
        buildKeyStore("brent");
        buildKeyStore("evanx");
        buildKeyStore("henty");
        for (String name : keyStoreMap.keySet()) {
            sslContextMap.put(name, SSLContexts.create(
                keyStoreMap.get(name), 
                keyStorePass, trustStore));
        }
    }

    class SubmitterThread extends Thread  {
        MockConsole console;
        DualControlConsole dualControlConsole;
        Exception exception = null;
        
        public SubmitterThread(Properties properties, MockConsole console,
                SSLContext sslContext) {
            super();
            this.console = console;
            dualControlConsole = new DualControlConsole(properties, console);
            dualControlConsole.init(sslContext);
        }
        
        @Override
        public void run() {
            try {
                dualControlConsole.call();
            } catch (Exception e) {
                exception = e;
            }
        }
    }
    
    class DualReaderThread extends Thread  {
        DualControlManager manager;
        Map.Entry<String, char[]> dualEntry = null;
        Exception exception = null;

        public DualReaderThread(DualControlManager reader) {
            this.manager = reader;
        }
        
        @Override
        public void run() {
            try {
                manager.call();
                dualEntry = manager.getDualMap().entrySet().iterator().next();
            } catch (Exception e) {
                exception = e;
            }
        }
    }
    
    private KeyStore buildKeyStore(String alias) throws Exception {
        KeyStore keyStore = createSSLKeyStore(alias, 1);
        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
        String dname = cert.getSubjectDN().getName();
        Assert.assertEquals(alias, getCN(dname));
        keyStoreMap.put(alias, keyStore);
        trustStore.setCertificateEntry(alias, cert);
        return keyStore;
    }
    
    private KeyStore createSSLKeyStore(String name, int validityDays) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, keyStorePass);
        GenRsaPair keyPair = new GenRsaPair();
        keyPair.generate("CN=" + name, new Date(), validityDays, TimeUnit.DAYS);
        X509Certificate[] chain = new X509Certificate[] {keyPair.getCertificate()};
        keyStore.setKeyEntry(name, keyPair.getPrivateKey(), keyStorePass, chain);
        return keyStore;
    }
    
    public static SecretKey getSecretKey(KeyStore keyStore, String keyAlias, char[] keyPass) 
            throws GeneralSecurityException {
        KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(
                keyAlias, new KeyStore.PasswordProtection(keyPass));
        return entry.getSecretKey();
    }
    
    public static <E> SortedSet<E> asSortedSet(Enumeration<E> enumeration) {
        return new TreeSet(Collections.list(enumeration));
    }
    
    public static String getCN(String dname) throws InvalidNameException {
        LdapName ln = new LdapName(dname);
        for (Rdn rdn : ln.getRdns()) {
            if (rdn.getType().equalsIgnoreCase("CN")) {
                return rdn.getValue().toString();
            }
        }
        throw new InvalidNameException(dname);
    }

    private static void waitPort() throws InterruptedException {
        Sockets.waitPort("127.0.0.1", 4444, 2000, 500);
    }
    
    static Logger logger = Logger.getLogger(DualControlTest.class);    
}
