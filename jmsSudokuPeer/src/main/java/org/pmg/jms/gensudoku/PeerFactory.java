/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.gensudoku;

import com.google.inject.name.Named;
import org.pmg.jms.genclient.Messenger;
import org.pmg.jms.gendirector.AppDirector;
import org.pmg.jms.gensudoku.director.ResolveUnitA1;
import org.pmg.jms.gensudoku.director.ResponseUnitA1;

/**
 *
 * @author peter
 */
public interface PeerFactory {

    ClientPeerA1 getClientPeer();
    @Named("game") Messenger<MessageProvider,GamePropogate> getGameMessenger(String sessionId);
    AppDirector<ResolveUnitA1,ResponseUnitA1> getGameDirector(String sessionId);    
}
