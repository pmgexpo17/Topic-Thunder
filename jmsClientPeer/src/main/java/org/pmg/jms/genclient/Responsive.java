/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.genclient;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.pmg.jms.genbase.LifeCycle;

/**
 *
 * @author peter
 */
public interface Responsive extends LifeCycle {
    
    public void createProducer(String destKey) throws JMSException;
    public void createProducer(String destKey, String destinationName)
                                                            throws JMSException;
    public String getDestinationName(String destKey);
    public Session getSession();    
    public void send(String destKey,Message message) throws JMSException;
    public void send(String destKey,Message message,int deliveryMode, 
                                               int priority, long timeToLive) 
                                                        throws JMSException;
    public void setReplyToDest(String destKey, Destination destination);    
}
