/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genmonitor;

import com.google.inject.Inject;
import java.util.HashMap;
import javax.jms.JMSException;
import org.pmg.jms.gendirector.Resolvar;
import org.pmg.jms.gendirector.Resolve;
import org.pmg.jms.sudoku.genmodel.ClientState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class ResolveUnitB2 implements Resolvar {

    private static final Logger LOG = LoggerFactory.getLogger(ResolveUnitB2.class);
    private final String className = getClass().getSimpleName();
    private final HashMap<String,HashMap<String,Resolve>> rcache;
    private TrialGovernor governor;
    private ClientState state;        
    
    @Inject
    public ResolveUnitB2(ClientState state, TrialGovernor governor) {

        this.state = state;
        this.governor = governor;
        this.rcache = loadCache();
        LOG.debug("[{}] trial governor : {}",className,governor.toString());        
        LOG.debug("[{}] state bean : {}",className, state.toString());
    }

    @Override
    public Resolve apply() throws JMSException {
        
        state.action = state.delegate.getString("action");
        state.delegate.setStatus(true);
        return get();
    }

    @Override
    public Resolve get() {
        
        if (rcache.get(state.current).containsKey(state.action))
            return rcache.get(state.current).get(state.action);
        return unknown_action;
    }

    /*
     - state = ready
     */    
    Resolve ready_preset = () -> {
        
        LOG.debug("[{}] the trial monitor is ready",className);
        state.next = "resolve";
        return false;
    };

    /*
     - state = resolve
     */    
    Resolve default_reset = () -> {

        governor.reset();
        LOG.info("[{}] the trial monitor is reset",className);
        state.next = "ready";
        return false;
    };

    Resolve stall_test = () -> {

        // resolvar has notified idle status for monitoring
        String peerId = state.delegate.getString("peerId");
        if (!governor.gameIsStalled(peerId))
            return false;
        LOG.debug("[{}] game may be stalled, notify game director",className);
        state.transition = "stall_test";
        return true;
    };

    Resolve stall_retest = () -> {
            
        if (governor.gameIsStalled()) {
            LOG.debug("[{}] game is stalled, notify game director",className);
            state.transition = "resolve_trial";
            state.next = "trial";
            return true;
        }
        // if game is NOT stalled after retest action then reset the governor
        governor.setState("monitor");
        LOG.debug("[{}] loop monitor not required in state[{}] ",className,
                                                                state.current);
        return false;
    };
    
    /*
     - state = trial
     */    
    Resolve trial_error = () -> {
        
        LOG.info("[{}] trial option failed, deliver rollback index to director",
                                                                    className);
        try {
            state.delegate.setInt("retroIndex",governor.getRetroIndex());
            state.next = "trial-error";
            state.transition = "trial_error";
        } catch (RollbackException rex) {
            LOG.error(rex.toString());
            LOG.info("[{}] notify game director to handle error",className);
            state.transition = "rollback_exception";
        }
        return true;
    };

    Resolve trial_monitor = () -> {
        
        LOG.debug("[{}] start stall monitoring",className);
        governor.setState("monitor");
        return monitor();
    };

    Resolve trial_stall_retest = () -> {
            
        if (governor.gameIsStalled()) {
            LOG.debug("[{}] game is stalled, notify game director",className);
            state.transition = "resolve_trial";
            return true;
        }
        return monitor();
    };

    Resolve trial_start = () -> {
        
        if (governor.hasTrialOption("stall")) {
            state.delegate.setObject("trialOption",governor.getTrialOption());
            state.transition = "trial_start";
            return true;
        }
        LOG.info("[{}] trial governor is exhausted, end of game!!",className);
        return false;
    };

    /*
     - state = trial-error
     */    
    Resolve trial_rollback = () -> {
        
        governor.rollbackReductor();
        state.transition = "trial_retrial";
        return true;
    };

    Resolve trial_restart = () -> {
        
        if (governor.hasTrialOption("retrial")) {
            state.delegate.setObject("trialOption",governor.getTrialOption());
            state.next = "trial";
            state.transition = "trial_start";
            return true;
        }
        LOG.info("[{}] trial governor is exhausted, end of game!!",className);
        return false;
    };

    private boolean monitor() {

        // if the game is not stalled retest by sending a rebound message
        // to the game director
        try {
            // delay 200 millisec to allow the trial option to propagate at 
            // least one cycle before stall is retested
            Thread.sleep(200);
            LOG.debug("[{}] stall monitor, notify game director",className);
            state.transition = "stall_test";
            return true;
        } catch (InterruptedException ex) {
            LOG.error("[{}] sleep is interrupted",className,ex);
        }
        return false;
    }

    Resolve unknown_action = () -> {
        
        LOG.debug("[{}] unknown state[{}] action : {}",className,
                                                    state.current,state.action);
        return false;
    };

    private HashMap<String,HashMap<String,Resolve>> loadCache() {

        // state = ready
        HashMap<String,HashMap<String,Resolve>> _rcache = new HashMap<>(4,1);
        HashMap<String,Resolve> rmap = new HashMap<>(1,1);
        rmap.put("preset",ready_preset);
        _rcache.put("ready",rmap);
        // state = resolve
        rmap = new HashMap<>(3,1);
        rmap.put("reset",default_reset);
        rmap.put("stall_test",stall_test);
        rmap.put("stall_retest",stall_retest);
        _rcache.put("resolve",rmap);
        // state = trial
        rmap = new HashMap<>(6,1);
        rmap.put("error",trial_error);
        rmap.put("monitor",trial_monitor);
        rmap.put("reset",default_reset);
        rmap.put("stall_test",stall_test);
        rmap.put("stall_retest",trial_stall_retest);
        rmap.put("start",trial_start);
        _rcache.put("trial",rmap);
        // state = trial-error
        rmap = new HashMap<>(2,1);
        rmap.put("reset",default_reset);
        rmap.put("restart",trial_restart);
        rmap.put("rollback",trial_rollback);
        _rcache.put("trial-error",rmap);
        return _rcache;
    }
}
