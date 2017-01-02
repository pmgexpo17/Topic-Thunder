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
package org.pmg.jms.gensudoku;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import javax.jms.JMSException;
import org.pmg.jms.genconfig.JmsClientScope;
import org.pmg.jms.genbase.AbstractLifeCycle;
import org.pmg.jms.genclient.Messenger;
import org.pmg.jms.genclient.ServicePeer;
import org.pmg.jms.genconnect.Connector;
import org.pmg.jms.genconnect.OpenWire;
import org.pmg.jms.gendirector.Controller;
import org.pmg.jms.gendirector.AppDirector;
import org.pmg.jms.gendirector.Statement;
import org.pmg.jms.gensudoku.director.ResolveUnitA1;
import org.pmg.jms.gensudoku.director.ResponseUnitA1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class SudokuPeer extends AbstractLifeCycle {
    
    private static final Logger LOG = LoggerFactory.getLogger(SudokuPeer.class);
    private final String className = getClass().getSimpleName();
    private final JmsClientScope scope;
    private ServicePeer clientPeer;
    private PeerFactory peerFactory;
        
    @Inject
    public SudokuPeer(@Named("JmsClientScoped") JmsClientScope scope,
                            @OpenWire Provider<Connector> connectPrvdr, 
                                Provider<Controller> controlPrvdr,
                                    Provider<Statement> stateBeanPrvdr) {
        
        this.scope = scope;
        this.scope.enter();
        this.scope.seed(Key.get(Connector.class, OpenWire.class),connectPrvdr.get());
        this.scope.seed(Key.get(Controller.class), controlPrvdr.get());
        this.scope.seed(Key.get(Statement.class), stateBeanPrvdr.get());
    }
    
    public void showAllRoutes() {
        
        clientPeer.getController().showAllRoutes();
    }
    
    @Inject
    public void set(PeerFactory peerFactory) {
        
        this.peerFactory = peerFactory;
    }

    @Override
    public void doStart() throws Exception {
        
        try {
            clientPeer.start();
        } catch(JMSException jex) {
            LOG.error("[{}] client peer start failed",className);
            throw new Exception(jex.getMessage());
        } finally {
            scope.exit();
        }
    }

    public void configure(String sessionId) throws JMSException {
    
        clientPeer = peerFactory.getClientPeer();

        // ResponseUnitA1 producer is configured internally
        AppDirector<ResolveUnitA1,ResponseUnitA1> director = 
                                        peerFactory.getGameDirector(sessionId);
        
        director.init();
        clientPeer.addBean(director);
        
        // messenger1 receives sudoku solution and trial monitor delivery
        Messenger<MessageProvider,GamePropogate> messenger1 = 
                                    peerFactory.getGameMessenger(sessionId);

        String selection = String.format("peer='%s'",sessionId);
        String topicName = "topic://PEER.SUDOKU/resolve/" + sessionId;        
        messenger1.init(topicName, selection);
        String route = "/director/resolve/reduce/" + sessionId;        
        messenger1.setRoute(route);        
        clientPeer.addBean(messenger1);
        clientPeer.addHandler(route,director);
        
        // messenger2 receives game start and stop notice from game server
        Messenger<MessageProvider,GamePropogate> messenger2 = 
                                    peerFactory.getGameMessenger(sessionId);

        String queueName = "queue://PEER.CONTROL/game/service/" + sessionId;
        messenger2.init(queueName, selection);
        route = "/director/game/service/" + sessionId;
        messenger2.setRoute(route);        
        clientPeer.addBean(messenger2);
        clientPeer.addHandler(route,director);

        LOG.info("[{}] is configured : {}",className,sessionId);
    }
}
