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

import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import vellum.data.Millis;
import vellum.data.Nanos;

/**
 *
 * @author evan.summers
 */
public class KeyStoreBruteForceTimer extends Thread implements Cloneable, Runnable {
    private final static Logger logger = Logger.getLogger(KeyStoreBruteForceTimer.class);
    private static int passwordLength = Integer.getInteger("passwordLength", 8);
    private SecureRandom random = new SecureRandom();
    private Set<String> errorMessageSet = new TreeSet();
    private int threadCount;
    private int maximumCount;
    private String keyStoreLocation;
    private String keyStoreType;
    private char[] keyStorePass;
    private String alias;
    private char[] keyPass;
    private Exception exception;
    private String result; 
    private KeyStore keyStore;
    
    public static void main(String[] args) throws Exception {
        if (args.length != 7) {
            System.err.println("usage: threads count keystore storetype storepass alias keypass"); 
        } else {
            
            new KeyStoreBruteForceTimer(args).start(args);
        }
    }

    public KeyStoreBruteForceTimer(String[] args) throws Exception {
        threadCount = Integer.parseInt(args[0]);
        maximumCount = Integer.parseInt(args[1]);
        keyStoreLocation = args[2];
        keyStoreType = args[3];
        keyStorePass = args[4].toCharArray();
        alias = args[5];
        keyPass = args[6].toCharArray();
        keyStore = DualControlKeyStores.loadLocalKeyStore(keyStoreLocation, 
                keyStoreType, keyStorePass);
    }

    void start(String[] args) throws Exception {  
        logger.info("keyStoreLocation " + keyStoreLocation);
        logger.info("alias " + alias);
        logger.info("keyPass " + new String(keyPass));
        List<KeyStoreBruteForceTimer> threadList = new ArrayList();
        long nanos = System.nanoTime();
        for (int i = 0; i < threadCount; i++) {
            KeyStoreBruteForceTimer thread = new KeyStoreBruteForceTimer(args);
            thread.start();
            threadList.add(thread);
        }
        for (KeyStoreBruteForceTimer thread : threadList) {
            thread.join();
            if (thread.exception != null) {
                logger.error(thread.exception.getMessage(), thread.exception);
            } else {
                logger.info(thread.result);
            }
        }
        nanos = Nanos.elapsed(nanos);
        long average = nanos/maximumCount/threadCount;
        System.out.printf("threads %d, count %d, time %s, avg %s\n",
                threadCount, maximumCount, Millis.formatPeriod(Nanos.toMillis(nanos)), 
                Nanos.formatMillis(average));
        if (average > 0) {
            System.out.printf("%d guesses per millisecond\n", 1000*1000/average);
        }
    }
    
    @Override
    public void run() {
        try {
            call();
        } catch (Exception e) {
            this.exception = e;
        }
    }
    
    void call() throws Exception {
        long correctNanos = System.nanoTime();
        keyStore.getKey(alias, keyPass);
        correctNanos = Nanos.elapsed(correctNanos);
        long nanos = System.nanoTime();
        int exceptionCount = 0;
        for (int i = 0; i < maximumCount; i++) {
            try {
                char[] password = generateRandomPassword(8);
                if (i%500 == 0) password = keyPass;
                logger.trace("key " + keyStore.getKey(alias, password).getAlgorithm());
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    errorMessageSet.add(e.getMessage());
                }
                exceptionCount++;
            }
        }
        nanos = Nanos.elapsed(nanos);
        long average = nanos/maximumCount;
        result = String.format(
                "%s %d, exceptions %d (%d), correct %s, time %s, avg %s\n", 
                alias, maximumCount, exceptionCount, errorMessageSet.size(), 
                Nanos.formatMillis(correctNanos), Nanos.formatMillis(nanos), 
                Nanos.formatMillis(average));
        
    }
    
    char[] generateRandomPassword(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(nextChar());
        }
        return builder.toString().toCharArray();
    }
    
    char nextChar() {
        char first = ' ';
        char last = 'z';
        return (char) (first + (random.nextInt(last - first)));
    }
}
