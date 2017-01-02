/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genresolvar;

import com.google.inject.Inject;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import org.pmg.jms.genclient.ClientResponder;
import org.pmg.jms.genconnect.OpenWireSessionPrvdr;
import org.pmg.jms.sudoku.genmodel.ClientState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class GameResponderB1 extends ClientResponder {
    private static final Logger LOG = LoggerFactory.getLogger(GameResponderB1.class);
    private static final int MESSAGE_LIFESPAN = 3000;  // milliseconds (3 seconds)
    private static final int HIGH_PRIORITY = 9;
    private final ReductorBean bean;    
    private final ClientState state;

    @Inject
    public GameResponderB1(OpenWireSessionPrvdr sessionProvider,
                                                    ClientState state,
                                                        ReductorBean bean) {
        super(sessionProvider);
        this.state = state;
        this.bean = bean;
        LOG.debug("[{}] state bean : {}",className, state.toString());
        LOG.debug("[{}] reductor bean : {}",className, bean.toString());
    }
    
    protected ReductorBean getReductorBean() {
        
        return bean;
    }
    
    public void putControlAction(String action) {

        try {            
            MapMessage mapMessage = getSession().createMapMessage();
            mapMessage.setStringProperty("peer",bean.sessionId);
            mapMessage.setString("action",action);
            mapMessage.setString("peerId",bean.peerId);
            send("resolve-reduce",mapMessage,DeliveryMode.NON_PERSISTENT,
                                            HIGH_PRIORITY,MESSAGE_LIFESPAN);
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }            
    }

    public void putMonitorAction(String action) {

        try {            
            MapMessage mapMessage = getSession().createMapMessage();
            mapMessage.setStringProperty("peer","monitor");
            mapMessage.setString("action",action);
            mapMessage.setString("peerId",bean.peerId);
            send("resolve-reduce",mapMessage,DeliveryMode.NON_PERSISTENT,
                                            HIGH_PRIORITY,MESSAGE_LIFESPAN);
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }            
    }

    public void putSolveMap() {
          
        bean.fillSolveMap();
        for( String peerId: bean.solvant.keySet() )
            putSolveMap(peerId);    
    }
        
    private void putSolveMap(String toPeerId) {
            
        try {
            String action = bean.peerId.equals(toPeerId) ? "monitor" : "resolve";
            MapMessage mapMessage = getSession().createMapMessage();
            mapMessage.setStringProperty("peer",toPeerId);
            mapMessage.setString("gameId",bean.gameId);
            mapMessage.setString("peerId",bean.peerId);
            mapMessage.setInt("trialId",bean.trialId);                
            mapMessage.setString("action",action);
            mapMessage.setString("solved", bean.getSolvent(toPeerId));
            send("resolve-reduce",mapMessage,DeliveryMode.NON_PERSISTENT,
                                    Message.DEFAULT_PRIORITY,MESSAGE_LIFESPAN);
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }
    }

}
