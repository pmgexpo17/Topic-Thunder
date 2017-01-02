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
import javax.jms.JMSException;
import org.pmg.jms.genclient.Routable;
import org.pmg.jms.gendirector.ClientState;
import org.pmg.jms.genhandler.JmsAjaxBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class GameRecorder {
    
    private static final Logger LOG = LoggerFactory.getLogger(GameRecorder.class);
    private static final int MESSAGE_LIFESPAN = 3000;  // milliseconds (3 seconds)
    private final String className = getClass().getSimpleName();
    private volatile ClientState state;
    private GameRecord record;    
    public String gameId = "";

    @Inject 
    public GameRecorder(ClientState state) {
        
        this.state = state;
    }
    
    public void configure(Routable delegate) 
                                        throws RecordException, JMSException {
                
        gameId = delegate.getString("gameId");
        record = new GameRecord();
        record.configure(delegate);
        LOG.info("[{}] new game[{}] solve count : {}",className,gameId,
                                                            record.solveCount);
    }

    public void addSnapshot() {

        record.addSnapshot();
    }

    public boolean gameIsComplete() {
                        
        return record.gameIsResolved();
    }
        
    public boolean gameIsResolved() throws RecordException, JMSException {
        
        String solvent = state.delegate.getString("solved");
        String peerId = state.delegate.getString("peerId");
        record.parseResolvant(state.current, peerId, solvent);
        return record.gameIsResolved();
    }

    public JmsAjaxBean getSolutionBean(String errorType) {
        
        JmsAjaxBean ajaxBean = new JmsAjaxBean();
        ajaxBean.setMessage(record.solved);
        ajaxBean.setAction(errorType);
        return ajaxBean;        
    }

    private void putSolveStatus() {
            
        LOG.info("[{}] solve status : {}",className,record.solved.toString());
    }
        
    public void reset() {
            
        record.reset();
        gameId = "";
    }
     
    public void rollback(int retroIndex) {

        record.rollback(retroIndex);
    }
}
