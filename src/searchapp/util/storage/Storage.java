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

import java.util.Collection;

/**
 * 
 * @author evan.summers
 */
public interface Storage<E extends AbstractEntity> {
    
    public void insert(E entity) throws StorageException;
    
    public void update(E entity) throws StorageException;

    public boolean containsKey(Comparable key);
    
    public void delete(Comparable key) throws StorageException;
    
    public E select(Comparable key);

    public Collection<E> selectCollection(String query);
    
}
