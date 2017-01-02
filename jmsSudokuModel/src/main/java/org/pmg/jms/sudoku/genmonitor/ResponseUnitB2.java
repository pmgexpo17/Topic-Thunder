/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genmonitor;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.Arrays;
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
public class ResponseUnitB2 extends AbstractLifeCycle implements Respondar {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseUnitB2.class);
    private final String className = getClass().getSimpleName();
    private static final int HIGH_PRIORITY = 9;
    private final HashMap<String,Quicken> rcache;
    private ClientState state;    
    private GameResponderB2 responder;
    private String sessionId;
    
    @Inject
    public ResponseUnitB2(ClientState state, GameResponderB2 responder,
                                                @Assisted String sessionId) {
        
        this.state = state;
        this.responder = responder;
        this.sessionId = sessionId;
        rcache = loadCache();
        LOG.debug("[{}] state bean : {}",className, state.toString());
    }
    
    @Override
    public void configure() throws JMSException {
        
        // propogate the sudoku solution of each unit to peer units
        String topicName = "topic://PEER.SUDOKU/resolve/" + sessionId;
        responder.createProducer("resolve-reduce",topicName);
        
        topicName = "topic://PEER.SUDOKU/control/" + sessionId;
        responder.createProducer("resolve-control",topicName);
        LOG.info("[{}] configured with sessionId : {}",className,sessionId);
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

    Quicken rollback_exception = () -> {
        
        responder.putControlAction(sessionId,"rollback_exception");
    };

    Quicken resolve_trial = () -> {
        
        responder.putControlAction("","trial");
        responder.putControlAction(sessionId,"trial");
    };

    Quicken stall_test = () -> {
        
        responder.putControlAction(sessionId,"stall_test");
    };
    
    Quicken trial_error = () -> {
        
        int retroIndex = state.delegate.getInt("retroIndex");
        responder.putRetroIndex(sessionId,retroIndex);
    };

    Quicken trial_retrial = () -> {
        
        responder.putControlAction("","retrial");
    };

    Quicken trial_start = () -> {
        
        TrialOption trialOption = (TrialOption)
                                    state.delegate.getObject("trialOption");
        LOG.info("[{}] trial option delivery to peers : {}",className,
                                        Arrays.toString(trialOption.allPeers));
        responder.putTrialOption(sessionId,trialOption);
        responder.putControlAction("monitor","monitor");
    };
    
    Quicken unknown_action = () -> {
        
        LOG.warn("[{}] unknown state[{}] transition : {}",className,
                                                state.current,state.transition);
    };

    private HashMap<String,Quicken> loadCache() {

        // state = ready
        HashMap<String,Quicken> rmap = new HashMap<>(5,1);
        rmap.put("rollback_exception",rollback_exception);
        rmap.put("stall_test",stall_test);
        rmap.put("resolve_trial",resolve_trial);
        rmap.put("trial_error",trial_error);
        rmap.put("trial_retrial",trial_retrial);
        rmap.put("trial_start",trial_start);
        return rmap;
    }
}
