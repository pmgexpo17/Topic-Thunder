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

import com.google.inject.Provider;
import javax.jms.JMSException;

/**
 * Resolvar is a state machine component for JMS integration
 * Runs state machine (SM) behaviour, as a function of state.action, evaluating
 * ClientState.transition for SM lifeCycle iteration
 * @author peter
 */
public interface Resolvar extends Provider<Resolve> {

    public Resolve apply() throws JMSException;
}
