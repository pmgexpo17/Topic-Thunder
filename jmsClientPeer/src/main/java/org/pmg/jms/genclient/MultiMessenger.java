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
import java.util.HashSet;
import java.util.Set;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

/**
 * MultiMessenger saves creating a message listener for each route in a set
 * where the dependant state machine action requires group acknowledgement
 * @param <X> Real class ClientMember is a JMS message consumer
 * @param <Y> Delegates message routing and invokes state machine iteration
 * @author peter
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
