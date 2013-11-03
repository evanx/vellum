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
package vellum.hype;

import vellum.hype.java.JavaMeta;
import vellum.util.Streams;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 *
 * @author evan.summers
 */
public class HypeReader {

    HypeContext context = new HypeContext();
    String line;
    BlockType blockType;
    StringBuilder builder = new StringBuilder();
    String previousToken;
    boolean publicLine; 
    
    public void read(InputStream inputStream, OutputStream outputStream) throws Exception {
        BufferedReader reader = Streams.newBufferedReader(inputStream);
        PrintWriter printer = Streams.newPrintWriter(outputStream);
        while (true) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            processLine();
            printer.println(line);
        }
        printer.close();
    }

    private void processLine() throws Exception {
        if (blockType == BlockType.JAVA) {
            if (line.startsWith(JavaMeta.endPattern)) {
                blockType = null;
            } else {
                line = new HypeJavaLineProcessor(line).process();
            }
        } else if (line.startsWith(JavaMeta.beginPattern)) {
            blockType = BlockType.JAVA;
        }
    }
}

