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
package vellum.hype;

import vellum.util.Lists;
import vellum.hype.java.JavaMeta;
import java.util.List;
import vellum.hype.java.JavaTokenizer;

/**
 *
 * @author evan.summers
 */
public class HypeJavaLineProcessor {

    HypeContext context = new HypeContext();
    String line;
    StringBuilder builder = new StringBuilder();
    String previousToken;
    String previousWord;
    String previousKeyword;
    boolean publicLine = false; 
    int index = 0;
    
    public HypeJavaLineProcessor(String line) {
        this.line = line;
    }
    
    public String process() throws Exception {
        builder.setLength(0);
        List<String> tokenList = new JavaTokenizer().tokenize(line);
        if (tokenList.size() > 0) {
            for (; index < tokenList.size() - 1; index++) {
                processJava(tokenList.get(index), tokenList.get(index + 1));
            }
            processJava(tokenList.get(index), null);
        }
        return builder.toString();
    }

    private void processJava(String token, String nextToken) throws Exception {
        int length = builder.length();
        if (Utils.isWhitespace(token)) {
        } else if (token.startsWith("\"")) {
            builder.append("<span class=\"character\">");
            builder.append(token);
            builder.append("</span>");
        } else if (token.startsWith("//")) {
            builder.append("<span class=\"comment\">");
            builder.append(token);
            builder.append("</span>");
        } else if (token.startsWith("@")) {
            builder.append("<span class=\"comment\">");
            builder.append(token);
            builder.append("</span>");
        } else if (Lists.contains(JavaMeta.keywords, token)) {
            builder.append("<span class=\"keyword-directive\">");
            builder.append(token);
            builder.append("</span>");
            if (token.equals("public")) {
                publicLine = true;
            }
            previousKeyword = token;
        } else {
            if (Utils.isWord(token)) {
                if (publicLine) {
                    if (nextToken != null && nextToken.equals("(")) {
                        builder.append("<b>");
                        builder.append(token);
                        builder.append("</b>");
                    } else if (Lists.contains(JavaMeta.classKeywords, previousKeyword)) {
                        builder.append("<b>");
                        builder.append(token);
                        builder.append("</b>");
                    }
                    publicLine = false;
                } else {
                    builder.append(token);
                }
                previousWord = token;
            }
        }
        if (builder.length() == length) {
            builder.append(token);
        }
        previousToken = token;
    }
}
