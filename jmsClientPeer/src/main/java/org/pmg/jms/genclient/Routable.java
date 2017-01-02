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

/**
 - The subject of AppController execution when extended with Runnable
 - Routable defines ClientPeer message exchange protocol
 - Received message is immutable, ie, read-only
   @author Peter A McGill
 */
public interface Routable extends Runnable {

    public boolean containsKey(String key) throws JMSException;
    
    public Destination getJMSReplyTo() throws JMSException;
    
    public Object getObject(String key) throws JMSException;
    
    public int getInt(String key) throws JMSException;
    
    public String getString(String key) throws JMSException;
    
    public void setObject(String key, Object value);
    
    public void setInt(String key, int value);
    
    public void setString(String key, String value);
    
    public void setStatus(boolean handled);

    public Boolean isHandled();
    
    public String getRoute();
    
    public void setRoute(String routeId);    
}
