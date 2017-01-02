/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.genclient;

import javax.jms.JMSException;
import org.pmg.jms.genbase.AbstractLifeCycle;

/**
 *
 * @author peter
 * @param <X>
 * @param <Y>
 */
public abstract class AbstractMessenger<X extends Membership, 
                    Y extends Deliverable> extends AbstractLifeCycle 
                                                    implements MessageRouter {
    
    protected final X clientMember;
    protected final Y deliverable;
    
    public AbstractMessenger(X clientMember, Y deliverable) {
        
        this.clientMember = clientMember;
        this.deliverable = deliverable;
    }
    
    @Override
    public void init(String destName, String selector) throws JMSException {
        
        clientMember.createConsumer(destName, selector);
    }
    
    @Override
    public void setRoute(String route) {
        
        deliverable.setRoute(route);
    }
    
    @Override
    public void addSubRoute(String subRoute) {}
    
    @Override
    public void doStart() {
        
        clientMember.setListener(this);
    }
    
    @Override
    public void doStop() throws JMSException, Exception {
        
        clientMember.stop();
    }
}
