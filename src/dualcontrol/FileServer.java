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
import java.net.InetAddress;
import java.net.Socket;
import java.util.Set;
import java.util.TreeSet;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import org.apache.log4j.Logger;

/**
 *
 * @author evan.summers
 */
public class FileServer {
    private static Logger logger = Logger.getLogger(FileServer.class);
    private InetAddress localAddress;
    private int port;
    private int backlog;
    private int count;
    private Set<String> allowedHosts = new TreeSet();
    private String fileName;
    
    public static void main(String[] args) throws Exception {
        if (args.length != 6) {
            System.err.println("usage: localAddress port backlog count remoteAddress file");
        } else {
            new FileServer(InetAddress.getByName(args[0]), Integer.parseInt(args[1]), 
                    Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[4], args[5]).call();
        }
    }

    public FileServer(InetAddress localAddress, int port, int backlog, int count, 
            String remoteHostAddress, String fileName) {
        this.localAddress = localAddress;
        this.port = port;
        this.backlog = backlog;
        this.count = count;
        this.allowedHosts.add(remoteHostAddress);
        this.fileName = fileName;
    }
    
    public void call() throws Exception {
        SSLContext sslContext = SSLContexts.create(true, "fileserver.ssl", 
                System.getProperties(), new MockableConsoleAdapter(System.console()));
        SSLServerSocket serverSocket = (SSLServerSocket) sslContext.getServerSocketFactory().
                createServerSocket(port, backlog, localAddress);
        serverSocket.setNeedClientAuth(true);
        FileInputStream stream = new FileInputStream(fileName);
        int length = (int) new File(fileName).length();
        byte[] bytes = new byte[length];
        stream.read(bytes);
        while (true) {
            Socket socket = serverSocket.accept();
            logger.info("hostAddress " + socket.getInetAddress().getHostAddress());
            if (allowedHosts.contains(socket.getInetAddress().getHostAddress())) {
                socket.getOutputStream().write(bytes);
            }
            socket.close();
            if (count > 0 && --count == 0) break;
        }
    }
}
