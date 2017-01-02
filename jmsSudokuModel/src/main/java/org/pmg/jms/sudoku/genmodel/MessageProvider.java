/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genmodel;

import com.google.inject.Inject;
import org.pmg.jms.genclient.ClientMember;
import org.pmg.jms.genconnect.OpenWireSessionPrvdr;

/**
 *
 * @author peter
 */
public class MessageProvider extends ClientMember {
 
    @Inject
    public MessageProvider(OpenWireSessionPrvdr sessionProvider) {        
        
        super(sessionProvider);
    }
}
