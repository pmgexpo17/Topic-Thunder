/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genresolvar;

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
public class ResolveUnitB1 implements Resolvar {

    private static final Logger LOG = LoggerFactory.getLogger(ResolveUnitB1.class);
    private final String className = getClass().getSimpleName();
    private final HashMap<String,HashMap<String,Resolve>> rcache;
    private ClientState state;        
    private ReductorBean bean;    
    private Reduction reductor;
    
    @Inject
    public ResolveUnitB1(ClientState state, ReductorBean bean, 
                                               ReductorPrvdr reductorPrvder) {

        this.state = state;
        this.bean = bean;
        reductorPrvder.configure(bean.peerId);
        this.reductor = reductorPrvder.get();
        this.rcache = loadCache();
        LOG.debug("[{}] state bean : {}",className, state.toString());
        LOG.debug("[{}] reductor bean : {}",className, bean.toString());
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

    Resolve default_error = () -> {

        LOG.debug("[{}] error reported, game aborted : {}",className,state.action);
        return false;
    };
    
    Resolve default_ignore = () -> {

        LOG.debug("[{}] ignoring action : {}",className,state.action);
        return false;
    };

    /*
     - state = ready
    */
    Resolve ready_preset = () -> {
        
        bean.gameId = (String) state.delegate.getString("gameId");
        LOG.debug("[{}[{}] gameId is set : {}",className,
                                                    bean.peerId,bean.gameId);
        state.next = "resolve";
        state.transition = "ready_resolve";
        return true;
    };

    /*
     - state = resolve
     */
    Resolve default_monitor = () -> {

        bean.clear(); // to enable stall detection
        LOG.debug("[{}[{}] got solvent back, now stall test is valid",className,
                                                                bean.peerId);
        return false;
    };

    Resolve default_query = () -> {

        bean.putRemnantLog();
        return false;
    };

    Resolve default_reset = () -> {
        
        bean.reset();
        reductor.reset();
        state.next = "ready";
        state.transition = "default_ready";
        return true;
    };
    
    Resolve resolve_resolve = () -> {
        
        if (bean.gameIsComplete())
            return false;
        // this is to prevent interference between the end of the
        // current game and the start of the next
        if (!state.delegate.getString("gameId").equals(bean.gameId)) {
            LOG.debug("[{}[{}] gameId is not in sync : {}",
                                                    bean.peerId,bean.gameId);
            return false;
        }
        String solvent = state.delegate.getString("solved");
        if (!resolve(solvent)) // means that resolved cache must be empty
            state.transition = "stall_test";
        return true;
    };

    Resolve resolve_start = () -> {

        String solvent = state.delegate.getString("solved");
        LOG.debug("[{}[{}] received start map from controller : {}",
                                                className,bean.peerId,solvent);
        bean.reset();
        if (solvent == null)
            return false;
        if (!resolve(solvent))
            return false;
        bean.putStartLog();
        return true;
    };

    Resolve resolve_trial = () -> {
     
        LOG.debug("[{}[{}] trial state transition",className,bean.peerId);
        state.next = "trial";
        return false;
    };

    /*
     - state = trial
     */
    Resolve trial_error = () -> {
     
        LOG.debug("[{}[{}] got trial error notice",className,bean.peerId);
        state.transition = "trial_error";
        state.next = "trial-error";
        return true;
    };

    Resolve trial_start = () -> {

        String solvent = state.delegate.getString("solved");
        LOG.debug("[{}[{}] starting trial now with option [{}]",
                                                className,bean.peerId,solvent);
        return resolve(solvent);
    }; 

    Resolve trial_resolve = () -> {
        
        if (bean.gameIsComplete())
            return false;
        // this is to prevent interference between the end of the
        // current game and the start of the next
        if (state.delegate.getInt("trialId") != bean.trialId) {
            LOG.debug("[{}[{}] trialId is not in sync : {}",
                                                    bean.peerId,bean.trialId);
            return false;
        }
        String solvent = state.delegate.getString("solved");
        return resolve(solvent);
    };

    /*
     - state = trial-error
     */
    Resolve trial_retrial = () -> {
     
        LOG.debug("[{}[{}] got retrial notice",className,bean.peerId);
        state.next = "trial";
        state.transition = "trial_retrial";
        return true;
    };

    private boolean resolve(String solvent) {
        
        try {
            reductor.reduce(solvent);
        } catch (ResolveException rex) {
            LOG.error(rex.toString());
            LOG.info("[{}[{}] notify game director to handle error[{}]",
                                        className,bean.peerId,state.current);
            state.transition = "default_error";
            return true;
        }
        if (bean.hasReduction()) {
            state.transition = "default_resolve";
            return true;        
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
        rmap = new HashMap<>(6,1);
        rmap.put("monitor",default_monitor);
        rmap.put("query",default_query);
        rmap.put("reset",default_reset);
        rmap.put("resolve",resolve_resolve);
        rmap.put("start",resolve_start);
        rmap.put("trial",resolve_trial);
        _rcache.put("resolve",rmap);
        // state = trial
        rmap = new HashMap<>(6,1);
        rmap.put("error",trial_error);
        rmap.put("monitor",default_monitor);
        rmap.put("query",default_query);
        rmap.put("reset",default_reset);
        rmap.put("start",trial_start);
        rmap.put("resolve",trial_resolve);
        _rcache.put("trial",rmap);
        // state = trial-error
        rmap = new HashMap<>(5,1);
        rmap.put("query",default_query);
        rmap.put("reset",default_reset);
        rmap.put("retrial",trial_retrial);
        _rcache.put("trial-error",rmap);
        return _rcache;
    }
}
