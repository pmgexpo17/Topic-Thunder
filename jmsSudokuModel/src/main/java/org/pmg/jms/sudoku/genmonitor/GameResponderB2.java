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
package org.pmg.jms.sudoku.genmonitor;

import com.google.inject.Inject;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import org.pmg.jms.genclient.ClientResponder;
import org.pmg.jms.genconnect.OpenWire;
import org.pmg.jms.genconnect.SessionProvider;

/**
 * ClientReponder provides multiple JMS destination capacity, where destKey is 
 * mapped to a JMS producer for message delivery
 * @author peter
 */
public class GameResponderB2 extends ClientResponder {

    private static final int MESSAGE_LIFESPAN = 3000;  // 3 seconds
    private static final int MEDIUM_PRIORITY = 7;
    private static final int HIGH_PRIORITY = 9;

    @Inject
    public GameResponderB2(@OpenWire SessionProvider sessionPrvdr) {
        
        super(sessionPrvdr);
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
