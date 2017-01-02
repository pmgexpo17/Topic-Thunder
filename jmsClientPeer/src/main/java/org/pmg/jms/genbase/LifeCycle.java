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

import java.util.EventListener;

/**
 * The lifecycle interface for generic components.
 * Sourced from https://github.com/eclipse/jetty.project 
 */
public interface LifeCycle
{
    public void start() throws Exception;
    public void stop() throws Exception;
    public boolean isRunning();
    public boolean isStarted();
    public boolean isStarting();
    public boolean isStopping();
    public boolean isStopped();
    public boolean isFailed();
    public void addLifeCycleListener(LifeCycle.Listener listener);
    public void removeLifeCycleListener(LifeCycle.Listener listener);
    
    /** 
     * A listener for Lifecycle events.
     */
    public interface Listener extends EventListener
    {
        public void lifeCycleStarting(LifeCycle event);
        public void lifeCycleStarted(LifeCycle event);
        public void lifeCycleFailure(LifeCycle event,Throwable cause);
        public void lifeCycleStopping(LifeCycle event);
        public void lifeCycleStopped(LifeCycle event);
    }
}
