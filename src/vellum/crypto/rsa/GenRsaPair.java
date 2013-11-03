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
package vellum.crypto.rsa;

import java.security.PrivateKey;
import java.util.Date;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import sun.security.pkcs.PKCS10;
import sun.security.x509.CertAndKeyGen;
import sun.security.x509.X500Name;
import vellum.security.Certificates;

/**
 *
 * @author evan.summers
 */
public class GenRsaPair {    
    String keyAlgName = "RSA";
    String sigAlgName = "SHA1WithRSA";
    int keySize = 2048;
    String dname;    
    Date notBefore;
    Date notAfter;
    CertAndKeyGen gen;
    X509Certificate cert;

    public void generate(String dname, Date notBefore, long validity, TimeUnit timeUnit) 
            throws Exception {
        this.dname = dname;
        this.notBefore = notBefore;
        notAfter = new Date(notBefore.getTime() + timeUnit.toMillis(validity));
        gen = new CertAndKeyGen(keyAlgName, sigAlgName);
        gen.generate(keySize);
        cert = gen.getSelfCertificate(new X500Name(dname), notBefore, 
                timeUnit.toSeconds(validity));
    }

    public CertAndKeyGen getGen() {
        return gen;
    }
    
    public PrivateKey getPrivateKey() {
        return gen.getPrivateKey();
    }

    public X509Certificate getCertificate() {
        return cert;
    }

    public PKCS10 getCertRequest(String dname) throws Exception {
        return gen.getCertRequest(new X500Name(dname));
    }   
    
    public void sign(PrivateKey signerKey, X509Certificate signerCert) throws Exception {
        cert = Certificates.signCert(signerKey, signerCert, getCertRequest(dname), 
                notBefore, notAfter);
    }
}
