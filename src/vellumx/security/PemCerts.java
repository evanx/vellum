/*
 Source https://code.google.com/p/vellum by @evanxsummers

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
package vellumx.security;

import java.security.Key;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.provider.X509Factory;
import sun.security.x509.X509CertImpl;
import vellum.exception.Exceptions;

/**
 *
 * @author evan.summers
 */
public class PemCerts {

    public static final String BEGIN_PRIVATE_KEY = formatPem("BEGIN PRIVATE KEY");
    public static final String END_PRIVATE_KEY = formatPem("END PRIVATE KEY");
    public static final String BEGIN_CERT = formatPem("BEGIN CERTIFICATE");
    public static final String END_CERT = formatPem("END CERTIFICATE");
    private static final String dashes = "-----";

    private static String formatPem(String label) {
        return dashes + label + dashes;
    }
    
    public static String buildKeyPem(Key privateKey) throws Exception, CertificateException {
        StringBuilder builder = new StringBuilder();
        BASE64Encoder encoder = new BASE64Encoder();
        builder.append(BEGIN_PRIVATE_KEY);
        builder.append('\n');
        builder.append(encoder.encodeBuffer(privateKey.getEncoded()));
        builder.append(END_PRIVATE_KEY);
        builder.append('\n');
        return builder.toString();
    }

    public static String buildCertPem(Certificate cert) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(X509Factory.BEGIN_CERT);
            builder.append('\n');
            BASE64Encoder encoder = new BASE64Encoder();
            builder.append(encoder.encodeBuffer(cert.getEncoded()));
            builder.append(X509Factory.END_CERT);
            builder.append('\n');
            return builder.toString();
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static byte[] decodePemDer(String pem) throws Exception {
        int index = pem.lastIndexOf(dashes);
        if (index > 0) {
            pem = pem.substring(0, index);
            index = pem.lastIndexOf(dashes);
            pem = pem.substring(0, index);
            index = pem.lastIndexOf(dashes);
            pem = pem.substring(index + dashes.length());
        }
        return new BASE64Decoder().decodeBuffer(pem);
    }

    public static X509Certificate parseCert(String pem) throws Exception {
        return new X509CertImpl(decodePemDer(pem));
    }        
}
