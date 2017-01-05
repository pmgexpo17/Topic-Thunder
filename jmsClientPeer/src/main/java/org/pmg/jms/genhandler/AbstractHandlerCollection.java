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
package org.pmg.jms.genhandler;

import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import org.pmg.jms.genbase.AbstractLifeCycle;
import org.pmg.jms.genclient.Routable;

/**
 * A handler collection stores all handlers for a ClientPeer application
 * A handler is referenced by routeId, which is a URI
 * eg routeId = /myapp/state/action/method
 * @author Peter A McGill
 */
public abstract class AbstractHandlerCollection extends AbstractLifeCycle 
                                        implements Handler, HandlerCollection {

    protected Map<String,Handler> handlers = new HashMap<>();
    protected Handler defaultHandler;
    protected String className = getClass().getSimpleName();

    @Override
    public abstract void handle(Routable delegate) throws JMSException;
        
    @Override
    public void setDefaultHandler(Handler handler) {

        defaultHandler = handler;
    }
    
    @Override
    public void addHandler(String routeId, Handler handler) {

        handlers.put(routeId, handler);
        if (!this.isStarted())
            return;
        try {
            handler.start();
        } catch (Exception ex) {
            LOG.error("[{}] handler failed to start",className,ex);
        }            
        handlers.put(routeId,handler);
    }

    @Override
    public Map<String,Handler> getHandlers() {
        
        return handlers;
    }
    
    @Override
    public Handler removeHandler(String routeId) {
        
        return handlers.remove(routeId);
    }

    @Override
    protected void doStart() throws Exception {
        
        LOG.debug("[{}] is starting",className);
        for(String key: handlers.keySet())
            handlers.get(key).start();
    }

    @Override
    protected void doStop() throws Exception {
        
        LOG.info("[{}] is stopping",className);    
        for(String key: handlers.keySet())
            handlers.get(key).stop();
    }

    @Override
    public void destroy() {
    
        for(String key: handlers.keySet())
            handlers.get(key).destroy();
    }       
    
}
