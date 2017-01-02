/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.genclient;

import com.google.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

/**
 *
 * @author peter
 * @param <X>
 * @param <Y>
 */
public class MultiMessenger<X extends Membership, 
                    Y extends Deliverable> extends AbstractMessenger {
    
    private String baseRoute;
    private final Set<String> subRoutes = new HashSet<>();

    @Inject
    public MultiMessenger(X clientMember, Y deliverable) {
        
        super(clientMember, deliverable);
    }

    @Override
    public void setRoute(String baseRoute) {
        
        this.baseRoute = baseRoute;
    }

    @Override
    public void addSubRoute(String subRoute) {
        
        subRoutes.add(subRoute);
    }

    @Override
    public void onMessageAvailable(MessageConsumer consumer) {
        
        try {
            Message message = consumer.receive(10);
            // subRoute order is not important so a set is the best option
            for (String subRoute: subRoutes) {
                String route = baseRoute + "/" + subRoute;
                deliverable.setRoute(route);
                deliverable.getExecutor().execute(deliverable.handle(message));
            }
        } catch (JMSException ex) {
            LOG.error("[Messenger] message delivery failed",ex);
        }
    }           
}  
