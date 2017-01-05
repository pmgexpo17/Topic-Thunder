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
package org.pmg.jms.gendirector;

import javax.jms.JMSException;
import org.pmg.jms.genhandler.Handler;

/**
 * @param <X> Runs state machine (SM) behaviour for SM lifecycle iteration
 * @param <Y> Delivers JMS messages that propogate the state machine lifecycle
 * @author peter
 */
public interface ClientDirector<X extends Resolvar, Y extends Respondar>
                                                            extends Handler {
    
    public void init() throws JMSException;
}
