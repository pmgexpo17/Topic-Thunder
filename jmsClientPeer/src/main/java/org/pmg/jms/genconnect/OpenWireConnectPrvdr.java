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

import com.google.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Guiced Jms Connection provider for OpenWire transport
 * @author Peter A McGill
 */
public class OpenWireConnectPrvdr implements ConnectionProvider {
    
    private final ConnectionFactory connectionFactory;
    private final ActiveMQConfig config;
    
    @Inject
    public OpenWireConnectPrvdr(ActiveMQConfig config) {
        
        this.config = config;
        connectionFactory =  
            new ActiveMQConnectionFactory(config.user,config.pwd,config.url);
    }
        
    @Override
    public Connection get() throws JMSException {
        
        return connectionFactory.createConnection(config.user,config.pwd);
    }
}
