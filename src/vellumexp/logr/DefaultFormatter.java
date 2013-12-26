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

import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class DefaultFormatter implements LogrFormatter {

    @Override
    public String format(LogrContext context, LogrRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append(record.getLevel().name());
        builder.append(" ");
        builder.append(context.toString());
        builder.append(" ");
        builder.append(record.getMessage());
        if (record.getArgs() != null && record.getArgs().length > 0) {
            builder.append(": ");
            builder.append(format(record.getArgs()));
        }
        return builder.toString();
    }
    
    String format(Object[] args) {
        return Args.format(args);
    }
 
}
