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
package org.pmg.jms.gensudoku.server;

import javax.jms.JMSException;
import org.pmg.jms.genclient.Routable;
import org.pmg.jms.genhandler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class ResetResponder extends AbstractHandler {
 
    private static final Logger LOG = 
                                LoggerFactory.getLogger(ResetResponder.class);
    private GameService gameServer;

    public void setGameServer(GameService gameServer) {

        this.gameServer = gameServer;
    }

    @Override
    public void handle(Routable delegate) throws JMSException {

        synchronized (delegate) {
            String action = delegate.getString("action");            
            switch (action) {
                case "reset" :         
                    String sessionId = delegate.getString("sessionId");
                    gameServer.release(sessionId);
                    break;                    
                default : break;
            }
            delegate.setStatus(true);
        }
    }   
}
