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
package org.pmg.jms.sudoku.genmonitor;

import org.pmg.jms.sudoku.genresolvar.TrialAgent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.pmg.jms.genconfig.JmsClientScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
@JmsClientScoped
public class TrialGovernor implements TrialMonitor {

    private static final Logger LOG = 
                            LoggerFactory.getLogger(TrialGovernor.class);
    private final String className = getClass().getSimpleName();
    private final List<TrialAgent> trialAgents = new ArrayList<>();     
    private final List<TrialOption> iterations = new ArrayList<>();
    private final GameRankoten rankoten = new GameRankoten();
    private volatile HashSet<String> peersAll = new HashSet<>();    
    private TrialOption trialOption = null;        
    private int retroIndex = 0;
    private volatile String trialState = "monitor";
    
    @Override
    public void addTrialAgent(TrialAgent trialAgent) {
        
        trialAgents.add(trialAgent);
    }

    private void addIteration() {

        rankoten.reset();
        for(TrialAgent trialAgent: trialAgents)
            evalTopSeed(trialAgent);
        trialOption = rankoten.getTopSeed();
        LOG.info("[{}] new iteration, top ranked : {}",className,
                                                        trialOption.toString());
        iterations.add(0,trialOption);
    }
    
    private void evalTopSeed(TrialAgent trialAgent) {
        
        /* 
          Unit level scoring : get the top scored trialOption for the related 
          unit. Top score means : the unit cell or cells which have the  
          least number of remaining options. 
         */
        TrialOption topSeed = trialAgent.getTrialOption();
        /* 
          Game level scoring : each trialOption is ranked based on the count 
          and combination of double and triple options - see genMonitor.Rankoten
          objective : find the top scored trialOption from all remnant units
        */
        rankoten.evalRank(topSeed);
    }
    
    @Override
    public synchronized void clear() {
        
        peersAll.clear();
    }

    @Override
    public synchronized boolean gameIsStalled(String peerId) {

        switch (trialState) {
            case "monitor" :
                if (peersAll.contains(peerId))
                    return false;
                peersAll.add(peerId);
                // retest to get confirm current idle status
                if (peersAll.size() == 27) 
                    if (gameIsStalled()) {
                        // wait for trial state to be confirmed
                        setState("waiting"); 
                        peersAll.clear();
                        return true;
                    }
                return false;
            default :
                LOG.debug("[{}[{}] waiting for trial state confirmation",
                                                            className,peerId);
                break;
        }
        return false;
    }
    
    @Override
    public synchronized boolean gameIsStalled() {

        peersAll.clear();        
        String peerId;
        for(TrialAgent trialAgent: trialAgents) {
            if ((peerId = trialAgent.getIdleStatus()) != null)
                peersAll.add(peerId);
        }
        return peersAll.size() == 27;
    }

    @Override
    public TrialOption getTrialOption() {

        return trialOption.getNextOption();
    }

    @Override
    public boolean hasTrialOption(String action) {

        // can be either stall or retrial
        if (!action.equals("stall"))
            return trialOption.hasNextOption();
        addIteration();
        return trialOption.hasNextOption();
    }

    @Override
    public synchronized void reset() {
        
        LOG.info("[{}] about to reset the model",className);
        trialState = "monitor";
        peersAll.clear();
        iterations.clear();
        for(TrialAgent trialAgent: trialAgents)
            trialAgent.reset();
      }

    private void serialRollback() throws RollbackException {

        if (trialOption.hasNextOption())
            return;
        iterations.remove(0);
        if (iterations.isEmpty())
            throw new RollbackException(className);
        trialOption = iterations.get(0);
        retroIndex++;
        LOG.info("[{}] top seed options exhausted, rolling back iteration",
                                                                    className);
        serialRollback();
    }    

    @Override
    public int getRetroIndex() throws RollbackException {
        
        LOG.info("[{}] about to rollback the model",className);
        peersAll.clear();
        retroIndex = 0;
        serialRollback();
        return retroIndex;
    }
    
    @Override
    public void rollbackReductor() {
        
        for(TrialAgent trialAgent: trialAgents)
            trialAgent.rollback(retroIndex);        
    }
      
    @Override
    public synchronized void setState(String trialState) {
        
        LOG.debug("[{}] state change from {} to {}",className,trialState,
                                                                    trialState);
        this.trialState = trialState;
    }
    
    private class GameRankoten {

        private final HashSet<TrialOption> topSeeds = new HashSet<>();    
        private int topRank = 9;

        public void evalRank(TrialOption topSeed) {    

            if (topSeed.rank > topRank)
                return;
            else if (topSeed.rank < topRank) {
                topRank = topSeed.rank;
                topSeeds.clear();
            }        
            topSeeds.add(topSeed);
        }

        public TrialOption getTopSeed() {

            return topSeeds.iterator().next();
        }

        public void reset() {
        
            topSeeds.clear();
            topRank = 9;
        }

    }
}
