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
package localca.certstore;

import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import sun.security.x509.X509CertImpl;

/**
 *
 * @author evans
 */
public class MockCertificateStorage implements CertificateStorage {
    Map<String, StoredCertificate> map = new HashMap();
    
    @Override
    public boolean contains(String commonName) throws CertificateStorageException {
        return map.containsKey(commonName) && map.get(commonName).getEncoded() != null;
    }
    
    @Override
    public boolean isNullCert(String commonName) throws CertificateStorageException {
        return map.containsKey(commonName) && map.get(commonName).getEncoded() == null;
    }

    @Override
    public void insert(String commonName, X509Certificate certificate) 
            throws CertificateStorageException {
        if (map.containsKey(commonName)) {
            throw new CertificateStorageException(CertificateStorageExceptionType.ALREADY_EXISTS,
                    commonName);            
        }
        try {
            map.put(commonName, new StoredCertificate(commonName,
                    Base64.encodeBase64String(certificate.getEncoded())));
        } catch (CertificateEncodingException e) {
            throw new CertificateStorageException(e, 
                    CertificateStorageExceptionType.ENCODING_ERROR, commonName);
        }
    }

    @Override
    public void setCert(String commonName, X509Certificate certificate) 
            throws CertificateStorageException {
        if (!map.containsKey(commonName)) {
            throw new CertificateStorageException(CertificateStorageExceptionType.NOT_FOUND,
                    commonName);            
        }
        if (map.get(commonName).getEncoded() != null) {
            throw new CertificateStorageException(CertificateStorageExceptionType.ALREADY_SET,
                    commonName);            
        }
        try {
            map.put(commonName, new StoredCertificate(commonName,
                    Base64.encodeBase64String(certificate.getEncoded())));
        } catch (CertificateEncodingException e) {
            throw new CertificateStorageException(e, 
                    CertificateStorageExceptionType.ENCODING_ERROR, commonName);
        }
    }
    
    @Override
    public void update(String commonName, X509Certificate certificate) 
            throws CertificateStorageException {
        if (!map.containsKey(commonName)) {
            throw new CertificateStorageException(CertificateStorageExceptionType.NOT_FOUND,
                    commonName);            
        }
        try {
            map.put(commonName, new StoredCertificate(commonName,
                    Base64.encodeBase64String(certificate.getEncoded())));
        } catch (CertificateEncodingException e) {
            throw new CertificateStorageException(e, 
                    CertificateStorageExceptionType.ENCODING_ERROR, commonName);
        }
    }

    @Override
    public X509Certificate load(String commonName) throws CertificateStorageException {
        if (!map.containsKey(commonName)) {
            throw new CertificateStorageException(CertificateStorageExceptionType.NOT_FOUND,
                    commonName);            
        }
        if (map.get(commonName).getEncoded() == null) {
            return null;
        }
        try {
            return new X509CertImpl(Base64.decodeBase64(map.get(commonName).getEncoded()));
        } catch (CertificateException e) {
            throw new CertificateStorageException(e, 
                    CertificateStorageExceptionType.DECODING_ERROR, commonName);            
        }
        
    }    

    @Override
    public boolean isEnabled(String commonName) throws CertificateStorageException {
        if (!map.containsKey(commonName)) {
            throw new CertificateStorageException(CertificateStorageExceptionType.NOT_FOUND,
                    commonName);            
        }
        return map.get(commonName).isEnabled();
    }
}
