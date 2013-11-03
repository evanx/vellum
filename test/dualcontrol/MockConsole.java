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
package dualcontrol;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author evan.summers
 */
public class MockConsole implements MockableConsole {
    private static Logger logger = Logger.getLogger(MockConsole.class);
    private List<String> lines = new ArrayList();
    private char[] password;
    String alias;
    
    public MockConsole(String alias, char[] password) {
        this.alias = alias;
        this.password = password;
        logger = Logger.getLogger("MockConsole-" + alias);
    }

    public List<String> getLines() {
        return lines;
    }

    public String getLine(int index) {
        if (index < lines.size()) {
            return lines.get(index);
        }
        return "";
    }
    
    @Override
    public char[] readPassword(String prompt, Object ... args) {
        prompt = String.format(prompt, args);
        logger.info(prompt);
        lines.add(prompt);
        return password.clone();
    }
    
    @Override
    public void println(String message) {
        logger.info(message);
        lines.add(message);
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            logger.error("usage: alias password");
        } else {
            MockConsole console = new MockConsole(args[0], args[1].toCharArray());
            DualControlConsole instance = new DualControlConsole(
                    System.getProperties(), console);
            instance.init();
            instance.call();
            logger.info(console.getLines().get(0));
        }
    }
    
}
