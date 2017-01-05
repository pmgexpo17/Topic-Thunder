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
import javax.jms.JMSException;

/**
 * Injected OpenWire connector and session parameter components provide
 * an OpenWire sessionAgent
 * @author Peter A McGill
 */
public class OpenWireSessionPrvdr implements SessionProvider {

    private final Connector connector;
    private final Boolean transacted;
    private final int acknowledgeMode;

    @Inject
    public OpenWireSessionPrvdr(@OpenWire Connector connector, 
                                                      SessionConfig config) {
        
        this.connector = connector;
        this.transacted = config.transacted;
        this.acknowledgeMode = config.acknowledgeMode;
    }

    @Override
    public SessionAgent get() throws JMSException {
        
        return connector.createSession(transacted, acknowledgeMode);
    }
}
