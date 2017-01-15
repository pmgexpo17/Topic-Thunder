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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.MessageAvailableConsumer;
import org.apache.activemq.MessageAvailableListener;
import org.pmg.jms.genbase.AbstractLifeCycle;
import org.pmg.jms.genconnect.SessionAgent;
import org.pmg.jms.genconnect.SessionProvider;

/**
 * ClientMember is a jms message consumer
 * @author Peter A McGill
 */
public class ClientMember extends AbstractLifeCycle implements Membership {

    private Destination destination;    
    private SessionAgent sessionAgent;
    private MessageAvailableConsumer consumer;
    protected final String className = getClass().getSimpleName();    
    protected String destName;    

    // descendent will inject the sessionProvider
    // guice doesn't allow throwing exceptions during injection
    // TO DO : register a container event handler for the error scenario    
    public ClientMember(SessionProvider sessionProvider) {
        
        try {
            this.sessionAgent = sessionProvider.get();
        }
        catch (JMSException ex) {
            LOG.error("Error creating session", ex);
        }
    }

    @Override
    public void createConsumer(String destinationName) throws JMSException {
        
        createConsumer(destinationName,null);
    }

    @Override
    public void createConsumer(String destinationName, String selector) 
                                                        throws JMSException {
        destName = destinationName;
        destination = sessionAgent.createDestination(destinationName);
        consumer = (MessageAvailableConsumer) 
                sessionAgent.getSession().createConsumer(destination, selector);            
    }

    @Override
    protected void doStop() throws JMSException, Exception
    {
        LOG.debug("[{}] is stopping",className);
        sessionAgent.stop();
        if (consumer != null)
           consumer.close();
    }

    @Override
    public String getDestinationName()
    {
        return destName;
    }    

    @Override
    public Session getSession() {

        return sessionAgent.getSession();
    }

    @Override
    public void setListener(MessageAvailableListener listener) {
        
        consumer.setAvailableListener(listener);
    }

    @Override
    public String getTransportName() {
            
        return sessionAgent.getTransportName();
    }

}
