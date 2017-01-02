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
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import java.util.Iterator;
import javax.jms.JMSException;
import org.pmg.jms.genbase.AbstractLifeCycle;
import org.pmg.jms.genclient.Messenger;
import org.pmg.jms.genclient.MultiMessenger;
import org.pmg.jms.genclient.ServicePeer;
import org.pmg.jms.genconfig.JmsClientScope;
import org.pmg.jms.genconnect.Connector;
import org.pmg.jms.genconnect.OpenWire;
import org.pmg.jms.gendirector.Controller;
import org.pmg.jms.gendirector.Statement;
import org.pmg.jms.sudoku.genmonitor.ResolveUnitB2;
import org.pmg.jms.sudoku.genmonitor.ResponseUnitB2;
import org.pmg.jms.sudoku.genmonitor.TrialMonitor;
import org.pmg.jms.sudoku.genresolvar.Reductive;
import org.pmg.jms.sudoku.genresolvar.ResolveUnitB1;
import org.pmg.jms.sudoku.genresolvar.ResponseUnitB1;
import org.pmg.jms.sudoku.genresolvar.TrialAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class SudokuModel extends AbstractLifeCycle {
    
    private static final Logger LOG = LoggerFactory.getLogger(SudokuModel.class);
    private final JmsClientScope jmsScope;
    private final SudokuModelScope modelScope;
    private final PeerFactory peerFactory;
    private final ScopedObjFactory beanFactory;
    private TrialMonitor trialGovernor;
    private ServicePeer clientPeer;
    private String sessionId;
    private String context;
        
    @Inject
    public SudokuModel(@Named("JmsClientScoped") 
                        Provider<JmsClientScope> scopePrvdr1,
                            @Named("SudokuModelScoped") 
                            Provider<SudokuModelScope> scopePrvdr2,
                                PeerFactory peerFactory, 
                                    ScopedObjFactory beanFactory) {
        
        jmsScope = scopePrvdr1.get();
        modelScope = scopePrvdr2.get();
        this.peerFactory = peerFactory;
        this.beanFactory = beanFactory;
    }

    @Override
    public void doStart() throws Exception {
        
        try {
            clientPeer.start();
        } catch(JMSException jex) {
            LOG.error("[{}] client peer start failed");
            throw new Exception(jex.getMessage());
        } finally {
            jmsScope.exit();
        }
    }
    
    public void configure(String sessionId) throws JMSException {

        this.sessionId = sessionId;
        
        jmsScope.enter();
        jmsScope.seed(Key.get(Connector.class, OpenWire.class),
                                                beanFactory.getConnector());
        jmsScope.seed(Key.get(Controller.class),beanFactory.getController());

        clientPeer = peerFactory.getClientPeer();
        
        modelScope.enter();
        modelScope.seed(Key.get(Statement.class), beanFactory.getClientState());
        
        trialGovernor = beanFactory.getTrialGovernor();
        LOG.info("[SudokuModel] trial governor : {}",trialGovernor.toString());
        jmsScope.seed(Key.get(TrialMonitor.class),trialGovernor);
                
        Director<ResolveUnitB2,ResponseUnitB2> director = 
                                        peerFactory.getAuxDirector(sessionId);
        director.init();
        String route = "/director/trial/monitor";        
        clientPeer.addHandler(route,director);        
        clientPeer.addBean(director);

        modelScope.exit();

        Messenger<MessageProvider,GamePropogate> messenger1 = 
                                                    peerFactory.getMessenger();
        
        String topicName = "topic://PEER.SUDOKU/resolve/" + sessionId;        
        messenger1.init(topicName,"peer='monitor'");
        messenger1.setRoute(route); // complements previous director setup
        clientPeer.addBean(messenger1);

        MultiMessenger<MessageProvider,GamePropogate> messenger2 = 
                                                peerFactory.getMultiMessenger();
        
        topicName = "topic://PEER.SUDOKU/control/" + sessionId;
        messenger2.init(topicName,"");      
        route = "/director/resolve/control";
        messenger2.setRoute(route);
        clientPeer.addBean(messenger2);
        
        SudokuMembers members = new SudokuMembers();
        Iterator<String> it = members.getIterator();
        while (it.hasNext()) {
            String unitId = it.next();
            addComponent(unitId);
            messenger2.addSubRoute(unitId);
        }
    }
    
    /*
        build methods
    */
    private void addComponent(String peerId) throws JMSException {

        modelScope.enter();
        modelScope.seed(Key.get(Statement.class), beanFactory.getClientState());
        Reductive reductorBean = beanFactory.getReductorBean();
        reductorBean.init(peerId, sessionId);
        modelScope.seed(Key.get(Reductive.class), reductorBean);

        // ResponseUnitB1 producer internally configured
        Director<ResolveUnitB1,ResponseUnitB1> director = 
                                                peerFactory.getGameDirector();
        director.init();
        clientPeer.addBean(director);

        String route = "/director/resolve/reduce/" + peerId;
        clientPeer.addHandler(route,director);
        
        Messenger<MessageProvider,GamePropogate> messenger = 
                                                    peerFactory.getMessenger();
        
        String selection = String.format("peer='%s'",peerId);
        String topicName = "topic://PEER.SUDOKU/resolve/" + sessionId;
        messenger.init(topicName, selection);
        messenger.setRoute(route); // complements previous director setup
        clientPeer.addBean(messenger);

        // complements previous multiMessenger setup
        route = "/director/resolve/control/" + peerId;
        clientPeer.addHandler(route,director);

        TrialAgent trialAgent = peerFactory.getTrialAgent();
        trialGovernor.addTrialAgent(trialAgent);
        
        modelScope.exit();
    }    

    /*
    private void addComponents() throws JMSException {

        addComponent("COL1");
        addComponent("COL2");
        addComponent("COL3");
        addComponent("COL4");
        addComponent("COL5");
        addComponent("COL6");
        addComponent("COL7");
        addComponent("COL8");
        addComponent("COL9");
        addComponent("ROWA");
        addComponent("ROWB");
        addComponent("ROWC");
        addComponent("ROWD");
        addComponent("ROWE");
        addComponent("ROWF");
        addComponent("ROWG");
        addComponent("ROWH");
        addComponent("ROWI");
        addComponent("SQUA1");
        addComponent("SQUA2");
        addComponent("SQUA3");
        addComponent("SQUB1");
        addComponent("SQUB2");
        addComponent("SQUB3");
        addComponent("SQUC1");
        addComponent("SQUC2");
        addComponent("SQUC3");
    }
*/
    public void showAllRoutes() {
        
        clientPeer.getController().showAllRoutes();
    }
}
