/*
 Source https://code.google.com/p/vellum by @evanxsummers

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
package searchapp.util.storage;

import vellum.entity.*;

/**
 *
 * @author evan.summers
 */
public abstract class AbstractEntity implements Comparable<AbstractEntity> {

    public abstract Comparable getKey();
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractEntity) {
            AbstractEntity other = (AbstractEntity) obj;
            return Comparables.equals(getKey(), other.getKey());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public int compareTo(AbstractEntity o) {
        return Comparables.compareTo(getKey(), o.getKey());
    }
    
    @Override
    public String toString() {
        if (getKey() == null) return getClass().getSimpleName();
        return getKey().toString();
    }
        
}
