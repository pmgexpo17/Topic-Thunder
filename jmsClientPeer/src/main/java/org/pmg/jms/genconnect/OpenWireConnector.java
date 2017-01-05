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
import javax.jms.JMSException;
import javax.jms.Session;
import org.pmg.jms.genconfig.JmsClientScoped;
import org.pmg.jms.genbase.AbstractLifeCycle;

/**
 * OpenWire Connector is a sessionAgent provider component. (see
 * OpenWireSessionPrvdr) SessionAgent is a decorated session with transport 
 * specific convenience methods for queue and topic creation
 * @author peter
 */
@JmsClientScoped
public class OpenWireConnector extends AbstractLifeCycle implements Connector {
    
    private Connection connection;
    private final String transportName = "OpenWire";
    
    @Inject
    public OpenWireConnector(@OpenWire 
                                    ConnectionProvider<Connection> provider) {
        
        try {
            connection = provider.get();
        }
        catch (JMSException ex) {
            LOG.error("Error creating connection", ex);
        }
        System.out.println("[OpenWireConnector] created : " + toString());       
    }
    
    @Override
    public SessionAgent createSession(Boolean transacted, int acknowledgeMode)
                                                          throws JMSException {

        Session session = connection.createSession(transacted, acknowledgeMode);
        return new OpenWireSession(session);
    }

    @Override
    protected void doStart() {

        try {
            LOG.info("OpenWire Connector is starting");
            connection.start();
        }
        catch (JMSException ex) {
            LOG.error("Error starting OpenWire Connector", ex);
        }
    }

    @Override
    protected void doStop() {

        try {
            LOG.info("OpenWire Connector is stopping");
            connection.stop();
        }
        catch (JMSException ex) {
            LOG.error("Error stopping OpenWire Connector", ex);
        }
    }
     
    @Override
    public String getTransportName() {
        
        return transportName;
    }        
}
