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

import dualcontrol.ExtendedProperties;
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
    Thread serverThread;
    VellumHttpsServer httpsServer;
    Map<ComparableTuple, StatusRecord> recordMap = new HashMap();
    Map<ComparableTuple, AlertRecord> alertMap = new HashMap();
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    
    public void init() throws Exception {
        config.init();
        properties.init(config.getProperties());
        storage.init();
        httpsServer = new VellumHttpsServer(config.getProperties("httpsServer"));
        char[] keyPassword = Long.toString(new SecureRandom().nextLong() & 
                System.currentTimeMillis()).toCharArray();
        KeyStore keyStore = RsaKeyStores.createKeyStore("JKS", "crom", keyPassword, 365);
        SSLContext sslContext = SSLContexts.create(keyStore, keyPassword, 
                new CromTrustManager(this));
        httpsServer.init(sslContext);        
        logger.info("initialized");
    }

    public void start() throws Exception {
        executorService.schedule(this, 3, TimeUnit.MINUTES);
        if (httpsServer != null) {
            httpsServer.start();
            httpsServer.createContext("/", new CromHttpHandler(this));
            logger.info("HTTPS server started");
        }
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
            httpsServer.stop();
        }
        executorService.shutdown();
    }

    public CromStorage getStorage() {
        return storage;
    }
    
    synchronized void putRecord(StatusRecord statusRecord) {
        StatusRecord previous = recordMap.put(statusRecord.getKey(), statusRecord);
        if (previous == null) {
            alertMap.put(statusRecord.getKey(), new AlertRecord(statusRecord));
        } else {
            AlertRecord alertRecord = alertMap.get(statusRecord.getKey());
            logger.info("putRecord {}", Arrays.toString(new Object[] {
                    alertRecord.getStatusRecord().getStatusType(), 
                    previous.getStatusType(), statusRecord.getStatusType()}));
            if (statusRecord.isAlertable(previous, alertRecord)) {
                alertChanged(statusRecord, previous, alertRecord);
                alertMap.put(statusRecord.getKey(), new AlertRecord(statusRecord));
            }
        }
    }
    
    synchronized void alertChanged(StatusRecord statusRecord, StatusRecord previous,
            AlertRecord previousAlert) {
        logger.info("ALERT {}", statusRecord.toString());
        if (properties.getAlertScript() != null) {
            try {
                exec(properties.getAlertScript(), 
                        "CROM_FROM=" + statusRecord.getFrom(),
                        "CROM_SOURCE=" + statusRecord.getSource(),
                        "CROM_SUBJECT=" + statusRecord.getSubject(),
                        "CROM_STATUS=" + statusRecord.getStatusType(),
                        "CROM_ALERT=" + statusRecord.getAlertString()
                        );
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    synchronized void alertElapsed(StatusRecord statusRecord) {
        logger.info("ALERT {}", statusRecord.toString());
        if (properties.getAlertScript() != null) {
            try {
                exec(properties.getAlertScript(), 
                        "CROM_FROM=" + statusRecord.getFrom(),
                        "CROM_SOURCE=" + statusRecord.getSource(),
                        "CROM_STATUS=ELAPSED",
                        "CROM_SUBJECT=" + statusRecord.getSubject(),
                        "CROM_ALERT=" + statusRecord.getAlertString()
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

    @Override
    public void run() {
        for (StatusRecord statusRecord : recordMap.values()) {
            AlertRecord alertRecord = alertMap.get(statusRecord.getKey());
            if (alertRecord != null && alertRecord.getStatusRecord() != statusRecord &&
                    statusRecord.getPeriodMillis() != 0) {
                long period = Millis.elapsed(statusRecord.getTimestamp());
                if (period > statusRecord.getPeriodMillis() && 
                        period - statusRecord.getPeriodMillis() > Millis.fromMinutes(5)) {                                  alertElapsed(statusRecord);
                }                                    
            }
        }
    }
}
