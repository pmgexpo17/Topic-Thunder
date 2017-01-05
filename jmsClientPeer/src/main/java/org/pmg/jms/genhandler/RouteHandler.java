/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.genhandler;

import javax.jms.JMSException;
import org.pmg.jms.genclient.Routable;

/**
 * A handler is a JMS application component mapped by routeId
 * The handler behaviour can be chained by changing the routeId which points
 * to the next handler
 * @author peter
 */
public class RouteHandler extends AbstractHandlerCollection {

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
