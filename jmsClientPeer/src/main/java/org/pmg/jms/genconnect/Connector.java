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

import javax.jms.JMSException;
import org.pmg.jms.genbase.LifeCycle;

/**
 * Connector lifeCycle interface which is a sessionAgent provider component  
 * (see OpenWireSessionPrvdr) SessionAgent is a decorated session with transport
 * specific convenience methods for queue and topic creation
 * @author Peter A McGill
 */
public interface Connector extends LifeCycle {
       
    public SessionAgent createSession(Boolean transacted, int acknowledgeMode)
                                                            throws JMSException;
    public String getTransportName();
}
