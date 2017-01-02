/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.genclient;

import com.google.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

/**
 *
 * @author peter
 * @param <X>
 * @param <Y>
 */
public class Messenger<X extends Membership, 
                    Y extends Deliverable> extends AbstractMessenger {
    
    @Inject
    public Messenger(X clientMember, Y deliverable) {
        
        super(clientMember, deliverable);
    }
    
    @Override
    public void onMessageAvailable(MessageConsumer consumer) {
        
        try {
            Message message = consumer.receive(10);
            deliverable.getExecutor().execute(deliverable.handle(message));
        } catch (JMSException ex) {
            LOG.error("[Messenger] message delivery failed",ex);
        }
    }           
}  
