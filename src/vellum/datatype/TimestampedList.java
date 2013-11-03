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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author evan.summers
 */
public class TimestampedList<T extends Timestamped>  {
    long capacityMillis;
    LinkedList<T> linkedList = new LinkedList();
    
    public TimestampedList(long capacityMillis) {
        this.capacityMillis = capacityMillis;
    }
    
    public int size() {
        return linkedList.size();
    }

    public synchronized void add(T element) {
        prune(element.getTimestamp());
        linkedList.add(0, element);
    }

    private void prune(long latestTimestamp) {
        if (latestTimestamp == 0) latestTimestamp = System.currentTimeMillis();
        while (linkedList.size() > 0) {
            T last = linkedList.getLast();
            if (last.getTimestamp() >= latestTimestamp - capacityMillis) {
                return;
            }
            linkedList.remove(last);
        }
    }

    public List<T> snapshot() {
        return getList(System.currentTimeMillis());
    }
    
    public synchronized List<T> getList(long millis) {
        prune(millis);
        return new ArrayList(linkedList);
    }

    public synchronized List<T> last(int size) {
        if (linkedList.size() <= size) {
            return new ArrayList(linkedList);
        } else {
            return new ArrayList(linkedList.subList(0, size));
        }
    }
}
