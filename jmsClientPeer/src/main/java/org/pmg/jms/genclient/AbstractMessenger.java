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
