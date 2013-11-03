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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 *
 * @author evan.summers
 */
public class TimestampedDequer<T extends Timestamped>  {
    long capacityMillis;
    long lastTimestamp;
    ArrayDeque<T> deque = new ArrayDeque();
    
    public TimestampedDequer(long capacityMillis) {
        this.capacityMillis = capacityMillis;
    }
    
    public synchronized void addLast(T element) {
        if (element.getTimestamp() == 0 || element.getTimestamp() < lastTimestamp) {
            deque.clear(); // throw our toys out the cot
        } else {
            lastTimestamp = element.getTimestamp();
            prune(lastTimestamp);
            deque.addLast(element);
        }
    }

    private void prune(long lastTimestamp) {
        while (deque.size() > 0 && 
                deque.getFirst().getTimestamp() <= lastTimestamp - capacityMillis) {
            deque.removeFirst();
        }
    }
    
    public synchronized Deque<T> snapshot(long lastTimestamp) {
        prune(lastTimestamp);
        return deque.clone();
    }
    
    public synchronized Deque<T> tail(int size) {
        Deque tail = new ArrayDeque();
        Iterator<T> it = deque.descendingIterator();
        for (int i = 0; i < size && it.hasNext(); i++) {
            tail.addFirst(it.next());
        }
        return tail;
    }
    
    public synchronized Deque<T> tailDescending(int size) {
        Deque tail = new ArrayDeque();
        Iterator<T> it = deque.descendingIterator();
        for (int i = 0; i < size && it.hasNext(); i++) {
            tail.addLast(it.next());
        }
        return tail;
    }
    
}
