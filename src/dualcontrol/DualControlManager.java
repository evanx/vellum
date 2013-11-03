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
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import org.apache.log4j.Logger;

/**
 *
 * @author evan.summers
 */
public class DualControlManager {

    private final static Logger logger = Logger.getLogger(DualControlManager.class);
    private final static int PORT = 4444;
    private final static String HOST = "127.0.0.1";
    private final static String REMOTE_ADDRESS = "127.0.0.1";
    private Properties properties;
    private boolean verifyPassphrase = true;
    private String purpose;
    private int submissionCount;
    private SSLContext sslContext;
    private Collection<String> verifiedNames = new TreeSet();
    private Map<String, char[]> submissions = new TreeMap();
    private Map<String, char[]> dualMap = new TreeMap();

    public DualControlManager(Properties properties, int submissionCount, String purpose) {
        this.properties = properties;
        this.submissionCount = submissionCount;
        this.purpose = purpose;
    }

    public void setVerifyPassphrase(boolean verifyPassphrase) {
        this.verifyPassphrase = verifyPassphrase;
    }
    
    public void addVerifiedNames(Collection<String> verifiedNames) {
        this.verifiedNames.addAll(verifiedNames);
    }
    
    public void init(SSLContext sslContent) {
        this.sslContext = sslContent;
    }

    public void call() throws Exception {
        logger.info("purpose: "  + purpose);
        SSLServerSocket serverSocket = (SSLServerSocket) sslContext.
                getServerSocketFactory().createServerSocket(PORT, submissionCount,
                InetAddress.getByName(HOST));
        try {
            serverSocket.setNeedClientAuth(true);
            accept(serverSocket);
        } finally {
            serverSocket.close();
        }
        buildDualMap();
    }

    private void buildDualMap() {
        for (String name : submissions.keySet()) {
            for (String otherName : submissions.keySet()) {
                if (name.compareTo(otherName) < 0) {
                    String dualAlias = String.format("%s-%s", name, otherName);
                    char[] dualPassword = combineSplitPassword(
                            submissions.get(name), submissions.get(otherName));
                    dualMap.put(dualAlias, dualPassword);
                    logger.info("dualAlias: " + dualAlias);
                }
            }
        }
        for (char[] password : submissions.values()) {
            Arrays.fill(password, (char) 0);
        }
    }

    public void clear() {
        for (char[] password : dualMap.values()) {
            Arrays.fill(password, (char) 0);
        }    
    }
    
    public static char[] combineSplitPassword(char[] password, char[] other) {
        char[] splitPassword = new char[password.length + other.length + 1];
        int index = 0;
        for (char ch : password) {
            splitPassword[index++] = ch;
        }
        splitPassword[index++] = '|';
        for (char ch : other) {
            splitPassword[index++] = ch;
        }
        return splitPassword;
    }
    
    private void accept(SSLServerSocket serverSocket) throws Exception {
        logger.info("accept: " + submissionCount);
        while (submissions.size() < submissionCount) {
            logger.debug(String.format("Waiting for %d of %d", submissions.size() + 1, 
                    submissionCount));
            SSLSocket socket = (SSLSocket) serverSocket.accept();
            try {
                if (!socket.getInetAddress().getHostAddress().equals(REMOTE_ADDRESS)) {
                    throw new Exception("Invalid remote address: "
                            + socket.getInetAddress().getHostAddress());
                }
                read(socket);
            } catch (Exception e) {
                logger.warn(e.getMessage());
            } finally {
                socket.close();
            }
        }
    }

    private void read(SSLSocket socket) throws Exception {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        String name = getCN(socket.getSession().getPeerPrincipal());
        if (submissions.keySet().contains(name)) {
            String errorMessage = "Duplicate submission from " + name;
            dos.writeUTF(errorMessage);
            dos.writeUTF("");
            throw new Exception(errorMessage);
        }
        dos.writeUTF("Connected " + name);
        dos.writeUTF(purpose);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        char[] passphrase = readChars(dis);
        try {
            String resultMessage = verify(name, passphrase);
            dos.writeUTF(resultMessage);
            logger.info(resultMessage);
        } catch (Exception e) {
            dos.writeUTF(e.getMessage());
            logger.warn(e.getMessage());
            throw e;
        }
    }
    
    private String verify(String name, char[] passphrase) throws Exception {
        if (passphrase.length == 0) {
            return "Empty submission from " + name;
        }
        String responseMessage = "Received " + name;
        if (verifyPassphrase && !verifiedNames.contains(name)) {
            String invalidMessage = new DualControlPassphraseVerifier(properties).
                    getInvalidMessage(passphrase);
            if (invalidMessage != null) {
                throw new Exception(responseMessage + ": " + invalidMessage);
            }
        }
        submissions.put(name, passphrase);
        return responseMessage;
    }
    
    public static char[] readChars(DataInputStream dis) throws IOException {
        char[] chars = new char[dis.readShort()];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = dis.readChar();
        }
        return chars;
    }

    public Map<String, char[]> getDualMap() {
        return dualMap;
    }

    public static Map.Entry<String, char[]> readDualEntry(String purpose) throws Exception {
        DualControlManager manager = new DualControlManager(System.getProperties(), 2, purpose);
        manager.setVerifyPassphrase(false);
        SSLContext sslContext = SSLContexts.create(true, "dualcontrol.ssl", 
                System.getProperties(), new MockableConsoleAdapter(System.console()));
        manager.init(sslContext);
        manager.call();
        return manager.getDualMap().entrySet().iterator().next();
    }
    
    public static String getCN(Principal principal) throws InvalidNameException {
        String dname = principal.getName();
        LdapName ln = new LdapName(dname);
        for (Rdn rdn : ln.getRdns()) {
            if (rdn.getType().equalsIgnoreCase("CN")) {
                return rdn.getValue().toString();
            }
        }
        throw new InvalidNameException(dname);
    }
}
