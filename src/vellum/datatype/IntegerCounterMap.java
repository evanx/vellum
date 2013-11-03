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

import java.util.LinkedList;
import java.util.TreeMap;

/**
 *
 * @author evan.summers
 */
public class IntegerCounterMap<K> extends TreeMap<K, Integer> {


    private int sum = 0;
    private Integer minimumValue = null;
    private Integer maximumValue = null;
    private Integer defaultValue = new Integer(0);

    public IntegerCounterMap() {
    }

    public IntegerCounterMap(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public Integer get(Object key) {
        Integer value = super.get(key);
        if (value == null) return defaultValue;
        return value;
    }

    @Override
    public Integer put(K key, Integer value) {
        if (minimumValue == null || value < minimumValue) {
            minimumValue = value;
        }
        if (maximumValue == null || value > maximumValue) {
            maximumValue = value;
        }
        return super.put(key, value);
    }

    public int getInt(K key, int defaultValue) {
        if (!containsKey(key)) {
            return defaultValue;
        } else {
            return get(key);
        }
    }

    public int getInt(K key) {
        return getInt(key, 0);
    }

    public void add(K key, int augend) {
        sum += augend;
        int value = getInt(key) + augend;
        put(key, value);
    }

    public void increment(K key) {
        add(key, 1);
    }

    public int getTotalCount() {
        return sum;
    }

    public Integer getMinimumValue() {
        return minimumValue;
    }

    public Integer getMaximumValue() {
        return maximumValue;
    }

    public Integer findMinimumValue() {
        return Maps.getMinimumValue(this);
    }

    public Integer findMaximumValue() {
        return Maps.getMaximumValue(this);
    }

    public int calculateTotalCount() {
        int total = 0;
        for (K key : keySet()) {
            total += getInt(key);
        }
        return total;
    }

    public LinkedList<K> descendingValueKeys() {
        return Maps.descendingValueKeys(this);
    }
}
