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

import org.pmg.jms.gensudoku.GameFactory;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import org.pmg.jms.genbase.AbstractLifeCycle;
import org.pmg.jms.genclient.Messenger;
import org.pmg.jms.genclient.Routable;
import org.pmg.jms.genclient.ServicePeer;
import org.pmg.jms.genconfig.JmsClientScope;
import org.pmg.jms.genconnect.Connector;
import org.pmg.jms.genconnect.OpenWire;
import org.pmg.jms.gendirector.Controller;
import org.pmg.jms.gensudoku.GamePropogate;
import org.pmg.jms.gensudoku.MessageProvider;

/**
 * Manages sudoku solver game service, where server capacity is setup by the 
 * Topic Thunder main program 
 * @author peter
 */
public class GameServer extends AbstractLifeCycle implements GameService {
    
    private final String className = getClass().getSimpleName();
    private final JmsClientScope scope;
    private ServicePeer clientPeer;
    private ScheduledExecutorService scheduledService;
    private final LinkedList<Routable> waitList = new LinkedList<>();
    private final LinkedList<String> sessionList = new LinkedList<>();
    private final HashMap<String,NextGameResponder> gameResponders = 
                                                                new HashMap<>();
    private GameFactory gameFactory;        
        
    @Inject
    public GameServer(@Named("JmsClientScoped") JmsClientScope scope,
                                @OpenWire Provider<Connector> connectPrvdr, 
                                        Provider<Controller> controlPrvdr) {
        
        this.scope = scope;
        this.scope.enter();
        this.scope.seed(Key.get(Connector.class, OpenWire.class),connectPrvdr.get());
        this.scope.seed(Key.get(Controller.class), controlPrvdr.get());
    }

    @Override
    public void addCapacity(String sessionId) throws JMSException {
        
        sessionList.addLast(sessionId);
        // NextGameResponder delivers the startMap to the gameDirector
        NextGameResponder gameResponder = 
                                gameFactory.getNextGameResponder(sessionId);
        gameResponder.configure();
        clientPeer.addBean(gameResponder);
        gameResponders.put(sessionId, gameResponder);

        // ResetResponder gets the game completion signal from gameDirector
        String queueName = "queue://PEER.CONTROL/game/service/" + sessionId;
        Messenger<MessageProvider,GamePropogate> resetMessenger = 
                                            gameFactory.getResetMessenger();
        resetMessenger.init(queueName,"peer='game-control'");
        String route = "/game/service/reset/" + sessionId;
        resetMessenger.setRoute(route);
        clientPeer.addBean(resetMessenger);
        
        ResetResponder resetResponder = new ResetResponder();
        resetResponder.setGameServer(this);
        clientPeer.addHandler(route,resetResponder);        
        LOG.info("[{}] sessionList size : {}",className,sessionList.size());
    }
    
    @Inject
    public void setGameFactory(GameFactory gameFactory) {
    
        this.gameFactory = gameFactory;
    }
    
    @Override
    public void configure() throws JMSException {

        clientPeer = gameFactory.getClientPeer();
        clientPeer.addBean(this);
        
        Messenger<MessageProvider,JoinPropogate> joinMessenger = 
                                                gameFactory.getJoinMessenger();
        joinMessenger.init("queue://WEB.CLIENT/sudoku/play","");
        String route = "/web/client/sudoku/play";
        joinMessenger.setRoute(route);
        clientPeer.addBean(joinMessenger);
                
        JoinResponder joinResponder = gameFactory.getJoinResponder();
        joinResponder.setGameServer(this);
        clientPeer.addHandler(route,joinResponder);
        
        PingResponder pingResponder = gameFactory.getPingResponder();
        pingResponder.createConsumer("queue://WEB.CLIENT/sudoku/ping");
        clientPeer.addBean(pingResponder);
        
        scheduledService = Executors.newScheduledThreadPool(6);
        LOG.info("[{}] is configured ",className);
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

    @Override
    public void doStop() throws Exception {

        scheduledService.shutdown();
    }
    
    @Override
    public void pushWaitingClient(Routable delegate) {
        
        synchronized (waitList) {
            waitList.addLast(delegate);
        }
    }

    @Override
    public synchronized void startGame(Routable delegate) throws JMSException {

        String sessionId = sessionList.pollFirst();        
        String webClientId = delegate.getString("clientId");
        LOG.info("[{}] notify game director to start : {},{}",className,
                                                        webClientId,sessionId);
        delegate.setString("sessionId",sessionId);
        NextGameResponder gameResponder = gameResponders.get(sessionId);
        // set timeout = 90 secs and apply gameResponder as timeout handler
        ScheduledFuture future =
                    scheduledService.schedule(gameResponder,90,TimeUnit.SECONDS);            
        delegate.setObject("future",future);
        // use nanotime, to measure elapsed run time, see NextGameResponder
        long startTime = System.nanoTime();
        delegate.setObject("startTime",startTime);
        gameResponder.startGame(delegate);
    }
            
    @Override
    public synchronized void join() throws JMSException {

        Routable delegate  = waitList.pollFirst();
        String webClientId = delegate.getString("clientId");
        LOG.info("[{}] waiting customer is now served : {}",className,
                                                                webClientId);
        startGame(delegate);
    }
    
    @Override
    public synchronized void join(Routable delegate) throws JMSException {

        String webClientId = delegate.getString("clientId");
        LOG.info(String.format("sessionList size : %d",sessionList.size()));
        if (!sessionList.isEmpty()) {
            startGame(delegate);
            return;
        }
        if (!waitList.contains(delegate))
            pushWaitingClient(delegate);
        LOG.info("[{}] join deferred for {}",className,webClientId);
     }
    
    @Override
    public synchronized void release(String sessionId) throws JMSException {

        gameResponders.get(sessionId).cancelTimeout();
        sessionList.addLast(sessionId);
        LOG.info("[{}] gameDirector has released a session : {}",className,
                                                                    sessionId);
        if (waitList.isEmpty())
            LOG.info("[{}] there are 0 waiting customers : {}",className,
                                                                    sessionId);
        else
            join();
    }    
    
    public void showAllRoutes() {
        
        clientPeer.getController().showAllRoutes();
    }
}
