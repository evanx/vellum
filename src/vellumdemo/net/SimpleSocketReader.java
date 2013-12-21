/*
 * Source https://github.com/evanx by @evanxsummers

       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements. See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership. The ASF licenses this file to
       you under the Apache License, Version 2.0 (the "License").
       You may not use this file except in compliance with the
       License. You may obtain a copy of the License at:

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.  
 */
package vellumdemo.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author evan.summers
 */
public abstract class SimpleSocketReader {

    final static int PORT = 4444;
    final static String HOST = "127.0.0.1";

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(HOST, PORT);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        String string = reader.readLine();
        System.err.println("read: " + string);
    }
}
