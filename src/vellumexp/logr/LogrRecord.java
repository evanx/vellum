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

import vellum.data.Timestamped;

/**
 *
 * @author evan.summers
 */
public class LogrRecord implements Timestamped {
    LogrLevel level;
    Throwable throwable;
    String message;
    Object[] args;
    long timestamp = System.currentTimeMillis();
    LogrContext context;
    
    public LogrRecord(LogrLevel level, String message, Object[] args) {
        this.level = level;
        this.message = message;
        this.args = args;
    }

    public LogrRecord(Throwable throwable, LogrLevel level, String message, Object[] args) {
        this(level, message, args);
        this.throwable = throwable;
    }

    public LogrRecord(Throwable throwable, LogrLevel level) {
        this.level = level;
        this.throwable = throwable;
    }
    
    public void setContext(LogrContext context) {
        this.context = context;
    }

    public LogrContext getContext() {
        return context;
    }
        
    public LogrLevel getLevel() {
        return level;
    }

    public Throwable getThrowable() {
        return throwable;
    }
    
    public String getMessage() {
        return message;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

}
