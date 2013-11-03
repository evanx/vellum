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

import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 *
 * @author evan.summers
 */
public class ArrayLogger {
    private Logger logger;

    public static ArrayLogger getLogger(Class source) {
        return new ArrayLogger(Logger.getLogger(source));
    }
    
    private ArrayLogger(Logger logger) {
        this.logger = logger;
    }
            
    public void trace(Object ... args) {
        logger.trace(Arrays.toString(args));
    }

    public void debug(Object ... args) {
        logger.debug(Arrays.toString(args));
    }
    
    public void info(Object ... args) {
        logger.info(Arrays.toString(args));
    }

    public void warn(Object ... args) {
        logger.warn(Arrays.toString(args));
    }

    public void error(Object ... args) {
        logger.error(Arrays.toString(args));
    }
    
}
