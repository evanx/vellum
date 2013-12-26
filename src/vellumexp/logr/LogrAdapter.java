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
public class LogrAdapter implements Logr {

    LogrHandler handler;
    LogrContext context;

    public LogrAdapter(LogrContext context, LogrHandler handler) {
        this.context = context;
        this.handler = handler;
    }

    private boolean isLevel(LogrLevel level) {
        return level.ordinal() >= context.getLevel().ordinal();
    }

    @Override
    public void trace(String message, Object... args) {
        if (isLevel(LogrLevel.TRACE)) {
            handler.handle(context, new LogrRecord(LogrLevel.TRACE, message, args));
        }
    }

    @Override
    public void verbose(String message, Object... args) {
        if (isLevel(LogrLevel.VERBOSE)) {
            handler.handle(context, new LogrRecord(LogrLevel.VERBOSE, message, args));
        }
    }

    @Override
    public void verboseArray(String message, Object[] args) {
        if (isLevel(LogrLevel.VERBOSE)) {
            handler.handle(context, new LogrRecord(LogrLevel.VERBOSE, message, args));
        }
    }
    
    @Override
    public void info(String message, Object... args) {
        if (isLevel(LogrLevel.INFO)) {
            handler.handle(context, new LogrRecord(LogrLevel.INFO, message, args));
        }
    }

    @Override
    public void infoArray(String message, Object[] args) {
        if (isLevel(LogrLevel.INFO)) {
            handler.handle(context, new LogrRecord(LogrLevel.INFO, message, args));
        }
    }
    
    @Override
    public void feature(String message, Object... args) {
        if (isLevel(LogrLevel.FEATURE)) {
            handler.handle(context, new LogrRecord(LogrLevel.INFO, message, args));
        }
    }
    
    @Override
    public void warn(String message, Object... args) {
        if (isLevel(LogrLevel.WARN)) {
            handler.handle(context, new LogrRecord(LogrLevel.WARN, message, args));
        }
    }

    @Override
    public void error(String message, Object... args) {
        if (isLevel(LogrLevel.ERROR)) {
            handler.handle(context, new LogrRecord(LogrLevel.ERROR, message, args));
        }
    }

    @Override
    public void warn(Throwable throwable) {
        if (isLevel(LogrLevel.WARN)) {
            handler.handle(context, new LogrRecord(throwable, LogrLevel.WARN));
        }
    }
    
    @Override
    public void warn(Throwable throwable, String message, Object... args) {
        if (isLevel(LogrLevel.WARN)) {
            handler.handle(context, new LogrRecord(throwable, LogrLevel.WARN, message, args));
        }
    }

    @Override
    public void error(Throwable throwable, String message, Object... args) {
        if (isLevel(LogrLevel.ERROR)) {
            handler.handle(context, new LogrRecord(throwable, LogrLevel.ERROR, message, args));
        }
    }

}
