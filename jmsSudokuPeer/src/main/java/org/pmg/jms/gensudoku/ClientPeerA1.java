/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.gensudoku;

import com.google.inject.Inject;
import org.pmg.jms.genclient.ClientPeer;
import org.pmg.jms.genconnect.Connector;
import org.pmg.jms.genconnect.OpenWire;
import org.pmg.jms.gendirector.Controller;

/**
 *
 * @author peter
 */
public class ClientPeerA1 extends ClientPeer {

    @Inject
    public ClientPeerA1(@OpenWire Connector connector, Controller controller) {
        
        super(connector, controller);
    }
}
