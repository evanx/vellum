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
package vellum.type;

import vellum.util.Comparables;
import vellum.format.ArgFormats;

/**
 *
 * @author evan.summers
 */
public class ComparableTuple implements Comparable<ComparableTuple> {
    Comparable[] values;

    public ComparableTuple(Comparable[] values) {
        this.values = values;
    }
        
    @Override
    public int compareTo(ComparableTuple other) {
        return Comparables.compareTo(values, other.values);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComparableTuple) {
            return compareTo((ComparableTuple) obj) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Comparables.hashCode(values);
    }

    @Override
    public String toString() {
        return ArgFormats.formatter.format(values);
    }
    
    public static ComparableTuple create(Comparable ... values) {
        return new ComparableTuple(values);
    }   
}
