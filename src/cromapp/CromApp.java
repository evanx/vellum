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
package cromapp;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import localca.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.crypto.rsa.RsaKeyStores;
import vellum.datatype.Millis;
import vellum.httpserver.VellumHttpsServer;
import vellum.type.ComparableTuple;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class CromApp implements Runnable {

    Logger logger = LoggerFactory.getLogger(getClass());
    CromConfig config = new CromConfig();
    CromProperties properties = new CromProperties();
    CromStorage storage = new CromStorage();
    VellumHttpsServer httpsServer;
    Map<ComparableTuple, StatusRecord> recordMap = new HashMap();
    Map<ComparableTuple, AlertRecord> alertMap = new HashMap();
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    
    public void init() throws Exception {
        config.init();
        properties.init(config.getProperties());
        storage.init();
        char[] keyPassword = Long.toString(new SecureRandom().nextLong() & 
                System.currentTimeMillis()).toCharArray();
        KeyStore keyStore = RsaKeyStores.createKeyStore("JKS", "crom", keyPassword, 365);
        SSLContext sslContext = SSLContexts.create(keyStore, keyPassword, 
                new CromTrustManager(this));
        httpsServer = new VellumHttpsServer();
        httpsServer.start(config.getProperties("httpsServer"), sslContext, 
                new CromHttpHandler(this));
        logger.info("initialized");
    }

    public void start() throws Exception {
        executorService.schedule(this, 3, TimeUnit.MINUTES);
        logger.info("started");
        if (config.systemProperties.getBoolean("crom.test")) {
            test();
        }
    }
    
    public void test() throws Exception {
        String pattern = "From: [a-z]+ \\(Cron Daemon\\)";
        logger.info("matches {}", "From: root (Cron Daemon)".matches(pattern));
        
    }

    public void stop() throws Exception {
        if (httpsServer != null) {
            httpsServer.shutdown();
        }
        executorService.shutdown();
    }

    public CromStorage getStorage() {
        return storage;
    }
    
    synchronized void putRecord(StatusRecord statusRecord) {
        StatusRecord previousRecord = recordMap.put(statusRecord.getKey(), statusRecord);
        if (previousRecord == null) {
            alertMap.put(statusRecord.getKey(), new AlertRecord(statusRecord));
        } else {
            AlertRecord previousAlert = alertMap.get(statusRecord.getKey());
            logger.info("putRecord {}", Arrays.toString(new Object[] {
                    previousAlert.getStatusRecord().getStatusType(), 
                    previousRecord.getStatusType(), statusRecord.getStatusType()}));
            if (statusRecord.isAlertable(previousRecord, previousAlert)) {
                AlertRecord alertRecord = new AlertRecord(statusRecord);
                alert(statusRecord);
                alertMap.put(statusRecord.getKey(), alertRecord);
            }
        }
    }
    
    @Override
    public void run() {
        for (StatusRecord statusRecord : recordMap.values()) {
            AlertRecord previousAlert = alertMap.get(statusRecord.getKey());
            if (previousAlert != null && previousAlert.getStatusRecord() != statusRecord &&
                    statusRecord.getPeriodMillis() != 0) {
                long period = Millis.elapsed(statusRecord.getTimestamp());
                if (period > statusRecord.getPeriodMillis() && 
                        period - statusRecord.getPeriodMillis() > 
                        Millis.fromMinutes(properties.getPeriodMinutes())) {
                        statusRecord.setStatusType(StatusType.ELAPSED);
                        alert(statusRecord);
                }                                    
            }
        }
    }
    
    synchronized void alert(StatusRecord statusRecord) {
        logger.info("ALERT {}", statusRecord.toString());
        if (properties.getAlertScript() != null) {
            try {
                exec(properties.getAlertScript(), 
                        "from=" + statusRecord.getFrom(),
                        "source=" + statusRecord.getSource(),
                        "status=" + statusRecord.getStatusType(),
                        "subject=" + statusRecord.getSubject(),
                        "alert=" + statusRecord.getAlertString()
                        );
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }
    
    
    public void exec(String command, String ... envp) throws Exception {
        Process process = Runtime.getRuntime().exec(command, envp);
        logger.info("process started: " + command);
        int exitCode = process.waitFor();
        logger.info("process completed {}", exitCode);
        logger.info("output\n{}\n", Streams.readString(process.getInputStream()));
    }
    
    public static void main(String[] args) throws Exception {
        try {
            CromApp app = new CromApp();
            app.init();
            app.start();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
