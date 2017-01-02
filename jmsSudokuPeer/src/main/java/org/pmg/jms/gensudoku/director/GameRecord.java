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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import javax.jms.JMSException;
import org.pmg.jms.genclient.Routable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class GameRecord {
    
    private static final Logger LOG = LoggerFactory.getLogger(GameRecord.class);
    private final String className;
    private final ArrayList<Snapshot> iterations = new ArrayList<>();
    private final HashMap<String,UnitMarshal> marshals = new HashMap<>();
    private final UnitSerializer serializer = new UnitSerializer();
    public HashMap<String,String> solved = new HashMap<>();
    public final HashSet<String> batchKeys = new HashSet<>();    
    public int rollingCount = 0;
    public int solveCount = 0;   

    public GameRecord() {
        
        className = getClass().getSimpleName();
        setMarshals();
    }

    public void addSnapshot() {

        Snapshot snapshot = new Snapshot();
        snapshot.rollingCount = rollingCount;
        snapshot.solved = serializer.toByteArray(solved);        
        iterations.add(0,snapshot);
        for( String unitId: marshals.keySet())
            marshals.get(unitId).addSnapshot();        
    }

    private void addStartSolvent(String peerId, String solvant) 
                                                        throws RecordException {
        
        if (solvant.isEmpty())
            return;
        String key, solvent;            
        StringTokenizer tokenz = new StringTokenizer(solvant,",");
        while( tokenz.hasMoreTokens() ) {
            key = tokenz.nextToken();
            if (!tokenz.hasMoreTokens())
                throw new RecordException(peerId,solvant);
            solvent = tokenz.nextToken();
            marshals.get(peerId).addSolvent(solvent);
        }
    }

    public void configure(Routable delegate) 
                                        throws RecordException, JMSException {
        
        HashMap<String,String> startMap = 
                                    (HashMap) delegate.getObject("startMap");
        int startCount = Integer.valueOf(startMap.remove("count"));
        solveCount = 81 - startCount;
        rollingCount = 0;
        for( String unitId: startMap.keySet()) {
            if (startMap.get(unitId) == null)
                continue;
            String solvant = startMap.get(unitId);
            addStartSolvent(unitId,solvant);
        }
    }

    public boolean gameIsResolved() {
        
        return rollingCount >= solveCount;
    }

    public synchronized void parseResolvant(String state,String unitId, 
                                        String solvant) throws RecordException {

        LOG.debug("[{}] parsing solvant for {} : {}",className,unitId,solvant);
        batchKeys.clear();
        String key, solvent;            
        StringTokenizer tokenz = new StringTokenizer(solvant,",");
        while( tokenz.hasMoreTokens() ) {
            key = tokenz.nextToken();
            solvent = tokenz.nextToken();
            if (solved.containsKey(key)) {
                if (!solved.get(key).equals(solvent))
                    throw new RecordException(key,solvent,solved.get(key));
                else
                    LOG.debug("[{}] Duplicate removed, {}={}",className,
                                                                   key,solvent);
                continue;
            }
            // in trial state, we test if the solvent is a duplicate in the unit
            if (state.equals("trial"))
                testDuplicateError(unitId,key,solvent);            
            batchKeys.add(key);
            solved.put(key,solvent);
            putResolved(unitId,key,solvent);
        }
        if (batchKeys.isEmpty())
            return;
        rollingCount += batchKeys.size();
        LOG.info("[{}] rolling count : {}",className,rollingCount);
    }

    private void putResolved(String unitId, String key, String solvent) {

        for( String peerId: marshals.get(unitId).getAllPeers(key) )
            marshals.get(peerId).addSolvent(solvent);
    }

    public void reset() {
        
        solved.clear();
        rollingCount = 0;
        solveCount = 0;
        for( String unitId: marshals.keySet())
            marshals.get(unitId).reset();
    }    

    public void rollback(int retroIndex) {
        
        serialRollback(retroIndex);
        Snapshot snapshot = iterations.get(0);
        solved = (HashMap<String,String>) serializer.toObject(snapshot.solved);
        rollingCount = snapshot.rollingCount;
        for( String unitId: marshals.keySet())
            marshals.get(unitId).rollback(retroIndex);        
    }
    
    private void serialRollback(int retroIndex) {
        
        while (retroIndex > 0) {
            iterations.remove(0);
            retroIndex--;
        }
    }

    private void setMarshals() {
        
        marshals.put("ROWA",new UnitMarshal("ROWA"));
        marshals.put("ROWB",new UnitMarshal("ROWB"));
        marshals.put("ROWC",new UnitMarshal("ROWC"));
        marshals.put("ROWD",new UnitMarshal("ROWD"));
        marshals.put("ROWE",new UnitMarshal("ROWE"));
        marshals.put("ROWF",new UnitMarshal("ROWF"));
        marshals.put("ROWG",new UnitMarshal("ROWG"));
        marshals.put("ROWH",new UnitMarshal("ROWH"));
        marshals.put("ROWI",new UnitMarshal("ROWI"));
        marshals.put("COL1",new UnitMarshal("COL1"));
        marshals.put("COL2",new UnitMarshal("COL2"));
        marshals.put("COL3",new UnitMarshal("COL3"));
        marshals.put("COL4",new UnitMarshal("COL4"));
        marshals.put("COL5",new UnitMarshal("COL5"));
        marshals.put("COL6",new UnitMarshal("COL6"));
        marshals.put("COL7",new UnitMarshal("COL7"));
        marshals.put("COL8",new UnitMarshal("COL8"));
        marshals.put("COL9",new UnitMarshal("COL9"));
        marshals.put("SQUA1",new UnitMarshal("SQUA1"));
        marshals.put("SQUA2",new UnitMarshal("SQUA2"));
        marshals.put("SQUA3",new UnitMarshal("SQUA3"));
        marshals.put("SQUB1",new UnitMarshal("SQUB1"));
        marshals.put("SQUB2",new UnitMarshal("SQUB2"));
        marshals.put("SQUB3",new UnitMarshal("SQUB3"));
        marshals.put("SQUC1",new UnitMarshal("SQUC1"));
        marshals.put("SQUC2",new UnitMarshal("SQUC2"));
        marshals.put("SQUC3",new UnitMarshal("SQUC3"));
    }

    private void testDuplicateError(String unitId, String key, String solvent) 
                                                        throws RecordException {
        
        // test if the solvent exists in any of the 3 peers that contain the key
        for( String peerId: marshals.get(unitId).getAllPeers(key) ) {
            if (marshals.get(peerId).contains(solvent)) {
                String resolved = marshals.get(peerId).getResolved();
                throw new RecordException(peerId,key,solvent,resolved);
            }
        }
    }    
}
