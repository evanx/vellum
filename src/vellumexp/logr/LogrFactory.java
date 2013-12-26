/*
 * Source https://code.google.com/p/vellum by @evanxsummers

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
package vellumexp.logr;

/**
 *
 * @author evan.summers
 */
public class LogrFactory {

    final static DequerProvider dequerProvider = new DequerProvider();
    static LogrProvider provider = dequerProvider;
    static ThreadLocal threadLocalLogger = new ThreadLocal();
    static LogrLevel defaultLevel = LogrLevel.INFO;

    public static DequerProvider getDequerProvider() {
        return dequerProvider;
    }
    
    public static LogrProvider newProvider(String providerName) {
        try {
            return (LogrProvider) Class.forName(providerName).newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(providerName, e);
        }
    }
    
    public static void setProvider(LogrProvider provider) {
        LogrFactory.provider = provider;
    }
            
    public static void setDefaultLevel(LogrLevel defaultLevel) {
        LogrFactory.defaultLevel = defaultLevel;
    }

    public static LogrLevel getDefaultLevel() {
        return defaultLevel;
    }

    public static Logr getLogger(Class source) {
        return getLogger(new LogrContext(provider, defaultLevel, source, source.getSimpleName()));
    }

    public static Logr getLogger(Thread thread) {
        Logr logger = getLogger(new LogrContext(provider, defaultLevel, thread.getClass(), thread.getName()));
        threadLocalLogger.set(logger);
        return logger;
    }

    public static Logr getThreadLogger(Class source) {
        return getLogger(new LogrContext(provider, defaultLevel, source, Thread.currentThread().getName()));
    }

    private static Logr getLogger(LogrContext context) {
        return provider.getLogger(context);
    }
}
