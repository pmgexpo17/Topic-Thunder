/**
 * Copyright (c) 2016 Peter A McGill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
**/
package org.pmg.jms.genbase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An Uptime class that is compatible with Android, GAE and the new Java 8
 * compact profiles
 * Sourced from https://github.com/eclipse/jetty.project 
 */
public class Uptime {
    
    public static final int NOIMPL = -1;

    public static interface Impl {
        
        public long getUptime();
    }

    public static class DefaultImpl implements Impl {
        
        public Object mxBean;
        public Method uptimeMethod;

        public DefaultImpl() {
            
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                String klassName = "java.lang.management.ManagementFactory";
                Class<?> mgmtFactory = Class.forName(klassName,true,cl);
                klassName = "java.lang.management.RuntimeMXBean";
                Class<?> runtimeClass = Class.forName(klassName,true,cl);
                Class<?> noparams[] = new Class<?>[0];
                Method mxBeanMethod = 
                            mgmtFactory.getMethod("getRuntimeMXBean",noparams);
                if (mxBeanMethod == null) {
                    String lognote = "method getRuntimeMXBean() not found";
                    throw new UnsupportedOperationException(lognote);
                }
                mxBean = mxBeanMethod.invoke(mgmtFactory);
                if (mxBean == null) {
                    String lognote = "getRuntimeMXBean() method returned null";
                    throw new UnsupportedOperationException(lognote);
                }
                uptimeMethod = runtimeClass.getMethod("getUptime",noparams);
                if (mxBean == null) {
                    String lognote = "method getUptime() not found";
                    throw new UnsupportedOperationException(lognote);
                }
            }
            catch (ClassNotFoundException | 
                   NoClassDefFoundError | 
                   NoSuchMethodException | 
                   SecurityException | 
                   IllegalAccessException | 
                   IllegalArgumentException | 
                   InvocationTargetException e) {
                String lognote = 
                            "Implementation not available in this environment";
                throw new UnsupportedOperationException(lognote,e);
            }
        }

        @Override
        public long getUptime() {
            
            try {
                return (long)uptimeMethod.invoke(mxBean);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                return NOIMPL;
            }
        }
    }

    private static final Uptime INSTANCE = new Uptime();

    public static Uptime getInstance() {
        
        return INSTANCE;
    }

    private Impl impl;

    private Uptime() {
        
        try {
            impl = new DefaultImpl();
        }
        catch (UnsupportedOperationException e) {
            
            String lognote = "Defaulting Uptime to NOIMPL due to (%s) %s%n";
            System.err.printf(lognote,e.getClass().getName(),e.getMessage());
            impl = null;
        }
    }

    public Impl getImpl() {
        
        return impl;
    }

    public void setImpl(Impl impl) {
        
        this.impl = impl;
    }

    public static long getUptime() {
        
        Uptime u = getInstance();
        if (u == null || u.impl == null) {
            return NOIMPL;
        }
        return u.impl.getUptime();
    }
}
