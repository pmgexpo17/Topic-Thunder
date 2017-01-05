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
package org.pmg.jms.sudoku.genmodel;

import com.google.inject.Inject;
import java.util.concurrent.Executor;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import org.pmg.jms.genclient.Deliverable;
import org.pmg.jms.genclient.MapDelegate;
import org.pmg.jms.genclient.Routable;
import org.pmg.jms.gendirector.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles message routing and runs a Director deployed state machine iteration 
 * mapped by routeId
 * @author peter
 */
public class GamePropogate implements Deliverable {

    private static final Logger LOG = 
                                   LoggerFactory.getLogger(GamePropogate.class);
    private final String className = getClass().getSimpleName();
    private final Controller controller;
    private String routeId;
    
    @Inject
    public GamePropogate(Controller controller) {
        
        this.controller = controller;
    }

    @Override
    public Executor getExecutor() {
        
        return controller.getExecutor();
    }

    @Override
    public void setRoute(String routeId) {
        
        this.routeId = routeId;
    }
    
    
    @Override
    public Routable handle(Message message) throws JMSException {
        
        MapMessage mapMessage = (MapMessage) message;
        return new Delegate(mapMessage,routeId);
    }

    protected class Delegate extends MapDelegate {        

        public Delegate(MapMessage mapMessage, String routeId) {
            
            super(mapMessage, routeId);
        }

        @Override
        public void run() {

            controller.runApp(this);
        }
    }
}
