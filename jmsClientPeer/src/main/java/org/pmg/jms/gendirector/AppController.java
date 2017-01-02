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
package org.pmg.jms.gendirector;

import org.pmg.jms.genhandler.RouteHandler;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import org.pmg.jms.genconfig.JmsClientScoped;
import org.pmg.jms.genbase.AbstractLifeCycle;
import org.pmg.jms.genclient.Routable;
import org.pmg.jms.genhandler.HandlerCollection;

/**
 * AppController is the engine for ClientPeer runnable task execution
 * Add a local Guice binding to define ThreadSize in <YourAppModule>.configure
 * RouteHandler resolves a handler instance by lookup using the associated
 * Delegate.routeId
 * @author Peter A McGill
 */
@JmsClientScoped
public class AppController extends AbstractLifeCycle 
                                            implements Controller {

    private final String className;
    private final ThreadPoolExecutor threadpool;
    private final RouteHandler router;
    private int errorCode = 0;
        
    @Inject 
    public AppController(@Named("ThreadSize") int threadPoolSize, 
                                                        RouteHandler router) {
        
       className = getClass().getSimpleName();
       threadpool = (ThreadPoolExecutor) 
                                Executors.newFixedThreadPool(threadPoolSize);
       this.router = router;
       System.out.println("[AppController] created : " + toString());       
    }
    
    @Override
    public HandlerCollection getRouter() {
        
        return router;
    }

    @Override
    public Executor getExecutor() {
        
        return (Executor) threadpool;
    }

    @Override
    public boolean isStarted() {
        
        if (super.isStarted())
            return true;
        if (errorCode == 1)
            LOG.error("[{}] error caught, stopped ",className);
        else
            LOG.error("[{}] not started ",className);
        return false;
    }
    
    @Override
    public void runApp(Routable delegate) {
        
        try {
            if (isStarted())
                router.handle(delegate);
        } catch (JMSException jex) {
            LOG.error("[{}] director exception : {}",className,
                                                        delegate.getRoute(),jex);
            errorStop();
        }
    }
    
    public void errorStop() {
        
        try {
            errorCode = 1;
            this.stop();
        } catch (Exception ex) {
            LOG.error("[{}] failed to stop",className,ex);
        }
    }

    @Override
    public void doStart() throws Exception {

        LOG.info("[{}] starting ... ",className);
        router.start();
    }

    @Override
    public void doStop() throws Exception {

        LOG.info("[{}] stopping ... ",className);
        threadpool.shutdown();
        router.stop();
    }
    
    @Override
    public void destroy() {
    
        threadpool.shutdown();
    };

    @Override
    public void showAllRoutes() {
        
        LOG.info("[RouteHandler] All routes :");
        int count = 1;
        for(String key: router.getHandlers().keySet())
        {
            LOG.info(String.format("\troute[%d] : {}",count++),key);
        }    
    }
}
