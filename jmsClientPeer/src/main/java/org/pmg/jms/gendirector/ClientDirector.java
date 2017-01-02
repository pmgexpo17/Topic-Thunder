/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.gendirector;

import javax.jms.JMSException;
import org.pmg.jms.genhandler.Handler;

/**
 *
 * @author peter
 * @param <X>
 * @param <Y>
 */
public interface ClientDirector<X extends Resolvar, Y extends Respondar>
                                                            extends Handler {
    
    public void init() throws JMSException;
    //public <E extends Statement> void setClientState(E state);
}
