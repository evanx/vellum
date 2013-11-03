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

import static localca.ServerThread.logger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import junit.framework.Assert;
import org.apache.log4j.Logger;

/**
 *
 * @author evan
 */
public class ClientThread extends Thread {
    final static Logger logger = Logger.getLogger(ClientThread.class);
    private static final String HOST = "localhost";

    private final SSLContext sslContext;
    private final int port;
    private final String host;
    private String errorMessage;

    public ClientThread(SSLContext sslContext, String host, int port) {
        this.sslContext = sslContext;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            connect(sslContext, port);
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
    }
    
    static String connect(SSLContext context, int port) 
            throws GeneralSecurityException, IOException {
        SSLSocket socket = (SSLSocket) context.getSocketFactory().
                createSocket(HOST, port);
    try {
            Thread.sleep(0);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF("clienthello");
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            Assert.assertEquals("serverhello", dis.readUTF());
            logger.info("ok");
            return null;
        } catch (Exception e) {
            logger.info("error: " + e.getMessage());
            return e.getMessage();
        } finally {
            socket.close();
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }    
}
