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
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class TemporaryStorage<E extends AbstractEntity> implements Storage<E> {

    Logger logger = LoggerFactory.getLogger(TemporaryStorage.class);
    Map<Comparable, E> map = new TreeMap();

    @Override
    public void insert(E entity) throws StorageException {
        logger.info("insert {} {}", entity.getKey(), !map.containsKey(entity.getKey()));
        if (map.put(entity.getKey(), entity) != null) {
            throw new StorageException(StorageExceptionType.ALREADY_EXISTS, entity.getKey());
        }
    }

    @Override
    public void update(E entity) throws StorageException {
        logger.info("update {} {}", entity.getKey(), map.containsKey(entity.getKey()));
        if (map.put(entity.getKey(), entity) == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, entity.getKey());            
        }
    }

    @Override
    public boolean containsKey(Comparable key) {
        logger.info("containsKey {}", key, map.containsKey(key));
        return map.containsKey(key);
    }
    
    @Override
    public void delete(Comparable key) throws StorageException {
        logger.info("delete {} {}", key, map.containsKey(key));
        if (map.remove(key) != null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, key);           
        }
    }

    @Override
    public E select(Comparable key) {
        logger.info("select {} {}", key, map.containsKey(key));
        if (!map.containsKey(key)) {
        }
        return map.get(key);
    }

    @Override
    public Collection<E> selectCollection(String query) {
        return map.values();
    }
}
