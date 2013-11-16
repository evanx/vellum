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
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evan.summers
 */
public class TemporaryStorage<E extends AbstractEntity> implements Storage<E> {

    Logger logger = LoggerFactory.getLogger(TemporaryStorage.class);
    Map<Comparable, E> map = new HashMap();

    @Override
    public boolean insert(E entity) {
        logger.info("insert {} {}", entity.getKey(), map.containsKey(entity.getKey()));
        return map.put(entity.getKey(), entity) == null;
    }

    @Override
    public boolean update(E entity) {
        logger.info("update {} {}", entity.getKey(), map.containsKey(entity.getKey()));
        return map.put(entity.getKey(), entity) != null;
    }

    @Override
    public boolean delete(Comparable key) {
        logger.info("delete {} {}", key, map.containsKey(key));
        return map.remove(key) != null;
    }

    @Override
    public E select(Comparable key) {
        logger.info("delete {} {}", key, map.containsKey(key));
        return map.get(key);
    }

    @Override
    public Collection<E> selectCollection(String query) {
        return map.values();
    }
}
