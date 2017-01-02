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
package org.pmg.jms.gensudoku.server;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import org.pmg.jms.genclient.ClientResponder;
import org.pmg.jms.genclient.Routable;
import org.pmg.jms.genconnect.OpenWireSessionPrvdr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class NextGameResponder extends ClientResponder implements Runnable {
    private static final Logger LOG = 
                               LoggerFactory.getLogger(NextGameResponder.class);    
    private static final int MESSAGE_LIFESPAN = 3000;// milliseconds (3 seconds)
    private static final int HIGH_PRIORITY = 9;
    private final String sessionId;
    private final int sessionNum;
    private volatile int sequenceNum = 0;
    private volatile String gameId;
    private String clientId;
    private Routable delegate;

    @Inject
    public NextGameResponder(OpenWireSessionPrvdr sessionProvider,
                                                  @Assisted String sessionId) {
        super(sessionProvider);
        this.sessionId = sessionId;
        sessionNum = 
           Integer.valueOf(sessionId.substring(sessionId.indexOf("session")+7));
    }

    public void configure() throws JMSException {

        String queueName = "queue://PEER.CONTROL/game/service/" + sessionId;
        createProducer("control",queueName);
        LOG.info("[{}] configured, sessionId : {}",className,sessionId);
    }
    
    public void cancelTimeout() throws JMSException {
        
        ScheduledFuture future = 
                         (ScheduledFuture) delegate.getObject("future");
        future.cancel(true);
        LOG.info("[{}] game[{}] timeout future is cancelled",className,gameId);
        long difference = System.nanoTime() - 
                                (Long) delegate.getObject("startTime");
        long secs = TimeUnit.NANOSECONDS.toSeconds(difference);
        long millisecs = TimeUnit.NANOSECONDS.toMillis(difference) - 
                                                TimeUnit.SECONDS.toMillis(secs);        
        LOG.info("[{}] Game {} run time : {}.{} secs",className,gameId,
                                                                secs,millisecs);
    }
    
    private String getNextGameId() {
        
        sequenceNum = sequenceNum == 500 ? 1 : sequenceNum + 1;                
        return String.format(("%d%03d"),sessionNum,sequenceNum);
    }

    public synchronized void startGame(Routable delegate) {
            
        try {
            gameId = getNextGameId();            
            LOG.info("[{}] notify controller to start game[{}]",className,
                                                                    gameId);
            this.delegate = delegate;
            this.clientId = delegate.getString("clientId");
            MapMessage mapMessage = getSession().createMapMessage();
            mapMessage.setStringProperty("peer",sessionId);
            mapMessage.setString("clientId",clientId);
            mapMessage.setString("action","preset");
            mapMessage.setString("gameId",gameId);
            mapMessage.setObject("startMap", delegate.getObject("startMap"));
            Queue replyTo = (Queue) delegate.getJMSReplyTo();
            mapMessage.setJMSReplyTo(replyTo);
            send("control",mapMessage,DeliveryMode.NON_PERSISTENT,
                                                HIGH_PRIORITY,MESSAGE_LIFESPAN);
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }
    }

    @Override
    public void run() {
            
        try {
            LOG.info("[{}] timeout expired, game terminated",className);
            LOG.info("[{}] notify controller to stop game[{}]",
                                                            className,gameId);
            MapMessage mapMessage = getSession().createMapMessage();
            mapMessage.setStringProperty("peer",sessionId);
            mapMessage.setString("clientId",clientId);
            mapMessage.setString("action","stop");
            send("control",mapMessage,DeliveryMode.NON_PERSISTENT,
                                                HIGH_PRIORITY,MESSAGE_LIFESPAN);
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }
    }    
}
