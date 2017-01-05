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

import com.google.inject.Inject;
import javax.jms.JMSException;
import org.pmg.jms.genclient.Routable;
import org.pmg.jms.genhandler.AbstractHandler;

/**
 * Director runs the state machine and handles ClientPeer lifeCycle
 * @param <X> Runs state machine (SM) behaviour for SM lifeCycle iteration
 * @param <Y> Delegates message delivery for companion SM lifeCycle iteration
 * @author peter
 */
public class AppDirector<X extends Resolvar, Y extends Respondar> 
                                            extends AbstractHandler 
                                                    implements ClientDirector {
    
    private Statement state;
    private final X resolver;
    private final Y responder;
    
    @Inject
    public AppDirector(X resolver, Y responder) {
        
        this.resolver = resolver;
        this.responder = responder;
    }

    @Override
    public void doStop() throws JMSException, Exception {
        
        responder.stop();
    }
    
    @Override
    public void init() throws JMSException {
        
        responder.configure();
        state.init("ready");
    }
    
    @Override
    public synchronized void handle(Routable delegate) throws JMSException {

        try {
            state.setDelegate(delegate);        
            if (resolver.apply().hasNext())
                responder.apply().respond();
            state.iterate();
        } catch(Exception ex) {
            LOG.error("[AppDirector] resolve exception",ex);
            throw new JMSException(ex.getMessage());
        }
    }    
    
    @Inject
    public void setClientState(Statement state) {
        
        this.state = state;
    }
}
