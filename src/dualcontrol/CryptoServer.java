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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Properties;
import javax.net.ssl.SSLContext;
import org.apache.log4j.Logger;

/**
 *
 * @author evan.summers
 */
public class CryptoServer {
    static Logger logger = Logger.getLogger(CryptoServer.class);
    static final String purpose = "CryptoServer";
    DualControlKeyStoreSession dualControlSession = new DualControlKeyStoreSession();
    
    public static void main(String[] args) throws Exception {
        logger.info("args: " + Arrays.toString(args));
        if (args.length != 7) {
            System.err.println(
                    "usage: localAddress port backlog count remoteAddress keyStore storePass");
        } else {
            new CryptoServer().call(System.getProperties(), 
                    new MockableConsoleAdapter(System.console()),
                    InetAddress.getByName(args[0]), Integer.parseInt(args[1]), 
                    Integer.parseInt(args[2]), Integer.parseInt(args[3]), 
                    args[4], args[5], args[6].toCharArray());
        }
    }
    
    private void call(Properties properties, MockableConsole console, 
            InetAddress localAddress, int port, int backlog, int count, 
            String remoteHostAddress, String keyStoreLocation, char[] storePass) 
            throws Exception {
        dualControlSession.configure(keyStoreLocation, storePass, purpose);
        SSLContext sslContext = SSLContexts.create(true, "cryptoserver.ssl", 
                properties, console);
        ServerSocket serverSocket = sslContext.getServerSocketFactory().
                createServerSocket(port, backlog, localAddress);
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                logger.debug("remote " + socket.getInetAddress().getHostAddress());
                if (socket.getInetAddress().getHostAddress().equals(remoteHostAddress)) {
                    new CryptoHandler().handle(dualControlSession, socket);
                }
            } catch (Exception e) {
                logger.error("request handling error", e);
            } finally {
                close(socket);
            }
            if (count > 0 && --count == 0) break;
        }
    }
    
    private static void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
                logger.warn("close socket", e);
            }
        }
    }
}

