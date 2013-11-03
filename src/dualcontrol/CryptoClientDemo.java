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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Properties;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author evan.summers
 */
public class CryptoClientDemo {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("CryptoClientDemo usage: hostAddress port text");
        } else {
            new CryptoClientDemo().call(System.getProperties(), 
                    new MockableConsoleAdapter(System.console()),
                    args[0], Integer.parseInt(args[1]), args[2].getBytes());
        }
    }

    private void call(Properties properties, MockableConsole console, 
            String hostAddress, int port, byte[] data) throws Exception {
        Socket socket = SSLContexts.create(false, "cryptoclient.ssl", 
                properties, console).getSocketFactory().
                createSocket(hostAddress, port);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeShort(data.length);
        dos.write(data);
        dos.flush();
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        byte[] ivBytes = new byte[dis.readShort()];
        dis.readFully(ivBytes);
        byte[] bytes = new byte[dis.readShort()];
        dis.readFully(bytes);
        if (new String(data).contains("DECRYPT")) {
            System.err.printf("INFO CryptoClientDemo decrypted %s\n", new String(bytes)); 
        } else {
            System.out.printf("%s:%s\n", 
                    Base64.encodeBase64String(ivBytes), Base64.encodeBase64String(bytes));    
        }
        socket.close();
    }
}
