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
package vellum.logging;

import org.apache.log4j.Logger;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class FormatLogger {
    private Logger logger;

    public static FormatLogger getLogger(Class source) {
        return new FormatLogger(source);
    }
    
    private FormatLogger(Class source) {
        this.logger = Logger.getLogger(source);
    }
    
    public void trace(String message, Object ... args) {
        logger.trace(String.format(message, args));
    }
    
    public void verbose(String message, Object ... args) {
        logger.debug(String.format(message, args));
    }

    public void verboseArray(String message, Object ... args) {
        logger.debug(message + ": " + Args.format(args));
    }

    public void info(String message, Object ... args) {
        logger.info(String.format(message, args));
    }
    
    public void infoArray(String message, Object[] args) {
        logger.info(message + ": " + Args.format(args));
        
    }
    
    public void warn(String message, Object ... args) {
        logger.warn(String.format(message, args));
    }
    
    public void warn(Throwable throwable) {
        logger.warn(throwable.getMessage(), throwable);
    }
    
    public void warn(Throwable throwable, String message, Object ... args) {
        logger.warn(String.format(message, args), throwable);
    }

    public void error(String message, Object ... args) {
        logger.error(String.format(message, args));
    }
    
    public void error(Throwable throwable, String message, Object ... args) {
        logger.error(String.format(message, args), throwable);
    }
}
