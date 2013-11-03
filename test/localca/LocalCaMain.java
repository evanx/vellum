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

import java.util.Arrays;
import javax.net.ssl.SSLContext;
import junit.framework.Assert;

/**
 *
 * @author evan.summers
 */
public class LocalCaMain {
    final int PORT = 4445;
    
    public static void main(String[] args) throws Exception {
        System.err.println("main invoked with args: " + Arrays.toString(args));
        if (args.length == 5) {
            new LocalCaMain().connect(args[0], args[1], args[2], args[3], 
                    args[4].toCharArray());
        } else {
            System.err.println("usage: serverKS serverTS clientKS clientTS pass");
        }
    }
    
    private void connect(String serverKeyStoreLocation, String serverTrustStoreLocation,
            String clientKeyStoreLocation, String clientTrustStoreLocation,
            char[] pass) throws Exception {
        SSLContext serverSSLContext = SSLContexts.create(
                serverKeyStoreLocation, pass, serverTrustStoreLocation);
        SSLContext clientSSLContext = SSLContexts.create(
                clientKeyStoreLocation, pass, clientTrustStoreLocation);        
        ServerThread serverThread = new ServerThread();
        try {
            serverThread.start(serverSSLContext, PORT, 1);
            Assert.assertNull(ClientThread.connect(clientSSLContext, PORT));
            Assert.assertNull(serverThread.getErrorMessage());
        } finally {
            serverThread.close();
            serverThread.join(1000);
        }        
    }
}

