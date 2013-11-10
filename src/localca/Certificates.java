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

import java.security.Principal;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.pkcs.PKCS10;
import sun.security.pkcs.PKCS10Attribute;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.x509.AlgorithmId;
import sun.security.x509.BasicConstraintsExtension;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.KeyUsageExtension;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

/**
 *
 * @author evan.summers
 */
public class Certificates {
    static Logger logger = LoggerFactory.getLogger(Certificates.class);
    
    public static boolean equals(X509Certificate cert, X509Certificate other) {
        if (cert.getSubjectDN().equals(other.getSubjectDN())) {
            if (Arrays.equals(cert.getPublicKey().getEncoded(),
                    other.getPublicKey().getEncoded())) {
                return true;
            }
        }
        return false;
    }
    
    public static String getCommonName(Principal principal) throws CertificateException {
        String dname = principal.getName();
        try {
            LdapName ln = new LdapName(dname);
            for (Rdn rdn : ln.getRdns()) {
                if (rdn.getType().equalsIgnoreCase("CN")) {
                    return rdn.getValue().toString();
                }
            }
            throw new InvalidNameException("no CN: " + dname);
        } catch (Exception e) {
            throw new CertificateException(e.getMessage());
        }
    }    
    
    public static PKCS10 createCertReq(PrivateKey privateKey, X509Certificate cert) 
            throws Exception {
        String sigAlgName = "SHA256WithRSA";
        PKCS10 request = new PKCS10(cert.getPublicKey());
        if (false) {
            CertificateExtensions ext = new CertificateExtensions();
            request.getAttributes().setAttribute(X509CertInfo.EXTENSIONS,
                    new PKCS10Attribute(PKCS9Attribute.EXTENSION_REQUEST_OID, ext));
        }
        Signature signature = Signature.getInstance(sigAlgName);
        signature.initSign(privateKey);
        X500Name subject = new X500Name(cert.getSubjectDN().toString());
        request.encodeAndSign(subject, signature);
        return request;
    }
    
    public static X509Certificate sign(PrivateKey signingKey, X509Certificate signingCert,
            PKCS10 certReq, Date startDate, int validityDays, int serialNumber,
            boolean isCa, int pathLength, KeyUsageType keyUsage) 
            throws Exception {
        String sigAlgName = "SHA256WithRSA";
        Date endDate = new Date(startDate.getTime() + TimeUnit.DAYS.toMillis(validityDays));
        CertificateValidity validity = new CertificateValidity(startDate, endDate);
        byte[] encoded = signingCert.getEncoded();
        X509CertImpl signerCertImpl = new X509CertImpl(encoded);
        X509CertInfo signerCertInfo = (X509CertInfo) signerCertImpl.get(
                X509CertImpl.NAME + "." + X509CertImpl.INFO);
        X500Name issuer = (X500Name) signerCertInfo.get(
                X509CertInfo.SUBJECT + "." + CertificateSubjectName.DN_NAME);
        Signature signature = Signature.getInstance(sigAlgName);
        signature.initSign(signingKey);
        X509CertInfo certInfo = buildCertInfo(issuer, certReq, 
                sigAlgName, validity, serialNumber, isCa, pathLength, keyUsage);
        X509CertImpl cert = new X509CertImpl(certInfo);
        cert.sign(signingKey, sigAlgName);
        return cert;
    }
    
    private static X509CertInfo buildCertInfo(X500Name issuer, PKCS10 certReq, 
            String sigAlgName, CertificateValidity validity, int serialNumber,
            boolean isCa, int pathLength, KeyUsageType keyUsage) throws Exception {
        X509CertInfo info = new X509CertInfo();
        info.set(X509CertInfo.VALIDITY, validity);
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(serialNumber));
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        info.set(X509CertInfo.ALGORITHM_ID,
                new CertificateAlgorithmId(AlgorithmId.get(sigAlgName)));
        info.set(X509CertInfo.ISSUER, new CertificateIssuerName(issuer));
        info.set(X509CertInfo.KEY, new CertificateX509Key(certReq.getSubjectPublicKeyInfo()));
        info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(certReq.getSubjectName()));
        CertificateExtensions extensions = new CertificateExtensions();
        if (isCa) {
            BasicConstraintsExtension bce = new BasicConstraintsExtension(true, true, 1);
            extensions.set(BasicConstraintsExtension.NAME, bce);
        } else {
            BasicConstraintsExtension bce = new BasicConstraintsExtension(true, false, 0);
            extensions.set(BasicConstraintsExtension.NAME, bce);
            if (keyUsage != null) {
                KeyUsageExtension kue = new KeyUsageExtension(getKeyUsages(keyUsage));
                extensions.set(KeyUsageExtension.NAME, kue);
            }
        }
        info.set(X509CertInfo.EXTENSIONS, extensions);
        return info;
    }
    
    private static boolean[] getKeyUsages(KeyUsageType keyUsage) {
        boolean[] array = new boolean[9];
        array[keyUsage.ordinal()] = true;
        return array;        
    }
}