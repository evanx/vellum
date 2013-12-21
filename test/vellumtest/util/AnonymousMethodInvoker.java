/*
 * Source https://github.com/evanx by @evanxsummers

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. The ASF licenses this file to
 you under the Apache License, Version 2.0 (the "License").
 You may not use this file except in compliance with the
 License. You may obtain a copy of the License at:

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.  
       
 */
package vellumtest.util;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;

/**
 *
 * @author evan
 */
public class AnonymousMethodInvoker<T> {

    private final static Logger logger = Logger.getLogger(AnonymousMethodInvoker.class);

    private Method method; 
    private Object target;
    private Object[] args;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    public AnonymousMethodInvoker(Object target, Object ... args) throws Exception {
        Method[] methods = target.getClass().getDeclaredMethods();
        if (methods.length > 1) {
            throw new Exception("Invocable object must have single method");
        }
        this.method = methods[0];
        this.target = target;
        this.args = args;
    }

    public T invoke() throws Exception {
        return (T) method.invoke(target, args);
    }
    
    public void run() throws Exception {
        method.invoke(target, args);
    }

    public Future<T> start() throws Exception {
        return executorService.submit(new Callable() {

            @Override
            public T call() throws Exception {
                try {
                    return (T) method.invoke(target, args);
                } catch (Exception e) {
                    logger.warn(e.getCause().getMessage());
                    throw e;
                } finally {
                    executorService.shutdown();
                }
            }
        });
    }
}
