/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genresolvar;

import com.google.inject.Inject;
import java.util.HashMap;
import javax.jms.JMSException;
import org.pmg.jms.genbase.AbstractLifeCycle;
import org.pmg.jms.gendirector.Quicken;
import org.pmg.jms.gendirector.Respondar;
import org.pmg.jms.sudoku.genmodel.ClientState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class ResponseUnitB1 extends AbstractLifeCycle implements Respondar {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseUnitB1.class);
    private final String className = getClass().getSimpleName();
    private static final int HIGH_PRIORITY = 9;
    private final HashMap<String,Quicken> rcache;
    private ClientState state;        
    private GameResponderB1 responder;
    private String peerId;    
    private String sessionId;    
    
    @Inject
    public ResponseUnitB1(ClientState state,GameResponderB1 responder) {

        this.state = state;
        this.responder = responder;
        rcache = loadCache();
        LOG.debug("[{}] state bean : {}",className, state.toString());
    }

    @Override
    public void configure() throws JMSException {
        
        ReductorBean bean = responder.getReductorBean();
        this.sessionId = bean.sessionId;
        this.peerId = bean.peerId;
        // propogate the sudoku solution of each unit to peer units
        String topicName = "topic://PEER.SUDOKU/resolve/" + sessionId;
        responder.createProducer("resolve-reduce",topicName);
        LOG.debug("[{}] configured with sessionId : {}",className,sessionId);
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

    Quicken default_error = () -> {
        
        responder.putControlAction("error");
    };

    Quicken default_ready = () -> {
        
        responder.putControlAction("reset");
    };

    Quicken default_resolve = () -> {
        
        responder.putSolveMap();
    };

    Quicken ready_resolve = () -> {
        
        responder.putControlAction("start");
    };

    Quicken stall_test = () -> {
        
        responder.putMonitorAction("stall_test");
    };

    Quicken trial_error = () -> {
        
        responder.putControlAction("reset");
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
        HashMap<String,Quicken> rmap = new HashMap<>(6,1);
        rmap.put("default_error",default_error);
        rmap.put("default_ready",default_ready);
        rmap.put("default_resolve",default_resolve);
        rmap.put("ready_resolve",ready_resolve);
        rmap.put("stall_test",stall_test);
        rmap.put("trial_error",trial_error);
        rmap.put("trial_retrial",trial_retrial);
        return rmap;
    }
}
