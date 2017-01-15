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

import java.util.HashMap;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.pmg.jms.genbase.AbstractLifeCycle;
import org.pmg.jms.genconnect.SessionAgent;
import org.pmg.jms.genconnect.SessionProvider;

/**
 * Provides multiple JMS destination capacity, where destKey is mapped to a JMS
 * producer for message delivery
 * @author Peter A McGill
 */
public class ClientResponder extends AbstractLifeCycle implements Responsive {

    private final HashMap<String,Respondar> pcache;
    private SessionAgent sessionAgent;
    protected final String className = getClass().getSimpleName();

    // descendent will inject the sessionProvider
    // guice doesn't allow throwing exceptions during injection
    // TO DO : register a container event handler for the error scenario
    public ClientResponder(SessionProvider sessionProvider) {

        try {
            sessionAgent = sessionProvider.get();
        }
        catch (JMSException ex) {
            LOG.error("Error creating session", ex);
        }
        pcache = new HashMap<>();
    }

    /**
     * In this case, the producer is coupled with a replyTo queue which is 
     * accepted on the first exchange with a peer device. setReplyToDevice 
     * enables storage of the couple for repeat message delivery
     * @param destKey - for pcache lookup
     * @throws javax.jms.JMSException
    */ 
    @Override
    public void createProducer(String destKey) throws JMSException {

        MessageProducer producer = 
                                sessionAgent.getSession().createProducer(null);
        Respondar responder = new Respondar(producer);
        pcache.put(destKey,responder);
    }

    /**
     * In this case, the destination is embedded in the producer so no need
     * to store it for later retrieval 
     * @param destKey - for pcache lookup
     * @param destinationName - jms queue creation param
     * @throws javax.jms.JMSException
    */ 
    @Override
    public void createProducer(String destKey,String destinationName)
                                                        throws JMSException {
        Destination destination = 
                                sessionAgent.createDestination(destinationName);
        MessageProducer producer = 
                        sessionAgent.getSession().createProducer(destination);
        Respondar responder = new Respondar(producer,destination);
        pcache.put(destKey,responder);
    }
    
    @Override
    public String getDestinationName(String destKey)
    {
        try {        
            Queue queue = 
                       (Queue) pcache.get(destKey).producer.getDestination();
            return queue.getQueueName();
        } catch (JMSException ex) {
            LOG.error("[{}] Error retrieving destination name",className,ex);
        }
        return "";
    }
    
    @Override
    public Session getSession() {

        return sessionAgent.getSession();
    }
    
    @Override
    protected void doStop() throws JMSException, Exception
    {
        LOG.debug("[{}] is stopping",className);
        sessionAgent.stop();
        for (String destName: pcache.keySet())
            pcache.get(destName).producer.close();
    }
    
    @Override
    public void setReplyToDest(String destKey, Destination destination) {
        
        if (!pcache.containsKey(destKey)) {
            LOG.error("[{}] replyTo destination key doesn't exist",className);
            return;
        }
        pcache.put(destKey,pcache.get(destKey).setReplyTo(destination));
    }

    @Override
    public void send(String destKey,Message message) throws JMSException {
        
        if (!pcache.containsKey(destKey))
            throw new JMSException("Destination key not found : " + destKey);
        Respondar responder = pcache.get(destKey);
        responder.producer.send(responder.destination,message);
    }

    @Override
    public void send(String destKey,Message message,int deliveryMode, 
                                               int priority, long timeToLive) 
                                                        throws JMSException {
        if (!pcache.containsKey(destKey))
            throw new JMSException("Destination key not found : " + destKey);
        Respondar responder = pcache.get(destKey);
        responder.producer.send(responder.destination,message,deliveryMode,
                                                        priority,timeToLive);
    }

    protected class Respondar {
        
        public MessageProducer producer;
        public Destination destination;
        
        public Respondar(MessageProducer producer) {
            
            this.producer = producer;
            destination = null; 
        }

        public Respondar(MessageProducer producer, Destination destination) {
            
            this.producer = producer;
            this.destination = destination; 
        }
        
        public Respondar setReplyTo(Destination destination) {
            
            this.destination = destination; 
            return this;
        }
    }
}
