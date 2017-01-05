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
package org.pmg.jms.sudoku.genmodel;

import com.google.inject.name.Named;
import org.pmg.jms.genclient.Messenger;
import org.pmg.jms.genclient.MultiMessenger;
import org.pmg.jms.sudoku.genmonitor.ResolveUnitB2;
import org.pmg.jms.sudoku.genmonitor.ResponseUnitB2;
import org.pmg.jms.sudoku.genresolvar.ResolveUnitB1;
import org.pmg.jms.sudoku.genresolvar.ResponseUnitB1;
import org.pmg.jms.sudoku.genresolvar.TrialAgent;

/**
 * Compliments FactoryModuleBuilder binding in SudokuModule
 * @author peter
 */
public interface PeerFactory {

    ClientPeerB1 getClientPeer();
    Messenger<MessageProvider,GamePropogate> getMessenger();
    MultiMessenger<MessageProvider,GamePropogate> getMultiMessenger();
    @Named("monitor") Director<ResolveUnitB2,ResponseUnitB2> 
                                            getAuxDirector(String sessionId);
    @Named("game") Director<ResolveUnitB1,ResponseUnitB1> getGameDirector();
    TrialAgent getTrialAgent();
}

