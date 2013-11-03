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

import dualcontrol.ExtendedProperties;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import javax.net.ssl.X509TrustManager;
import localca.Certificates;

/**
 *
 * @author evans
 */
public final class StorageTrustManager implements X509TrustManager {

    final private CertificateStorage certificateStorage;
    private boolean allowWithoutCertificate;
    private boolean allowExpired;
    private boolean insertNew;
    private boolean updateExpired;
    private boolean setNull;

    public StorageTrustManager(Properties properties, 
            CertificateStorage certificateStorage) {
        init(new ExtendedProperties(properties, "StorageTrustManager"));
        this.certificateStorage = certificateStorage;
    }
    
    private void init(ExtendedProperties properties) {
        this.allowWithoutCertificate = properties.getBoolean("allowWithoutCertificate");
        this.allowExpired = properties.getBoolean("allowExpired");
        this.insertNew = properties.getBoolean("insertNew");
        this.updateExpired = properties.getBoolean("updateExpired");
        this.setNull = properties.getBoolean("updateNull");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        if (chain.length == 0) {
            if (!allowWithoutCertificate) {
                throw new CertificateException("No certificate");
            }
        } else {
            try {
                X509Certificate peerCertificate = chain[0];
                if (!validate(Certificates.getCommonName(peerCertificate.getSubjectDN()),
                        peerCertificate)) {
                    throw new CertificateException("Certificate rejected");
                }
            } catch (CertificateStorageException e) {
                throw new CertificateException(e);
            }
        }
    }

    private boolean validate(String commonName, X509Certificate peerCertificate)
            throws CertificateStorageException, CertificateException {
        if (!certificateStorage.contains(commonName)) {
            if (insertNew) {
                certificateStorage.insert(commonName, peerCertificate);
                return true;
            }
        } else if (!certificateStorage.isEnabled(commonName)) {
            return false;
        } else if (certificateStorage.isNullCert(commonName)) {
            if (setNull) {
                certificateStorage.setCert(commonName, peerCertificate);
                return true;
            }
        } else {
            X509Certificate trustedCertificate = certificateStorage.load(commonName);
            if (Arrays.equals(peerCertificate.getPublicKey().getEncoded(),
                    trustedCertificate.getPublicKey().getEncoded())) {
                return allowExpired || !isExpired(trustedCertificate);
            } else if (updateExpired && isExpired(trustedCertificate)) {
                certificateStorage.update(commonName, peerCertificate);
                return true;
            }
        }
        return false;
    }

    private static boolean isExpired(X509Certificate certificate) {
        return new Date().after(certificate.getNotAfter());
    }
    
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        throw new CertificateException("Server authentication not supported");
    }
}
