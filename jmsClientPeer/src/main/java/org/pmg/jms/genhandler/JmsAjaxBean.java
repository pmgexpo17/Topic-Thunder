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
package org.pmg.jms.genhandler;

import java.util.HashMap;

/**
 * For ClientPeer webapp integration. A web client delivered XML text message is 
 * converted to a JmsAjaxBean for delivery to a JMS consumer by a JMS servlet  
 * extension, see https://github.com/gilmarie/TopicThunder
 * @author Peter A McGill
 */

public class JmsAjaxBean {
    
    public String from = "";
    public String action = "";
    public String method = "";    
    public String destName = "";
    public String state = "";
    public HashMap<String,String> message = null;

    /**
         * @return the message action
     */
    public String getAction() {
	return action;
    }
    /**
         * @param action the message action to set
     */
    public void setAction(String action) {
	this.action = action;
    }
    /**
         * @return the message sender
     */
    public String getFrom() {
	return from;
    }
    /**
         * @param name : the message sender 
     */
    public void setFrom(String name) {
	from = name;
    }
    /**
         * @return the message method
     */
    public String getMethod() {
	return method;
    }
     /**
         * @param method the message method to set
     */
    public void setMethod(String method) {
	this.method = method;
    }
    
    public String getDestName() {
	return destName;
    }
    /**
         * @param destName the message destination to set
     */
    public void setDestName(String destName) {
	this.destName = destName;
    }

    public String getState() {
	return state;
    }
    /**
         * @param state the message selector to set
     */
    public void setState(String state) {
	this.state = state;
    }

    /**
         * @return the message
     */
    public HashMap<String,String> getMessage() {
	return message;
    }
    /**
         * @param message the message to set
     */
    public void setMessage(HashMap<String,String> message) {
	this.message = message;
    }
    
    
	
    @Override
    public String toString() {
    	return new StringBuilder()
        .append("[from:").append(from)
	.append(",action:").append(action)
        .append(",method:").append(method)
        .append(",destName:").append(destName)
        .append(",state:").append(state)
	.append(",message:").append(message.toString())
        .append("]")
	.toString();
    }
}