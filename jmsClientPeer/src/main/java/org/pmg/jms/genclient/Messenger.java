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

import com.google.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

/**
 *
 * @author peter
 * @param <X> Real class ClientMember is a JMS message consumer
 * @param <Y> Handles message routing and invokes state machine iteration 
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
