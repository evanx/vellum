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
package dualcontrol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.util.Arrays;
import org.apache.commons.codec.binary.Base32;

/**
 *
 * @author evan.summers
 */
public class DualControlDigest {
    public final static String DIGEST_ALG = "SHA-256";
    
    public static String digestBase32(char[] chars) {
        try {
            return new Base32().encodeAsString(digest(chars));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String digestString(char[] chars) throws Exception {
        return new String(digest(chars));
    }
    
    public static byte[] digest(char[] chars) throws Exception {
        byte[] bytes = getBytes(chars);
        byte[] digestBytes = MessageDigest.getInstance(DIGEST_ALG).digest(bytes);
        Arrays.fill(bytes, (byte) 0);
        return digestBytes;
    }
    
    public static byte[] getBytes(char[] chars) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(baos);
        writer.write(chars);
        writer.close();
        return baos.toByteArray();
    }
}
