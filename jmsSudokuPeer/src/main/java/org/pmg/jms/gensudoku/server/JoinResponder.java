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
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import org.pmg.jms.genclient.ClientResponder;
import org.pmg.jms.genclient.Routable;
import org.pmg.jms.genconnect.OpenWire;
import org.pmg.jms.genconnect.SessionProvider;
import org.pmg.jms.genhandler.Handler;

/**
 * If the webClient provided startMap is valid, calls the game server to provide
 * a sudoku game and solution 
 * @author peter
 */
public class JoinResponder extends ClientResponder implements Handler {
 
    private static final int MESSAGE_LIFESPAN = 3000;  // 3 seconds
    private GameService gameServer;

    @Inject
    public JoinResponder(@OpenWire SessionProvider sessionPrvdr) {        
        
        super(sessionPrvdr);
    }
    
    public void setGameServer(GameService gameServer) {
        
        this.gameServer = gameServer;
    }

    @Override
    public synchronized void handle(Routable delegate) throws JMSException {

        String action = delegate.getString("action");
        LOG.info("[{}] got message, action : {}",className,action);
        switch (action) {
            case "join" :
                LOG.info("[{}] got join request",className);
                JoinAdmittal admittal = new JoinAdmittal(delegate);
                if (admittal.startMapIsValid(delegate))
                    gameServer.join(delegate);
                else
                    reportJoinError(delegate,admittal);
                break;                    
            default : break;
        }
        delegate.setStatus(true);
    }

    public void reportJoinError(Routable delegate, JoinAdmittal admittal) {
            
        try {
            TextMessage txtMessage = getSession().createTextMessage();
            txtMessage.setStringProperty("state","complete");
            String xmlText = admittal.getXmlErrorText();
            txtMessage.setText(xmlText);
            Queue replyQueue = (Queue) delegate.getJMSReplyTo();
            setReplyToDest("play",replyQueue);                
            send("join",txtMessage,DeliveryMode.NON_PERSISTENT,
                                    Message.DEFAULT_PRIORITY,MESSAGE_LIFESPAN);
            String logText = "[{}] reporting join error to client[{}]";
            LOG.info(logText,className,admittal.clientId);            
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }            
    }        

    @Override
    public void destroy() {}        
}
