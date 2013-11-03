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
package vellum.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.CharBuffer;

/**
 *
 * @author evan.summers
 */
public class Chars {

    public static char[] readChars(InputStream inputStream, int capacity) throws IOException {
        CharBuffer buffer = CharBuffer.allocate(capacity);
        while (true) {
            int b = inputStream.read();
            if (b < 0) {
                return buffer.array();
            }
            buffer.append((char) b);
        }
    }   

    public static byte[] getBytes(char[] chars) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(baos);
        writer.write(chars);
        writer.close();
        return baos.toByteArray();
    }
    
    public static byte[] getAsciiBytes(char[] chars) {
        byte[] array = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            array[i] = (byte) chars[i];    
        }
        return array;
    }   
}
