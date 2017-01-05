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
package org.pmg.jms.gensudoku.director;

import com.google.inject.Inject;
import java.util.HashMap;
import javax.jms.JMSException;
import org.pmg.jms.gendirector.ClientState;
import org.pmg.jms.gendirector.Resolvar;
import org.pmg.jms.gendirector.Resolve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs state machine (SM) behaviour for SM lifecycle iteration. Java 8 lambda
 * expressions provide a code as data mechanism
 * @author peter
 */

public class ResolveUnitA1 implements Resolvar {

    private static final Logger LOG = 
                                   LoggerFactory.getLogger(ResolveUnitA1.class);
    private final String className = getClass().getSimpleName();
    private final HashMap<String,HashMap<String,Resolve>> rcache;
    private Acknowledgement signal = new Acknowledgement();
    private volatile ClientState state;    
    private GameRecorder recorder;
    
    @Inject
    public ResolveUnitA1(ClientState state, GameRecorder recorder) {

        this.state = state;
        this.recorder = recorder; 
        rcache = loadCache();
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
        
        LOG.info("[{}] starting new sudoku session",className);
        state.transition = "ready_preset";
        try {
            recorder.configure(state.delegate);            
        } catch (RecordException rex) {
            LOG.error(rex.getMessage());
            throw new JMSException("recorder init failure");
        }
        return true;
    };
    
    Resolve ready_start = () -> {

        if (!signal.allAcknowledged(state.delegate.getString("peerId"),27))
            return false;
        LOG.info("[{}] start acknowledged",className);
        state.transition = "ready_resolve";
        state.next = "resolve";
        return true;
    };

    Resolve default_ignore = () -> {

        LOG.debug("[{}] ignoring action : {}",className,state.action);
        return false;
    };
    
    /*
     - state = reset
     */
    Resolve reset_ready = () -> {
        
        // test if all model resolver units have notified reset
        if (!signal.allAcknowledged(state.delegate.getString("peerId"),27))
            return false;
        // notify the game server that we are ready to restart
        LOG.info("[{}] reset acknowledged",className);
        recorder.reset();
        state.transition = "reset_ready";
        state.next = "ready";
        return true;
    };

    /*
     - state = resolve
     */
    Resolve resolve_error = () -> {
        
        // the model resolver has detected a fatal error, due to wrong start map
        LOG.info("[{}] notify model of start map error",className);
        state.ajaxBean = recorder.getSolutionBean("error1");
        state.transition = "default_reset";
        state.next = "reset";
        return true;
    };

    Resolve default_query = () -> {
    
        // this is included for debug mode
        // we don't reset immediately, wait for timeout
        LOG.info("[{}] notify model : get partial resolve status",className);
        state.ajaxBean = recorder.getSolutionBean("resolve");
        state.transition = "default_query";
        return true;
    };

    Resolve resolve_resolve = () -> {

        try {
            if (!recorder.gameIsResolved())
                return false;
            // return the resolved solution to the webclient
            state.ajaxBean = recorder.getSolutionBean("resolve");
        } catch (RecordException rex) {
            // the recorded has detected a fatal error, due to wrong start map
            // return the partial errored solution to the webclient            
            state.ajaxBean = recorder.getSolutionBean("error1");
            LOG.error(rex.getMessage());
        }
        // this means the game is resolved
        state.transition = "default_reset";
        state.next = "reset";
        return true;
    };

    Resolve default_reset = () -> {
    
        // the game timeout has expired
        LOG.info("[{}] notify model : timeout",className);
        state.ajaxBean = recorder.getSolutionBean("timeout");
        state.transition = "default_reset";
        state.next = "reset";
        return true;
    };

    Resolve stall_test = () -> {
        
        if (recorder.gameIsComplete())
            return false;
        LOG.debug("[{}] game is NOT complete, test stall",className);
        // no state transition here, describe the action instead
        state.transition = "stall_test";
        return true;
    };

    Resolve default_trial = () -> {
        
        if (recorder.gameIsComplete())
            return false;
        LOG.debug("[{}] game is NOT complete, resolve stall",className);        
        recorder.addSnapshot();
        state.transition = "default_trial";
        state.next = "trial";
        return true;
    };

    Resolve trial_error = () -> {

        // in this case the current trial option is wrong so rollback the model
        state.transition = "trial_error";
        state.next = "trial-error";        
        return true;
    };
    
    /*
     - state = trial
     */
    Resolve trial_resolve = () -> {

        try {
            if (!recorder.gameIsResolved())
                return false;
            // return the resolved solution to the webclient
            state.ajaxBean = recorder.getSolutionBean("resolve");
        } catch (RecordException rex) {
            // the recorded has detected an error, which means the trial option
            // is wrong, so now we rollback for retrial
            LOG.error(rex.toString());
            LOG.info("[{}] notify model to rollback trial",className);
            state.transition = "trial_error";
            state.next = "trial-error";
            return true;
        }
        // this means the game is resolved
        state.transition = "default_reset";
        state.next = "reset";
        return true;
    };

    /*
     - state = trial-error
     */
    Resolve trial_rollback = () -> {
        
        if (state.action.equals("rollback")) {
            int retroIndex = state.delegate.getInt("retroIndex");
            LOG.debug("[{}] got retro index[{}] for recorder rollback",
                                                        className, retroIndex);
            recorder.rollback(retroIndex);
        }
        // test if all model resolver units have notified reset
        if (!signal.allAcknowledged(state.delegate.getString("peerId"),28))
            return false;
        String lognote = 
               "[{}] got trial-error acknowledgement, ready for model rollback";
        LOG.debug(lognote,className);
        state.transition = "trial_rollback";
        return true;
    };

    Resolve trial_restart = () -> {

        // test if all model resolver units have notified reset
        if (!signal.allAcknowledged(state.delegate.getString("peerId"),27))
            return false;
        LOG.debug("[{}] got retrial acknowledgement, ready for retrial",
                                                                     className);
        state.next = "trial";
        state.transition = "trial_restart";
        return true;
    };

    Resolve unknown_action = () -> {
        
        LOG.debug("[{}] unknown state[{}] action : {}",className,
                                                    state.current,state.action);
        return false;
    };    
    
    private HashMap<String,HashMap<String,Resolve>> loadCache() {

        // state = ready
        HashMap<String,HashMap<String,Resolve>> _rcache = new HashMap<>(5,1);
        HashMap<String,Resolve> rmap = new HashMap<>(2,1);
        rmap.put("preset",ready_preset);
        rmap.put("start",ready_start);
        _rcache.put("ready",rmap);
        // state = reset
        rmap = new HashMap<>(1,1);
        rmap.put("reset",reset_ready);
        _rcache.put("reset",rmap);
        // state = resolve
        rmap = new HashMap<>(6,1);
        rmap.put("error",resolve_error);
        rmap.put("query",default_query);
        rmap.put("resolve",resolve_resolve);
        rmap.put("stop",default_reset);        
        rmap.put("stall_test",stall_test);
        rmap.put("trial",default_trial);
        _rcache.put("resolve",rmap);
        // state = trial
        rmap = new HashMap<>(7,1);
        rmap.put("error",trial_error);
        rmap.put("query",default_query);
        rmap.put("resolve",trial_resolve);
        rmap.put("stall_test",stall_test);
        rmap.put("start",trial_resolve);
        rmap.put("stop",default_reset);
        rmap.put("trial",default_trial);
        _rcache.put("trial",rmap);        
        // state = trial-error
        rmap = new HashMap<>(4,1);
        rmap.put("rollback",trial_rollback);
        rmap.put("retrial",trial_restart);
        rmap.put("reset",trial_rollback);
        rmap.put("rollback_exception",default_reset);
        rmap.put("stop",default_reset);
        _rcache.put("trial-error",rmap);
        return _rcache;
    }
    

}
