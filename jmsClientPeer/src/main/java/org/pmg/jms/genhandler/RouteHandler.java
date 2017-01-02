/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.genhandler;

import javax.jms.JMSException;
import org.pmg.jms.genclient.Routable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class RouteHandler extends AbstractHandlerCollection {

    private static final Logger LOG = LoggerFactory.getLogger(RouteHandler.class);
    
    @Override
    public void handle(Routable delegate) throws JMSException {

        String routeId = delegate.getRoute();
        if (handlers.get(routeId) == null) {
            if (defaultHandler == null) {
                LOG.warn("[{}] Default handler is not set. Route not found : {}"
                                                            ,className,routeId);
                throw new JMSException("Route not found : " + routeId);
            }
            LOG.debug("[{}] Default handling for routeId : {}",className,
                                                                    routeId);
            defaultHandler.handle(delegate);
            return;
        }
        handlers.get(routeId).handle(delegate);
        if (!delegate.isHandled() && !delegate.getRoute().equals(routeId)) {
            String altRoute = delegate.getRoute();
            LOG.info("[{}] alternate routeId : {}",className,altRoute);
            handlers.get(altRoute).handle(delegate);
        }
    }
    
    public void showAllRoutes() {
        
        LOG.info("[{}] All routes ",className);
        int count = 1;
        for(String key: handlers.keySet())
        {
            LOG.info(String.format("\troute[%d] : {}",count++),key);
        }    
    }
}
