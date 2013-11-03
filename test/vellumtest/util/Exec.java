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
package vellumtest.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

/**
 *
 * @author evan
 */
public class Exec<T> {

    private final static Logger logger = Logger.getLogger(Exec.class);
    private int exitCode; 
    
    public String exec(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        logger.info("process started: " + command);
        exitCode = process.waitFor();
        logger.info("process completed");
        return readString(process.getInputStream());
    }

    public int getExitCode() {
        return exitCode;
    }    
    
    public static String readString(InputStream stream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                return builder.toString();
            }
            builder.append(line);
            builder.append("\n");
            logger.trace(line);
        }
    }
    
}
