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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * ShutdownThread is a shutdown hook thread implemented as 
 * singleton that maintains a list of lifecycle instances
 * that are registered with it and provides ability to stop
 * these lifecycles upon shutdown of the Java Virtual Machine 
 * Sourced from https://github.com/eclipse/jetty.project 
 */
public class ShutdownThread extends Thread
{
	private static final Logger LOG = LoggerFactory.getLogger(ShutdownThread.class);
    private static final ShutdownThread _thread = new ShutdownThread();

    private boolean _hooked;
    private final List<LifeCycle> _lifeCycles = new CopyOnWriteArrayList<LifeCycle>();

    /* ------------------------------------------------------------ */
    /**
     * Default constructor for the singleton
     * 
     * Registers the instance as shutdown hook with the Java Runtime
     */
    private ShutdownThread()
    {
    }
    
    /* ------------------------------------------------------------ */
    private synchronized void hook()
    {
        try
        {
            if (_hooked)
                LOG.warn("shutdown hook already applied");
            else
                Runtime.getRuntime().addShutdownHook(this);
            _hooked=true;
        }
        catch(Exception e)
        {
            LOG.info("shutdown hook already applied");
        }
    }
    
    /* ------------------------------------------------------------ */
    private synchronized void unhook()
    {
        try
        {
            if (!_hooked)
                LOG.warn("shutdown already commenced");
            else
                Runtime.getRuntime().removeShutdownHook(this);
            _hooked=false;
        }
        catch(Exception e)
        {
            LOG.debug("shutdown already commenced");
        }
    }
    
    /* ------------------------------------------------------------ */
    /**
     * Returns the instance of the singleton
     * 
     * @return the singleton instance of the {@link ShutdownThread}
     */
    public static ShutdownThread getInstance()
    {
        return _thread;
    }

    /* ------------------------------------------------------------ */
    public static synchronized void register(LifeCycle... lifeCycles)
    {
        _thread._lifeCycles.addAll(Arrays.asList(lifeCycles));
        if (_thread._lifeCycles.size()>0)
            _thread.hook();
    }

    /* ------------------------------------------------------------ */
    public static synchronized void register(int index, LifeCycle... lifeCycles)
    {
        _thread._lifeCycles.addAll(index,Arrays.asList(lifeCycles));
        if (_thread._lifeCycles.size()>0)
            _thread.hook();
    }
    
    /* ------------------------------------------------------------ */
    public static synchronized void deregister(LifeCycle lifeCycle)
    {
        _thread._lifeCycles.remove(lifeCycle);
        if (_thread._lifeCycles.size()==0)
            _thread.unhook();
    }

    /* ------------------------------------------------------------ */
    public static synchronized boolean isRegistered(LifeCycle lifeCycle)
    {
        return _thread._lifeCycles.contains(lifeCycle);
    }

    /* ------------------------------------------------------------ */
    @Override
    public void run()
    {
        for (LifeCycle lifeCycle : _thread._lifeCycles)
        {
            try
            {
                if (lifeCycle.isStarted())
                {
                    lifeCycle.stop();
                    LOG.debug("Stopped {}",lifeCycle);
                }

                if (lifeCycle instanceof Destroyable)
                {
                    ((Destroyable)lifeCycle).destroy();
                    LOG.debug("Destroyed {}",lifeCycle);
                }
            }
            catch (Exception ex)
            {
                LOG.debug("[run]",ex);
            }
        }
    }
}
