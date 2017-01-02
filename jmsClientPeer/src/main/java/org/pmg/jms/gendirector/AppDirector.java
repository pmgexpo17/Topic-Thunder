/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.gendirector;

import com.google.inject.Inject;
import javax.jms.JMSException;
import org.pmg.jms.genclient.Routable;
import org.pmg.jms.genhandler.AbstractHandler;
/**
 *
 * @author peter
 * @param <X> Provider<resolve>
  * @param <Y> Provider<quicken>
 */
public class AppDirector<X extends Resolvar, Y extends Respondar> 
                                            extends AbstractHandler 
                                                    implements ClientDirector {
    
    private ClientState state;
    private final X resolver;
    private final Y responder;
    
    @Inject
    public AppDirector(X resolver, Y responder) {
        
        this.resolver = resolver;
        this.responder = responder;
    }

    @Override
    public void doStop() throws JMSException, Exception {
        
        responder.stop();
    }
    
    @Override
    public void init() throws JMSException {
        
        responder.configure();
        state.current = "ready";
        state.next = "ready";
    }
    
    @Override
    public synchronized void handle(Routable delegate) throws JMSException {

        try {
            state.setDelegate(delegate);        
            if (resolver.apply().hasNext())
                responder.apply().respond();
            state.iterate();
        } catch(Exception ex) {
            LOG.error("[AppDirector] resolve exception",ex);
            throw new JMSException(ex.getMessage());
        }
    }    
    
    @Inject
    public void setClientState(ClientState state) {
        
        this.state = state;
    }
}
