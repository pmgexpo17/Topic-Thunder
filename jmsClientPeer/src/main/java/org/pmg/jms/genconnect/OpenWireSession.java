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
package org.pmg.jms.genconnect;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import org.pmg.jms.genbase.AbstractLifeCycle;

/**
 * A decorated session with convenience methods for OpenWire queue and topic
 * creation. Create messages by using the session directly
 * @author Peter A McGill
 */
public class OpenWireSession extends AbstractLifeCycle implements SessionAgent {

    private final Session session;    

    public OpenWireSession(Session session) {

        this.session = session;
    }

    @Override
    public Destination createDestination(String destinationName) 
                                                        throws JMSException {

        String[] destPart = destinationName.split("//");
        if (destPart[0].toUpperCase().equals("QUEUE:"))
            return session.createQueue(destPart[1]);
        else if (destPart[0].toUpperCase().equals("TOPIC:"))
            return session.createTopic(destPart[1]);
        String errText = "Destination name is invalid : %s";
        throw new JMSException(String.format(errText,destinationName));
    }

    @Override
    public Queue createQueue(String queueName) throws JMSException {

        return session.createQueue(queueName);
    }

    @Override
    public Topic createTopic(String topicName) throws JMSException {
    
        return session.createTopic(topicName);                
    }

    @Override
    protected void doStop() {

        try {
            LOG.info("OpenWire session is closing");
            session.close();
        }
        catch (JMSException ex) {
            LOG.error("Error stopping OpenWire session", ex);
        }
    }

    @Override
    public Session getSession() {

        return session;
    }

    @Override
    public String getTransportName() {
        
        return "OpenWire";
    }
}
