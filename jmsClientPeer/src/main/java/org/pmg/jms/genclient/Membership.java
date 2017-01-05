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
import javax.jms.Session;
import org.apache.activemq.MessageAvailableListener;
import org.pmg.jms.genbase.LifeCycle;

/**
 * Membership is a JMS message consumer
 * @author Peter A McGill
 */

public interface Membership extends LifeCycle {

    public void createConsumer(String destinationName, String selector)
                                                            throws JMSException;
    public void createConsumer(String destinationName) throws JMSException;
    public String getDestinationName();
    public Session getSession();    
    public String getTransportName();
    public void setListener(MessageAvailableListener listener);
}
