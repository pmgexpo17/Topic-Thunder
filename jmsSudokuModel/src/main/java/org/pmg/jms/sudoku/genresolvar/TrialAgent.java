/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genresolvar;

import com.google.inject.Inject;
import java.util.ArrayList;
import org.pmg.jms.sudoku.genmonitor.Rankoten;
import org.pmg.jms.sudoku.genmonitor.TrialOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class TrialAgent {
    
    private static final Logger LOG = LoggerFactory.getLogger(TrialAgent.class);
    private final String className = getClass().getSimpleName();    
    private final ArrayList<Snapshot> iterations = new ArrayList<>();
    private final Rankoten rankoten;
    private final ReductorBean bean;
    
    @Inject
    public TrialAgent(ReductorBean bean) {
        
        this.bean = bean;
        rankoten =  new Rankoten(bean.peerId);
        LOG.debug("[{}[{}] reductor bean : {}",className, bean.peerId, 
                                                            bean.toString());
    }
    
    public synchronized String getIdleStatus() {

        return bean.isIdle() ? bean.peerId : null;
    }

    public TrialOption getTrialOption() {

        LOG.debug("[{}[{}] add snapshot iteration",className,bean.peerId);
        Snapshot snapshot = bean.getSnapshot();
        iterations.add(0,snapshot);
        bean.trialId++;
        rankoten.evalRank(snapshot);
        return rankoten.getTopSeed(bean.profile);
    }
        
    private void serialRollback(int retroIndex) {

        while (retroIndex > 0) {        
            iterations.remove(0);
            retroIndex--;
        }
    }
    
    public synchronized void rollback(int retroIndex) {

        serialRollback(retroIndex);
        bean.rollback(iterations.get(0));
        bean.trialId++;
        LOG.debug("[{}[{}] notify director of rollback",className,bean.peerId);                
    }
    
    public void reset() {
        
        iterations.clear();        
        LOG.debug("[{}[{}] notify director of reset",className,bean.peerId);
    }
}
