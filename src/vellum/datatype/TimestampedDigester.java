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
package vellum.datatype;

/**
 *
 * @author evan.summers
 */
public class TimestampedDigester<T extends Timestamped> {

    long millis = System.currentTimeMillis();
    long firstMillis;
    long lastMillis;
    long totalMillis;
    int sampleSize;

    public TimestampedDigester(long millis) {
        this.millis = millis;
    }

    public void digest(T timestamped) {
        if (firstMillis == 0 || firstMillis > timestamped.getTimestamp()) {
            firstMillis = timestamped.getTimestamp();
        }
        if (lastMillis == 0 || lastMillis < timestamped.getTimestamp()) {
            lastMillis = timestamped.getTimestamp();
        }
        totalMillis += millis - timestamped.getTimestamp();
        sampleSize++;
    }

    public long getFirstMillis() {
        return firstMillis;
    }

    public void setFirstMillis(long firstMillis) {
        this.firstMillis = firstMillis;
    }

    public long getLastMillis() {
        return lastMillis;
    }

    public void setLastMillis(long lastMillis) {
        this.lastMillis = lastMillis;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }
        
    public long getAverageMillis() {
        if (sampleSize == 0) {
            return 0;
        }
        return millis - totalMillis/sampleSize;
    }
}
