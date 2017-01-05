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
package org.pmg.jms.sudoku.genresolvar;

import java.util.ArrayList;
import org.pmg.jms.sudoku.genprofile.*;
import java.util.HashMap;
import org.pmg.jms.sudoku.genmodel.SudokuModelScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sudoku reductor component that contains sudoku unit and support data. API
 * methods support state machine activity and reduction status. Includes trial 
 * model iteration and rollback support. Guice scoping provides containerized 
 * sharing for this global bean
 * @author peter
 */
@SudokuModelScoped
public class ReductorBean implements Reductive {

    private static final Logger LOG = LoggerFactory.getLogger(ReductorBean.class);
    private final String className = getClass().getSimpleName();
    protected Profile profile;   
    protected String peerId;
    protected String sessionId;    
    volatile ArrayList<String> remnantKeys = new ArrayList<>();            
    volatile HashMap<String,String> resolved = new HashMap<>();
    volatile HashMap<String,String> unit = new HashMap<>(); 
    volatile HashMap<String,String> solvant = new HashMap<>();
    volatile String gameId = "";    
    volatile int trialId = 0;

    @Override
    public void init(String peerId, String sessionId) {

        this.peerId = peerId;
        this.sessionId = sessionId;
        LOG.info("[{}[{}] sessionId : {}",className,peerId,sessionId);
        profile = getUnitProfile(peerId);
    }
    
    /* method group : resolved/remnantKeys/unit handler methods */
    @Override
    public void clear() {
        
        resolved.clear();
    }

    @Override
    public boolean isIdle() {

        return (resolved.isEmpty() || remnantKeys.isEmpty());
    }

    @Override
    public void reset() {

        gameId = "";
        trialId = 0;
        remnantKeys.clear();
        unit.clear();
        for(String key: profile.getUnitKeys()) {
            remnantKeys.add(key);
            unit.put(key,"123456789");
        }
    }
        
    @Override
    public boolean gameIsComplete() {
        
        return remnantKeys.isEmpty();
    }

    @Override
    public boolean hasReduction() {
        
        return !resolved.isEmpty();
    }
    /*end method group */
    
    /* method group : snapshot handler methods */
    @Override
    public synchronized Snapshot getSnapshot() {

        Snapshot snapshot = new Snapshot();
        snapshot.remnantKeys = (ArrayList<String>) remnantKeys.clone();
        snapshot.unit = (HashMap<String,String>) unit.clone();
        return snapshot;
    }

    @Override
    public synchronized void rollback(Snapshot snapshot) {

        unit = snapshot.unit;
        remnantKeys = snapshot.remnantKeys;
        resolved.clear();        
    }
    /*end method group */
    
    /* method group : publish solvent to peer associates */
    private void addSolvent(String peerId, String key, String options) {

        if (solvant.containsKey(peerId))
            solvant.put(peerId,String.format("%s,%s,%s",
                                        solvant.get(peerId),key,options));
        else 
            solvant.put(peerId,key + "," + options);
    }

    @Override
    public String getSolvent(String peerId) {
        
        return solvant.get(peerId);
    }

    @Override
    public void fillSolveMap() {
        
        // fill solvant to post the solvent each peer director that the
        // related sudoku unit (rectangle,square) intersects with
        solvant.clear();
        String options;
        for( String key: resolved.keySet() ) {
            options = resolved.get(key);
            if (key.contains(":")) {
                String[] parts = key.split(",");
                addSolvent(parts[0],parts[1],options);
                continue;
            }
            if (options.length() == 1)
                addSolvent(sessionId,key,options);
            for( String _peerId: profile.getAllPeers(key) )
                addSolvent(_peerId,key,options);
        }
    }
    /* end method group */
    
    /* method group : post resolved/reduction stats in log messages */
    @Override
    public void putRemnantLog() {
        
        LOG.debug("[{}[{}] resolve status : {}",className,peerId,remnantLog());
    }
    
    @Override
    public void putResolveLog() {
        
        LOG.debug("[{}[{}] {} cells are resolved : {}",className,peerId,
                                resolved.size(),resolved.toString());
    }

    @Override
    public void putStartLog() {
        
        LOG.debug("[{}[{}] Initial status : {}",className,peerId,
                                                            unit.toString());            
        if (resolved.isEmpty())
            return;
        LOG.debug("[{}[{}] {} start cells are resolved : {}",className,peerId,
                                           resolved.size(),resolved.toString());                                
    }
    
    private String remnantLog() {
        
        return String.format("[%s]remnant: %s, unit: %s",peerId,
                                remnantKeys.toString(),unit.toString());
    }
    /* end method group */
    
    private static Profile getUnitProfile(String unitId) {
        
        switch (unitId) {
            case "ROWA" : return new ProfileRowA();
            case "ROWB" : return new ProfileRowB();
            case "ROWC" : return new ProfileRowC();
            case "ROWD" : return new ProfileRowD();
            case "ROWE" : return new ProfileRowE();
            case "ROWF" : return new ProfileRowF();
            case "ROWG" : return new ProfileRowG();
            case "ROWH" : return new ProfileRowH();
            case "ROWI" : return new ProfileRowI();
            case "COL1" : return new ProfileCol1();
            case "COL2" : return new ProfileCol2();
            case "COL3" : return new ProfileCol3();
            case "COL4" : return new ProfileCol4();
            case "COL5" : return new ProfileCol5();
            case "COL6" : return new ProfileCol6();
            case "COL7" : return new ProfileCol7();
            case "COL8" : return new ProfileCol8();
            case "COL9" : return new ProfileCol9();
            case "SQUA1" : return new ProfileSquA1();
            case "SQUA2" : return new ProfileSquA2();
            case "SQUA3" : return new ProfileSquA3();
            case "SQUB1" : return new ProfileSquB1();
            case "SQUB2" : return new ProfileSquB2();
            case "SQUB3" : return new ProfileSquB3();
            case "SQUC1" : return new ProfileSquC1();
            case "SQUC2" : return new ProfileSquC2();
            case "SQUC3" : return new ProfileSquC3();
        }
        return null;
    }
}
