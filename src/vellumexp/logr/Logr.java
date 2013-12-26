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
public interface Logr {

    public void trace(String message, Object ... args);
    
    public void verbose(String message, Object ... args);

    public void verboseArray(String message, Object ... args);

    public void info(String message, Object ... args);
    
    public void infoArray(String message, Object[] args);
    
    public void feature(String message, Object ... args);

    public void warn(String message, Object ... args);
    
    public void error(String message, Object ... args);

    public void warn(Throwable throwable);
    
    public void warn(Throwable throwable, String message, Object ... args);
    
    public void error(Throwable throwable, String message, Object ... args);
    
}
