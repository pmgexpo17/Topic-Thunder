/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.genclient;

import java.util.HashMap;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 *
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
