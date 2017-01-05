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
import javax.jms.TextMessage;

/**
 * Routable class that wraps a TextMessage
 * @author peter
 */
public abstract class TextDelegate implements Routable  {        
    
    private final TextMessage message;
    private final HashMap<String,Object> appData = new HashMap<>(3);
    private Boolean handled = false;
    private String routeId;
        
    public TextDelegate(TextMessage textMessage, String routeId) {
                        
        this.message = textMessage;
        this.routeId = routeId;
    }

    @Override
    public boolean containsKey(String key) throws JMSException {
        
        return appData.containsKey(key) || message.propertyExists(key);
    }

    @Override
    public Destination getJMSReplyTo() throws JMSException {

        return message.getJMSReplyTo();
    }

    @Override
    public Object getObject(String key) throws JMSException {

        if (appData.containsKey(key))
            return appData.get(key);
        return message.getObjectProperty(key);
    }

    @Override
    public int getInt(String key) throws JMSException {

        if (appData.containsKey(key))
            return (int) appData.get(key);
        return message.getIntProperty(key);
    }

    @Override
    public String getString(String key) throws JMSException {

        if (appData.containsKey(key))
            return (String) appData.get(key);
        return message.getStringProperty(key);
    }

    @Override
    public Boolean isHandled() {
         
        return handled;
    }

    @Override
    public void setObject(String key, Object value) {
            
        appData.put(key,value);
    }

    @Override
    public void setInt(String key, int value) {
        
        appData.put(key,value);
    }

    @Override
    public void setString(String key, String value) {

        appData.put(key,value);
    }

    @Override
    public void setStatus(boolean handled) {
            
        this.handled = handled;
    }
        
    @Override
    public String getRoute() {
            
        return routeId;
    }

    @Override
    public void setRoute(String routeId) {
        
        this.routeId = routeId;
    }
}
