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
package crumapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.type.ComparableTuple;

/**
 * 
 * @author evan.summers
 */
public class CrumRecord {
    static Logger logger = LoggerFactory.getLogger(CrumRecord.class);
    static Pattern subjectCronPattern = Pattern.compile("^Subject: Cron <(\\S+)@(\\S+)> (.*)$");

    List<String> lineList = new ArrayList();
    AlertType alertType;
    String alertString;
            
    String fromLine;
    String subjectLine;
    String contentTypeLine;
    String contentType;
    String from; 
    String subject;
    String username; 
    String hostname; 
    String source; 
    
    public ComparableTuple getKey() {
        return ComparableTuple.create(username, hostname, source);
    }
    
    public void setFromLine(String fromLine) {
        this.fromLine = fromLine;
        String fromCronPattern = "^From: ([a-z]+) \\(Cron Daemon\\)$";
        username = fromLine.replaceAll(fromCronPattern, "$1");
    }

    public void setSubjectLine(String subjectLine) {
        this.subjectLine = subjectLine;
        Matcher matcher = subjectCronPattern.matcher(subjectLine);
        if (matcher.find()) {
            username = matcher.group(1);
            hostname = matcher.group(2);
            source = matcher.group(3);
            subject = source;
        } else {
            subject = subjectLine.substring(9);
        }
    }

    public void setContentTypeLine(String contentTypeLine) {
        this.contentTypeLine = contentTypeLine;
        int index = contentTypeLine.indexOf(";");
        if (index > 14) {
            contentType = contentTypeLine.substring(14, index);
        } else {
            contentType = contentTypeLine.substring(14);
        }        
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }
    
    public List<String> getLineList() {
        return lineList;
    }
        
    @Override
    public String toString() {
        return Arrays.toString(new Object[] {username, hostname, source, subject, 
            contentType, lineList.size(), alertType});
    }
    
    public static CrumRecord parse(String text) throws IOException {
        CrumRecord record = new CrumRecord();
        boolean inHeader = true;
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            System.out.println(line);
            if (line.startsWith("From: ")) {
                record.setFromLine(line);
            } else if (line.startsWith("Subject: ")) {
                record.setSubjectLine(line);
            } else if (line.startsWith("Content-Type: ")) {
                record.setContentTypeLine(line);
            } else if (line.startsWith("Alert: ")) {
                record.parseAlertType(line.substring(7));
            } else if (!inHeader) {
                record.getLineList().add(line);
            } else if (line.length() == 0) {
                inHeader = false;
            }
        }
        return record;
    }
    
    private void parseAlertType(String string) {
        int index = string.indexOf(" ");
        if (index > 0) {
            alertString = string.substring(index + 1);
            string = string.substring(0, index);
        }
        try {
            alertType = AlertType.valueOf(string);
        } catch (Exception e) {
            logger.warn("parseAlertType {}: {}", string, e.getMessage());
        }
    }       
}
