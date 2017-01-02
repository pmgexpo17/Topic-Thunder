/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genmonitor;

import com.google.inject.Inject;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import org.pmg.jms.genclient.ClientResponder;
import org.pmg.jms.genconnect.OpenWireSessionPrvdr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class GameResponderB2 extends ClientResponder {

    private static final Logger LOG = 
                                LoggerFactory.getLogger(GameResponderB2.class);
    private static final int MESSAGE_LIFESPAN = 3000;  // milliseconds (3 seconds)
    private static final int MEDIUM_PRIORITY = 7;
    private static final int HIGH_PRIORITY = 9;

    @Inject
    public GameResponderB2(OpenWireSessionPrvdr sessionProvider) {
        
        super(sessionProvider);
    }

    public void putRetroIndex(String peerId, int retroIndex) {

        try {
            LOG.debug("[{}] deliver rollback index to game director[{}]",
                                                    className,peerId);
            MapMessage mapMessage = getSession().createMapMessage();
            mapMessage.setObjectProperty("peer",peerId);
            mapMessage.setString("action","rollback");
            mapMessage.setString("peerId","monitor");
            mapMessage.setInt("retroIndex", retroIndex);
            send("resolve-reduce",mapMessage,DeliveryMode.NON_PERSISTENT,
                                            HIGH_PRIORITY,MESSAGE_LIFESPAN);
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }            
    }

    public void putTrialOption(String sessionId,TrialOption topSeed) {
            
        LOG.debug("[{}] trial option for {} : {}",className,
                                        topSeed.peerId,topSeed.solvent);
        for( String peerId: topSeed.allPeers )
            putTrialOption(peerId,topSeed.peerId,topSeed.solvent,"start");
        putTrialOption(sessionId,topSeed.peerId,topSeed.solvent,"start");
    }

    public void putTrialOption(String peerId,String trialPeer,
                                                String solvent,String action) {
        try {
            MapMessage mapMessage = getSession().createMapMessage();
            mapMessage.setObjectProperty("peer",peerId);
            mapMessage.setString("action",action);
            mapMessage.setString("peerId",trialPeer);
            mapMessage.setObject("solved",solvent);                
            send("resolve-reduce",mapMessage,DeliveryMode.NON_PERSISTENT,
                                            MEDIUM_PRIORITY,MESSAGE_LIFESPAN);
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }                        
    }

    public void putControlAction(String peerId, String action) {
           
        /* 
         - There are 2 destination options
         - 1. selector param peerId is blank : resolve-control
         - 2. selector param peerId is not blank : resolve-reduce
        */
        try {
            LOG.debug("[{}] action[{}] delivery to game peer[{}]",
                                              className,action,peerId);
            MapMessage mapMessage = getSession().createMapMessage();
            String destKey = "resolve-control";
            if (!peerId.isEmpty()) {
                mapMessage.setObjectProperty("peer",peerId);
                destKey = "resolve-reduce";
            }
            mapMessage.setString("peerId","monitor");
            mapMessage.setString("action",action);
            send(destKey,mapMessage,DeliveryMode.NON_PERSISTENT,
                                            HIGH_PRIORITY,MESSAGE_LIFESPAN);
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }            
    }
    
}
