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
package vellum.hype.java;

import java.util.ArrayList;
import java.util.List;
import vellum.hype.Utils;

/**
 *
 * @author evan.summers
 */
public class JavaTokenizer {

    List<String> tokenList = new ArrayList();
    StringBuilder builder = new StringBuilder();
    char previous;
    boolean doubleQuoted;
    boolean singleQuoted;
    boolean word;
    boolean numeric;
    boolean comment;
    boolean escaped;
    boolean whitespace;
    boolean annotation;

    public List<String> tokenize(String line) {
        for (char ch : line.toCharArray()) {
            boolean appended = false;
            if (comment) {
            } else if (annotation) {
            } else if (escaped) {
                escaped = false;
            } else if (ch == '"') {
                if (doubleQuoted) {
                    appended = true;
                    builder.append(ch);
                    add();
                    doubleQuoted = false;
                } else {
                    add();
                    doubleQuoted = true;
                }
            } else if (doubleQuoted) {
                if (ch == '\\') {
                    escaped = true;
                }
            } else if (singleQuoted) {
                if (ch == '\\') {
                    escaped = true;
                }
            } else if (ch == '\'') {
                if (singleQuoted) {
                    appended = true;
                    builder.append(ch);
                    add();
                    singleQuoted = false;
                } else {
                    add();
                    singleQuoted = true;
                }
            } else if (ch == '@') {
                add();
                annotation = true;
            } else if (ch == '/') {
                if (previous == '/') {
                    comment = true;
                } else {
                    add();
                }
            } else if (!word) {
                add();
                if (Utils.isWord(ch)) {
                    word = true;
                }
            } else {
                if (!Utils.isWord(ch)) {
                    add();
                    word = false;
                }
            }
            if (!appended) {
                builder.append(ch);
            }
            previous = ch;
        }
        add();
        return tokenList;
    }

    void add() {
        if (builder.length() > 0) {
            //builder.append("~");
            tokenList.add(builder.toString());
            builder.setLength(0);
        }
    }
    
    
}
