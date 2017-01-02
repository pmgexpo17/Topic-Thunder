/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author peter
 */
public interface PeerFactory {

    ClientPeerB1 getClientPeer();
    Messenger<MessageProvider,GamePropogate> getMessenger();
    MultiMessenger<MessageProvider,GamePropogate> getMultiMessenger();
    @Named("monitor") Director<ResolveUnitB2,ResponseUnitB2> 
                                        getAuxDirector(String sessionId);
    @Named("game") Director<ResolveUnitB1,ResponseUnitB1> 
                                            getGameDirector();
    TrialAgent getTrialAgent();
}

