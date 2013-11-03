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

import java.util.HashMap;
import java.util.Map;
import vellum.type.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public class SimpleEntityCache implements EntityCache<Comparable> {
    Map<Comparable, Object> map = new HashMap();

    public static Comparable getComparable(Class type, Comparable id) {
        return new ComparableTuple(new Comparable[] {type.getName(), id});
    }
    
    public <V> void put(Comparable id, V value) {
        map.put(getComparable(value.getClass(), id), value);
    }
    
    public <V> V get(Class<V> type, Comparable id) {
        return (V) map.get(getComparable(type, id));
        
    }
}
