/*
 * Source https://github.com/evanx by @evanxsummers

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
package dualcontrol;

import vellum.util.ExtendedProperties;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author evan.summers
 */
public class DualControlPassphraseVerifier {

    private final static Logger logger = Logger.getLogger(DualControlPassphraseVerifier.class);
    private final boolean verifyPassphrase;
    private final boolean verifyPassphraseComplexity;
    private final int minPassphraseLength;
    private final int minWordCount;

    public DualControlPassphraseVerifier(Properties properties) {
        ExtendedProperties props = new ExtendedProperties(properties);
        verifyPassphrase = props.getBoolean(
                "dualcontrol.verifyPassphrase", true);
        verifyPassphraseComplexity = props.getBoolean(
                "dualcontrol.verifyPassphraseComplexity", true);
        minPassphraseLength = props.getInt(
                "dualcontrol.minPassphraseLength", 12);
        minWordCount = props.getInt(
                "dualcontrol.minWordCount", 4);
    }

    public String getInvalidMessage(char[] password) {
        if (verifyPassphrase) {
            if (password.length < minPassphraseLength) {
                return "Passphrase too short";
            }
            if (countWords(password) < minWordCount) {
                return "Too few words in passphrase";
            }
            if (verifyPassphraseComplexity) {
                if (!containsUpperCase(password) || !containsLowerCase(password)
                        || !containsDigit(password) || !containsPunctuation(password)) {
                    return "Insufficient password complexity";
                }
            }
        }
        return null;
    }

    public int countWords(char[] password) {
        int count = 0;
        for (char ch : password) {
            if (ch == ' ') {
                count++;
            }
        }
        return count;
    }

    public static boolean containsDigit(char[] array) {
        for (char ch : array) {
            if (Character.isDigit(ch)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsPunctuation(char[] array) {
        for (char ch : array) {
            if (!Character.isWhitespace(ch) && !Character.isLetterOrDigit(ch)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsLetter(char[] array) {
        for (char ch : array) {
            if (Character.isLetter(ch)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsUpperCase(char[] array) {
        for (char ch : array) {
            if (Character.isUpperCase(ch)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsLowerCase(char[] array) {
        for (char ch : array) {
            if (Character.isLowerCase(ch)) {
                return true;
            }
        }
        return false;
    }
}
