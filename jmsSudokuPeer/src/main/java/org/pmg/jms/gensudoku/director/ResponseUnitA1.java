/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.gensudoku.director;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.HashMap;
import javax.jms.JMSException;
import org.pmg.jms.genbase.AbstractLifeCycle;
import org.pmg.jms.gendirector.ClientState;
import org.pmg.jms.gendirector.Quicken;
import org.pmg.jms.gendirector.Respondar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class ResponseUnitA1 extends AbstractLifeCycle implements Respondar {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseUnitA1.class);
    private static final int HIGH_PRIORITY = 9;
    private static final int MEDIUM_PRIORITY = 6;
    private final String className = getClass().getSimpleName();
    private final HashMap<String,Quicken> rcache;    
    private final String sessionId;
    private ClientState state;    
    private GameResponder responder;
    
    @Inject
    public ResponseUnitA1(GameResponder responder, ClientState state,
                                                @Assisted String sessionId) {
        
        this.responder = responder;
        this.state = state;
        this.sessionId = sessionId;
        rcache = loadCache();
        LOG.debug("[{}] state bean : {}",className, state.toString());
    }

    @Override
    public void configure() throws JMSException {

        responder.init(sessionId);
        // webclient consumer initiates sending a replyTo queue
        responder.createProducer("webclient");
        
        // propogate the sudoku solution of each unit to peer units
        String topicName = "topic://PEER.SUDOKU/resolve/" + sessionId;
        responder.createProducer("resolve-reduce",topicName);

        // to coordinate trial state transition for all sudoku resolveUnits
        topicName = "topic://PEER.SUDOKU/control/" + sessionId;
        responder.createProducer("resolve-control",topicName);

        // control responder comms with the game server
        String queueName = "queue://PEER.CONTROL/game/service/" + sessionId;
        responder.createProducer("game-control",queueName);
    }

    @Override
    public void doStop() throws JMSException, Exception {
        
        responder.stop();
    }
    
    @Override
    public Quicken apply() throws JMSException {

        // state-transition is either self descriptive or it describes
        // the action to do in the current state
        LOG.debug("[{}] state transition : {}",className,state.transition);
        return get();
    }

    @Override
    public Quicken get() {
        
        if (rcache.containsKey(state.transition))
            return rcache.get(state.transition);
        return unknown_action;
    }

    Quicken default_query = () -> {

        responder.putXmlSolution();
        responder.putControlAction("query");
    };

    Quicken reset_ready = () -> {

        // notify the game server that we are ready to restart        
        responder.notifyComplete();
    };

    Quicken default_reset = () -> {

        responder.putXmlSolution();
        responder.putMonitorAction("reset",HIGH_PRIORITY);
        responder.putControlAction("reset");
    };
    
    Quicken default_trial = () -> {

        //  notify monitor, acknowledge trial action
        responder.putMonitorAction("start",HIGH_PRIORITY);
    };

    Quicken ready_preset = () -> {

        responder.configure();
        responder.putMonitorAction("preset",HIGH_PRIORITY);
        responder.putSessionKeys();
    };

    Quicken ready_resolve = () -> {

        responder.putStartMap();
    };

    Quicken stall_test = () -> {

        // notify monitor to retest stall status
        responder.putMonitorAction("stall_retest",HIGH_PRIORITY);
    };
    
    Quicken trial_error = () -> {

        // in this case the current trial option is wrong so rollback the model
        responder.putMonitorAction("error",HIGH_PRIORITY);
        responder.putControlAction("error");
    };

    Quicken trial_restart = () -> {

        // in this case the current trial option is wrong so rollback the model
        // ma
        responder.putMonitorAction("restart",HIGH_PRIORITY);
    };

    Quicken trial_rollback = () -> {

        // in this case the current trial option is wrong so rollback the model
        responder.putMonitorAction("rollback",HIGH_PRIORITY);
    };

    Quicken trial_retrial = () -> {

        responder.putControlAction("retrial");
    };

    Quicken unknown_action = () -> {
        
        LOG.warn("[{}] unknown state[{}] transition : {}",className,
                                                state.current,state.transition);
    };

    private HashMap<String,Quicken> loadCache() {

        // state = ready
        HashMap<String,Quicken> rmap = new HashMap<>(10,1);
        rmap.put("default_query",default_query);
        rmap.put("reset_ready",reset_ready);
        rmap.put("default_reset",default_reset);
        rmap.put("default_trial",default_trial);
        rmap.put("ready_preset",ready_preset);
        rmap.put("ready_resolve",ready_resolve);
        rmap.put("stall_test",stall_test);
        rmap.put("trial_error",trial_error);
        rmap.put("trial_restart",trial_restart);
        rmap.put("trial_rollback",trial_rollback);
        rmap.put("trial_retrial",trial_retrial);        
        return rmap;
    }
}
