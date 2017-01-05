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
