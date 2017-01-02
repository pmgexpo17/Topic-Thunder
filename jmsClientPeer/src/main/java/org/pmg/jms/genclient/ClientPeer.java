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
package org.pmg.jms.genclient;

import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import static org.pmg.jms.genbase.AbstractLifeCycle.STARTED;
import org.pmg.jms.genbase.ContainerLifeCycle;
import org.pmg.jms.genbase.LifeCycle;
import org.pmg.jms.genbase.MultiException;
import org.pmg.jms.genbase.ShutdownThread;
import org.pmg.jms.genbase.Uptime;
import org.pmg.jms.genconnect.Connector;
import org.pmg.jms.genconnect.OpenWire;
import org.pmg.jms.gendirector.Controller;
import org.pmg.jms.genhandler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClientPeer is a jms component container, which handles lifecycle creation
 * and completion. 
 * @author Peter A McGill
 */

public class ClientPeer extends ContainerLifeCycle implements ServicePeer {

    private static final Logger LOG = LoggerFactory.getLogger(ClientPeer.class);
    private final String className = getClass().getSimpleName();
    protected Controller controller;
    protected final Map<String,Connector> connectors = new HashMap<>();    

    @Inject
    public ClientPeer(Connector connector, Controller controller) {

        String transportName = connector.getTransportName().toUpperCase();
        connectors.put(transportName,connector);        
        this.controller = controller;
        addBean(controller);        
    }

    @Override
    public Connector getConnector(String transportName) {
        
        return connectors.get(transportName.toUpperCase());
    }


    @Override
    public Controller getController() {
        
        return controller; 
    }

    @Override
    public void addHandler(String route, Handler handler) {
        
        if (isStarted())
            throw new IllegalStateException(STARTED);
        
        controller.getRouter().addHandler(route,handler);
    }

    @Override
    protected void doStart() throws Exception
    {        
        //If the Server should be stopped when the jvm exits, register
        //with the shutdown handler thread.
        ShutdownThread.register((LifeCycle) this);

        LOG.info("ClientPeer : " + getVersion());
        
        MultiException mex = new MultiException();
        
        try
        {
            super.doStart();
        }
        catch(Throwable ex)
        {
            mex.add(ex);
        }

        for(String key: connectors.keySet())
            connectors.get(key).start();

        mex.ifExceptionThrow();

        LOG.info("[ClientPeer] Started @{}ms",Uptime.getUptime());
    }

    @Override
    protected void doStop() throws Exception
    {
        MultiException mex=new MultiException();
        
        // And finally stop everything else
        try
        {
            super.doStop();
        }
        catch (Throwable e)
        {
            mex.add(e);
        }

        for(String key: connectors.keySet())
            connectors.get(key).stop();

        ShutdownThread.deregister(this);
        
        //controller.stop();
                
        mex.ifExceptionThrow();
        
        LOG.info("[ClientPeer] Stopped @{}ms",Uptime.getUptime());
    }

    @Override
    public void destroy()
    {
        if (!isStopped())
            throw new IllegalStateException("!STOPPED");
        super.destroy();
    }

         /**
     * Set a graceful stop time.
     * The {@link StatisticsHandler} must be configured so that open connections can
     * be tracked for a graceful shutdown.
     * @param stopTimeout
     * @see org.eclipse.jetty.util.component.ContainerLifeCycle#setStopTimeout(long)
     */
    @Override
    public void setStopTimeout(long stopTimeout)
    {
        super.setStopTimeout(stopTimeout);
    }

    public static String getVersion()
    {
        return "2.0";
    }
}
